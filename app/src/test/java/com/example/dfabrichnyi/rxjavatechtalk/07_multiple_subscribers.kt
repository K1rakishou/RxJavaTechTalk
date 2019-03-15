import io.reactivex.Observable
import org.junit.Test

class `07_multiple_subscribers` {

    /**
     * У одного обзёрвабла может быть несколько сабскрайберов. Но тут есть подводный камень.
     *
     * Допустим у нас есть блокирующая операция. Мы хотим один раз её вызвать и потом разослать
     * результат нескольким сабскрайберам. Однако, не всё так просто.
     * */
    @Test
    fun test1() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(2000)
    }





























































    /**
     * Во многих туториалах советуют в данном случае использовать ператор share() да и судя из
     * названия можно сделать вывод что он должен помочь нам в этой ситуации. Но это не так.
     * */
    @Test
    fun test1_2() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.share()

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(2000)
    }






























































    /**
     * Оператор share() это сокращение от publish().refCount(1), где параметр у refCount это кол-во
     * сабскрайберов которых он будет ожидать прежде чем запустить стрим. И так как сабскрайберов
     * у нас три - работать share() не будет. Пофиксить это можно напрямую заюзав publish().refCount(3)
     *
     * Сам оператор refCount (Reference Counting) ждёт когда на стрим подпишется данное кол-во
     * подписчиков и "коннектится" к стриму, и дисконнектится от него когда кол-во подписчиков
     * становится равно нулю.
     *
     * Соответственно коннект/дисконнект сигнализирует стриму о том, что он должен начать/прекратить
     * эмититить ивенты (не тоже самое, что отписка от стрима!!!)
     * */
    @Test
    fun test1_3() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.publish().refCount(3)

        blockingOperationObservable
          .subscribe({ value -> println(value) })

        blockingOperationObservable
          .subscribe({ value -> println(value) })

        blockingOperationObservable
          .subscribe({ value -> println(value) })

        Thread.sleep(2000)
    }







































































    /**
     * Можно так же использовать оператор cache. Внутри, оператор cache использует LinkedList,
     * куда будут сохраняться любые значения которые через него проходят (и его нельзя очистить!!!,
     * только если отпиской), тем самым он может стать причиной ООМ. Cache не подходит в тех случаях,
     * когда нужно кешировать всего одно значение, потому что cache кеширует вообще всё
     * (особенно опасно юзать cache в сочетании с бесконечными стримами (subjects)).
     * */
    @Test
    fun test2() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.cache()

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }











































    /**
     * Или оператор replay с размером буфера равным единице.
     * Replay будет перезаписывать закешированное значение, что позволяет нам избежать
     * ООМ в отличии от cache.
     * */
    @Test
    fun test3() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.replay(1).autoConnect(3)

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }











































    /**
     * А можно вообще использовать 0 в качестве параметра для метода autoConnect и это будет означать,
     * что обзёрвабл сохранит последнее значение, даже если нет подписчиков, вместо того,
     * чтобы закрыть стрим.
     * */
    @Test
    fun test3_1() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.replay(1).autoConnect(0)

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }









































    /**
     * Так же можно использовать publish/connect
     *
     * В данном случае мы вручную вызываем метод connect после того как нужные нам подписчики были
     * подписаны на стрим.
     * */
    @Test
    fun test4() {
        val blockingOperationConnectableObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.publish()

        blockingOperationConnectableObservable
                .subscribe({ value -> println(value) })

        blockingOperationConnectableObservable
                .subscribe({ value -> println(value) })

        blockingOperationConnectableObservable
                .subscribe({ value -> println(value) })

        blockingOperationConnectableObservable.connect()

        Thread.sleep(700)
    }
}























