import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class `02_rxjava_is_synchronous_by_default` {

    /**
     * Реактивные стримы синхронны по-умолчанию. Это означает, что все вычисления происходят в том потоке,
     * из которого стрим был вызван (исключения составляют операторы по-умолчанию работающие на каком-то шедулере,
     * например timer или interval)
     * */
    @Test
    fun test_print_current_thread_name() {
        Observable.range(0, 5)
                .doOnNext { println("Current thread = ${Thread.currentThread().name}") }
                .subscribe()
    }

    /**
     * В данном примере можно увидеть, что после применения шедулера, все элементы стрима выолняются на одном потоке
     * хотя в по-идее должны выполняться каждый на своём потоке. Даже если поменять subscribeOn на observeOn -
     * ничего не изменится. Полагаю, что это происходит из-за того, что рабочий поток в шедулере назначается только
     * 1 раз при создании стрима
     * */
    @Test
    fun test_concurrency_not_working() {
        Observable.range(0, 5)
                .subscribeOn(Schedulers.computation())
//                .observeOn(Schedulers.computation())
                .doOnNext { println("1: Current thread = ${Thread.currentThread().name}") }
                .flatMap {
                    return@flatMap Observable.just(it)
                            .doOnNext { println("2: Current thread = ${Thread.currentThread().name}") }
                }
                .doOnNext { println("3: Current thread = ${Thread.currentThread().name}") }
                .subscribe()

        Thread.sleep(100)
    }

    /**
     * Чтобы выполнить параллельные запросы, нужно перенести применение шедулера во внутренний стрим.
     * Как можно заметить, шедулер сохраняется даже после возвращения из внутреннего стрима во внешний
     * */
    @Test
    fun test_concurrency_working() {
        Observable.range(0, 5)
                .doOnNext { println("1: Current thread = ${Thread.currentThread().name}") }
                .flatMap {
                    return@flatMap Observable.just(it)
                            .subscribeOn(Schedulers.computation())
                            .doOnNext { println("2: Current thread = ${Thread.currentThread().name}") }
                }
                .doOnNext { println("3: Current thread = ${Thread.currentThread().name}") }
                .subscribe()

        Thread.sleep(100)
    }
}