import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.rxkotlin.zipWith
import org.junit.Test
import java.util.concurrent.TimeUnit

class `13_reactive_streams_combining` {

    /**
     * Оператор zip комбинирует ивенты из разных апстримов в один ивент и пускает его дальше
     * вниз по стриму. Оператоп zip завершает свою работу как только один из стримов присылает
     * терминальный ивент (complete/error). Если один из стримов медленнее другого - оператор zip
     * будет буферизировать все ивенты из более быстрого стрима (потенциальные ООМ).
     *
     * */
    @Test
    fun test1() {
        val timer1 = Observable.interval(100, TimeUnit.MILLISECONDS)
        val timer2 = Observable.interval(400, TimeUnit.MILLISECONDS)

        Observables.zip(timer1, timer2)
                .subscribe({ value -> println("timer1 = ${value.first}, timer2 = ${value.second}") })

        Thread.sleep(5000)
    }

    /**
     * Похожий на оператор zip оператор combineLatest кеширует последний пришедший ивент из
     * каждого стрима и далее может использовать кешированные ивенты (обновляя их)
     * */
    @Test
    fun test2() {
        val timer1 = Observable.interval(100, TimeUnit.MILLISECONDS)
        val timer2 = Observable.interval(400, TimeUnit.MILLISECONDS)

        Observables.combineLatest(timer1, timer2)
                .subscribe({ value -> println("timer1 = ${value.first}, timer2 = ${value.second}") })

        Thread.sleep(5000)
    }

    /**
     * Оператор withLatestFrom предназначен для комбинирования более медленного стрима с
     * более быстрым. Работает похоже на combineLatest с отличием в том, что кешируются ивенты
     * только из одного стрима
     * */
    @Test
    fun test3() {
        val timer1 = Observable.interval(100, TimeUnit.MILLISECONDS)
        val timer2 = Observable.interval(400, TimeUnit.MILLISECONDS)

        timer2.withLatestFrom(timer1)
                .subscribe({ value -> println("timer2 = ${value.first}, timer1 = ${value.second}") })

        Thread.sleep(5000)
    }
}



















