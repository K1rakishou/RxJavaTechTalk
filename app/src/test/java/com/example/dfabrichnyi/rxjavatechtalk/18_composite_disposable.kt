import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.zipWith
import org.junit.Test
import java.util.concurrent.TimeUnit

class `18_composite_disposable` {

    /**
     * Clear отписывается от каждого сохранённого disposable и очищает свой внутренний список,
     * если после этого добавить новый disposable, то он будет работать
     *
     * Dispose отписывается от каждого сохранённого disposable и очищает свой внутренний список,
     * если после этого добавить новый disposable (через add()), CompositeDisposable
     * его не только не добавит, но и тут же отпишется от него
     * */
    @Test
    fun test() {
        val cd = CompositeDisposable()

        val d1 = Observable.just(Unit)
                .zipWith(Observable.timer(100, TimeUnit.MILLISECONDS))
                .subscribe()
        cd += d1

        println("d1 isDisposed = ${d1.isDisposed}")
        cd.clear()
        println("d1 isDisposed = ${d1.isDisposed}")

        val d2 = Observable.just(Unit)
                .zipWith(Observable.timer(100, TimeUnit.MILLISECONDS))
                .subscribe()
        cd += d2

        println("d2 isDisposed = ${d2.isDisposed}")
        cd.dispose()
        println("d2 isDisposed = ${d2.isDisposed}")

        val d3 = Observable.just(Unit)
                .zipWith(Observable.timer(100, TimeUnit.MILLISECONDS))
                .subscribe()

        cd += d3
        println("d3 isDisposed = ${d3.isDisposed}")
    }
}