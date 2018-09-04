import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit

class `06_multiple_subscribers` {

    /**
     * У обзёрвабла может быть несколько сабскрайберов. Но тут есть подводный камень.
     * Сколько раз будет выполнен код внутри fromCallable?
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
     * Многие ошибочно считают, что для решения этой проблемы существует оператор share
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
     * Избежать этого можно заюзав оператор cache. Внутри, оператор cache использует LinkedList,
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
     * Так же можно заюзать оператор replay с размером буфера равным единице.
     * Replay будет перезаписывать закешированное значение, что позволяет нам избежать
     * ООМ в отличии от cache.
     *
     * Или заюзать cacheLast из либы RxJava Extensions
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
     * А можно вообще задать 0 в параметр метода autoConnect и это будет означать, что обзёрвабл
     * сохранит последнее значение, даже если нет подписчиков, вместо того, чтобы закрыть стрим.
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

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }









































    /**
     * Так же можно заюзать publish/connect для создания горячего обзёрвабла который потом можно
     * мультикастнуть в несколько сабскрайберов
     * */
    @Test
    fun test4() {
        val blockingOperationObservable = Observable.fromCallable {
            println("Some blocking operation")

            Thread.sleep(500)
            "done"
        }.publish().autoConnect(3)

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        blockingOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }
}























