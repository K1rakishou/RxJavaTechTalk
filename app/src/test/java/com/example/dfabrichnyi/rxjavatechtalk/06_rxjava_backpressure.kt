import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit

class `06_rxjava_backpressure` {

    class Data(var id: Int,
               var buffer: ByteBuffer = ByteBuffer.allocate(1024 * 1024)) //1MB

    /**
     * Backpressure - это механизм общения даунстрима и апстрима. У flowable есть собственный тип сабскрайбера -
     * FlowableSubscriber у которого есть Subscription (в отличии от Disposable у других типов) и у него есть метод request.
     * Этот метод передаёт в апстрим количество элементов которое просит даунстрим. Каждый оператор в flowable
     * обрабатывает этот метод из-за этого backpressure работает для flowable по-умолчанию (в отличии от других типов)
     * */
    @Test
    fun testOk() {
        Flowable.range(0, 1_000_000_000)
                .map { Data(it) }
                .subscribe({
                    println("id = ${it.id}")
                    Thread.sleep(50)
                }, { error -> error.printStackTrace() })
    }















































    /**
     * Отключим Backpressure и удивимся, что всё по прежнему работает.
     * Затем вспомним, что реактивные стримы синхронны по-умолчанию
     * (onNext не вызовется до тех пор, пока не отработает sleep)
     * */
    @Test
    fun testStillOk() {
        Flowable.create<Int>({ emitter ->
            for (i in 0..1_000_000_000) {
                if (!emitter.isCancelled) {
                    emitter.onNext(i)
                }
            }

            emitter.onComplete()
        }, BackpressureStrategy.MISSING)
        .map { Data(it) }
        .subscribe({
            println("id = ${it.id}")
            Thread.sleep(50)
        }, { error -> error.printStackTrace() })
    }


















































    /**
     * Добавим шедулер и наконец узреем MissingBackpressureException
     * */
    @Test
    fun testNotOk() {
        Flowable.create<Int>({ emitter ->
            for (i in 0..1_000_000_000) {
                if (!emitter.isCancelled) {
                    emitter.onNext(i)
                }
            }

            emitter.onComplete()
        }, BackpressureStrategy.MISSING)
        .map { Data(it) }
        .observeOn(Schedulers.io())
        .subscribe({
            println("id = ${it.id}")
            Thread.sleep(50)
        }, { error ->
            error.printStackTrace()
        })
    }

    /**
     * Observable не имеет встроенного механизма backpressure
     * по-этому стрим будет работать вплоть до ООМ
     * */
    @Test
    fun testObservable() {
        Observable.create<Int> { emitter ->
            for (i in 0..1_000_000_000) {
                if (!emitter.isDisposed) {
                    emitter.onNext(i)
                }
            }

            emitter.onComplete()
        }
        .map { Data(it) }
        .observeOn(Schedulers.io())
        .subscribe({
            println("id = ${it.id}")
            Thread.sleep(50)
        }, { error ->
            error.printStackTrace()
        })
    }









































    /**
     * Ещё один пример того, что backpressure есть во-всех операторах flowable
     * Вопрос - как часто будет вызываться метод subscribe?
     * */
    @Test
    fun testInterval() {
        Flowable.interval(10, TimeUnit.MILLISECONDS)
                .subscribe({
                    println("time: ${Date().time}, id = ${it}")
                    Thread.sleep(1000)
                })

        Thread.sleep(15000)
    }
}