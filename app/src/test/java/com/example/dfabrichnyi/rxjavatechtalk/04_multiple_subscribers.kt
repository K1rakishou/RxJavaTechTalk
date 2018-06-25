import io.reactivex.Observable
import org.junit.Test

class `04_multiple_subscribers` {

    /**
     * У обзёрвабла может быть несколько сабскрайберов. Но тут есть подводный камень.
     * С каждым новым сабскрайбером, весь реактивный стрим будет выполняться с самого начала.
     * */
    @Test
    fun test1() {
        val longOperationObservable = Observable.fromCallable {
            println("Doing some heavy work")

            Thread.sleep(500)
            "done"
        }.share()

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(2000)
    }













































    /**
     * Избежать этого можно заюзав оператор cache. Но кеш будет кешировать всё,
     * что через него проходит, тем самым может стать причиной ООМ
     * */
    @Test
    fun test2() {
        val longOperationObservable = Observable.fromCallable {
            println("Doing some heavy work")

            Thread.sleep(500)
            "done"
        }.cache()

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }












































    /**
     * Так же можно использовать ConnectableObservable, которое заюзает мультикастинг
     * */
    @Test
    fun test3() {
        val longOperationObservable = Observable.fromCallable {
            println("Doing some heavy work")

            Thread.sleep(500)
            "done"
        }.publish()

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable.connect()

        Thread.sleep(700)
    }

    /**
     * Второй вариант с autoConnect
     * */
    @Test
    fun test3_1() {
        val longOperationObservable = Observable.fromCallable {
            println("Doing some heavy work")

            Thread.sleep(500)
            "done"
        }.publish().autoConnect(3)

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }
}