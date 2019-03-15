import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class `11_rxjava_backpressure` {

    /**
     * Backpressure возникает когда в реактивном стриме какой-то из операторов не успевает обработать
     * элементы достаточно быстро и ему необходимо сообщить вышестоящим операторам эмитить их по медленее.
     * Backpressure есть только у Flowable.
     *
     * Некоторые операторы Observable при возникновении ситуации, когда они не успевают обрабатывать
     * элементы, будут складывать их во внутреннюю очередь, которая, например, в первой
     * RxJava является неограниченной. Во второй же эта очередь уже не бесконечна, а имеет дефолтный
     * размер 128 элементов.
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
                .observeOn(Schedulers.io())
                .subscribe({
                    println(it)
                    Thread.sleep(50)
                }, { error ->
                    error.printStackTrace()
                })
    }


    @Test
    fun testOk() {
        Flowable.create<Int>({ emitter ->
            for (i in 0..1_000_000_000) {
                if (!emitter.isCancelled) {
                    emitter.onNext(i)
                }
            }

            emitter.onComplete()
        }, BackpressureStrategy.DROP)
                .observeOn(Schedulers.io())
                .subscribe({
                    println(it)
                    Thread.sleep(50)
                }, { error ->
                    error.printStackTrace()
                })

        Thread.sleep(3000)
    }

    /**
     * Однако, backpressure по-умолчанию не работает, когда у стрима есть вложенный стрим который
     * выполняется на другом шедулере
     * */
    @Test
    fun testInterval_mbe() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
                .flatMap {
                    return@flatMap Flowable.just(it)
                            .subscribeOn(Schedulers.io())
                            .doOnNext { println("Thread = ${Thread.currentThread().name}") }
                }
                .subscribe({
                    println("time: ${Date().time}, id = ${it}")
                    Thread.sleep(1000)
                })

        Thread.sleep(5000)
    }

    /**
     * Чтобы это починить, можно воспользоваться операторами onBackpressure***
     * */
    @Test
    fun testInterval_fixed() {
        Flowable.interval(1, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .flatMap {
                    return@flatMap Flowable.just(it)
                            .subscribeOn(Schedulers.io())
                            .doOnNext { println("Thread = ${Thread.currentThread().name}") }
                }
                .subscribe({
                    println("time: ${Date().time}, id = ${it}")
                    Thread.sleep(10)
                })

        Thread.sleep(5000)
    }

}