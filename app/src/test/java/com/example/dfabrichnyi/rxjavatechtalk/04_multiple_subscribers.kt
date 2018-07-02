import io.reactivex.Observable
import org.junit.Test

class `04_multiple_subscribers` {

    /**
     * У обзёрвабла может быть несколько сабскрайберов. Но тут есть подводный камень.
     * Сколько раз будет выполнен код внутри лямбды?
     * */
    @Test
    fun test1() {
        val longOperationObservable = Observable.fromCallable {
            println("Doing some heavy work")

            Thread.sleep(500)
            "done"
        }

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
}