package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.internal.fuseable.HasUpstreamObservableSource
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test

class `00_rxjava_flow_example` {

    //TODO: разница между subscribeOn/observerOn

    /**
     * У обзерваблов есть upstream и downstream
     * Subscribe вызывает метод subscribeActual у вышестоящего оператора,
     * тот в свою очередь вызывает subscribeActual у другого вышестоящего и так до тех пор,
     * пока дело не дойдёт до так называемого ObservableSource (just в данном случае.
     * Другие примеры сорсов - fromCallable, fromIterable, и тд)
     *
     * Далее just вызывает onSubscribe у нижестоящего оператора (циклично до конца) (создавая и передавая диспосабл)
     * и затем передаёт значение через onNext и терминальный ивент через onComplete
     *
     * */
    @Test
    fun test() {
        Observable.just(1)
                .testOperator("operator1")
                .testOperator("operator2")
                .testOperator("operator3")
                .subscribe()
    }

    private fun <T> Observable<T>.testOperator(tag: String): Observable<T> {
        return RxJavaPlugins.onAssembly(TestOperator(this, tag))
    }

    class TestOperator<T>(
            private val source: ObservableSource<T>,
            private val tag: String
    ) : Observable<T>(), HasUpstreamObservableSource<T> {

        override fun source(): ObservableSource<T> = source

        override fun subscribeActual(observer: Observer<in T>) {
            println("$tag: subscribeActual")
            source.subscribe(TestOperatorObserver(observer, tag))
        }

        class TestOperatorObserver<T>(
                private val actual: Observer<in T>,
                private val tag: String
        ) : Observer<T>, Disposable {

            var done: Boolean = false
            var disposable: Disposable? = null

            override fun onSubscribe(disposable: Disposable) {
                this.disposable = disposable
                println("$tag: onSubscribe")
                actual.onSubscribe(this)
            }

            override fun onNext(element: T) {
                if (!done) {
                    println("$tag: onNext")
                    actual.onNext(element)
                }
            }

            override fun onError(error: Throwable) {
                if (!done) {
                    done = true
                    println("$tag: onError")
                    actual.onError(error)
                } else {
                    RxJavaPlugins.onError(error)
                }
            }

            override fun onComplete() {
                if (!done) {
                    done = true
                    println("$tag: onComplete")
                    actual.onComplete()
                }
            }

            override fun isDisposed(): Boolean {
                return disposable?.isDisposed ?: true
            }

            override fun dispose() {
                disposable?.dispose()
            }

        }
    }
}