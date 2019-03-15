import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class `08_sharing_is_caring` {

    /**
     * Зачем же тогда нужен оператор share() ?
     * share позволяет поделиться подпиской нескольким подписчикам. Т.е. подписка создастся при
     * первом коннекте и будет жить до тех пор пока есть хотя бы один подписчик.
     * */
    @Test
    fun test_problem() {
        val index = AtomicInteger(0)
        val timer = Observable.interval(500, TimeUnit.MILLISECONDS)
                .map { index.getAndIncrement() }

        val d1 = timer
          .doOnDispose { println("Disposed of the first\n\n") }
          .subscribe({ value -> println("1: index = $value") })
        val d2 = timer
          .doOnDispose { println("Disposed of the second\n\n") }
          .subscribe({ value -> println("2: index = $value") })
        val d3 = timer
          .doOnDispose { println("Disposed of the third\n\n") }
          .subscribe({ value -> println("3: index = $value") })

        Thread.sleep(1000)

        println("Disposing of the first")
        d1.dispose()
        Thread.sleep(1000)

        println("Disposing of the second")
        d2.dispose()
        Thread.sleep(1000)

        println("Disposing of the last one")
        d3.dispose()
        Thread.sleep(1000)
    }













































    /**
     * Оператор share решает данную проблему. К слову любой другой метод мультикастинга тоже будет
     * работать в данном случае.
     * */
    @Test
    fun test_solution() {
        val index = AtomicInteger(0)
        val timer = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map { index.getAndIncrement() }
                .share()

        val d1 = timer
          .doOnDispose { println("Disposed of the first\n\n") }
          .subscribe({ value -> println("1: index = $value") })
        val d2 = timer
          .doOnDispose { println("Disposed of the second\n\n") }
          .subscribe({ value -> println("2: index = $value") })
        val d3 = timer
          .doOnDispose { println("Disposed of the third\n\n") }
          .subscribe({ value -> println("3: index = $value") })

        Thread.sleep(1000)

        println("Disposing of the first")
        d1.dispose()
        Thread.sleep(1000)

        println("Disposing of the second")
        d2.dispose()
        Thread.sleep(1000)

        println("Disposing of the last one")
        d3.dispose()
        Thread.sleep(1000)
    }
}