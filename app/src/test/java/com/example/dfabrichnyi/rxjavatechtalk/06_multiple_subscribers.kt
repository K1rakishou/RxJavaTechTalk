import io.reactivex.Observable
import org.junit.Test

class `06_multiple_subscribers` {

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
     * Многие могут ошибочно посчитать, что для решения этой проблемы существует оператор share, но это не так
     * */
    @Test
    fun test1_2() {
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
     * Избежать этого можно заюзав оператор cache. Внутри, оператор cache использует LinkedList,
     * куда будут сохраняться любые значения которые через него проходят (и его нельзя очистить),
     * тем самым он может стать причиной ООМ. К тому же cache не подходит когда нужно кешировать всего
     * одно значение, потому что cache кеширует вообще всё (особенно опасно юзать cache в сочетании с
     * бесконечными стримами (subjects)).
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
     * Так же можно заюзать оператор replay с буфером раным единице.
     * Replay будет перезаписывать закешированное значение, что позволяет нам избежать ООМ в отличии от cache
     * */
    @Test
    fun test3() {
        val longOperationObservable = Observable.fromCallable {
            println("Doing some heavy work")

            Thread.sleep(500)
            "done"
        }.replay(1).autoConnect(3)

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        longOperationObservable
                .subscribe({ value -> println(value) })

        Thread.sleep(700)
    }
}















