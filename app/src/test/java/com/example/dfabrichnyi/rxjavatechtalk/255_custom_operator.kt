import io.reactivex.Observable
import io.reactivex.ObservableOperator
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import org.junit.Test

class `255_custom_operator` {

    @Test
    fun testCustomOperator() {
        Observable.range(0, 1000000)
                .take(111)
                .myFilter { value -> value % 3 == 0 && value % 5 == 0 }
                .myMap { value -> "Transformed $value" }
                .subscribe({ next ->
                    println("value = $next")
                })
    }

    class MyFilter<T>(private val predicate: (input: T) -> Boolean)
        : ObservableOperator<T, T> {

        override fun apply(actual: Observer<in T>): Observer<in T> {
            return FilterEvensObserver(actual, predicate)
        }

        class FilterEvensObserver<T>(
                private val actual: Observer<in T>,
                private val predicate: (input: T) -> Boolean
        ) : Observer<T>, Disposable {

            var done: Boolean = false
            var disposable: Disposable? = null

            override fun onSubscribe(disposable: Disposable) {
                this.disposable = disposable
                actual.onSubscribe(this)
            }

            override fun onNext(element: T) {
                if (!done) {
                    if (predicate(element)) {
                        actual.onNext(element)
                    }
                }
            }

            override fun onError(error: Throwable) {
                if (!done) {
                    done = true
                    actual.onError(error)
                } else {
                    RxJavaPlugins.onError(error)
                }
            }

            override fun onComplete() {
                if (!done) {
                    done = true
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

    class MyMap<Upstream, Downstream>(private val transformerFunction: (input: Upstream) -> Downstream)
        : ObservableOperator<Downstream, Upstream> {

        override fun apply(actual: Observer<in Downstream>): Observer<Upstream> {
            return TestObserver(actual, transformerFunction)
        }

        class TestObserver<Upstream, Downstream>(
                private val actual: Observer<in Downstream>,
                private val transformerFunction: (input: Upstream) -> Downstream
        ) : Observer<Upstream>, Disposable {

            var done: Boolean = false
            var disposable: Disposable? = null

            override fun onSubscribe(disposable: Disposable) {
                this.disposable = disposable
                actual.onSubscribe(this)
            }

            override fun onNext(element: Upstream) {
                if (!done) {
                    actual.onNext(transformerFunction(element))
                }
            }

            override fun onError(error: Throwable) {
                if (!done) {
                    done = true
                    actual.onError(error)
                } else {
                    RxJavaPlugins.onError(error)
                }
            }

            override fun onComplete() {
                if (!done) {
                    done = true
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

    private fun <T> Observable<T>.myFilter(
            predicate: (input: T) -> Boolean
    ): Observable<T> {
        return this.lift(MyFilter<T>(predicate))
    }

    private fun <Upstream, Downstream> Observable<Upstream>.myMap(
            transformerFunction: (input: Upstream) -> Downstream
    ): Observable<Downstream> {
        return this.lift(MyMap<Upstream, Downstream>(transformerFunction))
    }
}