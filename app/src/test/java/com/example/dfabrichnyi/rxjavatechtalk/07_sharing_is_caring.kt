import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class `07_sharing_is_caring` {

    /**
     * Оператор share решает проблему мультикастинга обзервабла нескольким сабскрайберам.
     * Допустим, нам надо рассылать обзёрвабл нескольким сабскрайберам. По-умолчанию такое невозможно.
     * */
    @Test
    fun test_problem() {
        val index = AtomicInteger(0)
        val timer = Observable.interval(500, TimeUnit.MILLISECONDS)
                .map { index.getAndIncrement() }

        timer.subscribe({ value -> println("1: index = $value") })
        timer.subscribe({ value -> println("2: index = $value") })
        timer.subscribe({ value -> println("3: index = $value") })

        Thread.sleep(3000)
    }













































    /**
     * Оператор share решает данную проблему
     * */
    @Test
    fun test_solution() {
        val index = AtomicInteger(0)
        val timer = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map { index.getAndIncrement() }
                .share()

        timer.subscribe({ value -> println("1: index = $value") })
        timer.subscribe({ value -> println("2: index = $value") })
        timer.subscribe({ value -> println("3: index = $value") })

        Thread.sleep(3000)
    }
}