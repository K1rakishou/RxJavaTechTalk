import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class `12_schedulers` {

    /**
     * Разница между subscribeOn и observerOn.
     * subscribeOn указывает на каком шедулере будет выполнен observable source и весь стрим после него
     * (либо до следующего observeOn) в независимости от того, в каком месте в цепочке операторов он
     * был вызван.
     * SubscribeOn шедулит обзёрвабл в subscribeActual, observeOn в onNext
     * */

    /** Что будет если попытаться вызвать несколько subscribeOm подряд? */
    @Test
    fun testMultipleSubscribeOn() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.computation())
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }


     /**
     * observeOn указывает на каком шедулере будут выполнены операторы стоящии ниже по стриму,
     * либо до следующего observeOn
     * */

     @Test
     fun testMultipleObserveOn() {
         Observable.just(0)
                 .doOnNext { println("value: $it, thread: ${Thread.currentThread().name}") }
                 .observeOn(Schedulers.trampoline())
                 .doOnNext { println("value: $it, thread: ${Thread.currentThread().name}") }
                 .observeOn(Schedulers.newThread())
                 .doOnNext { println("value: $it, thread: ${Thread.currentThread().name}") }
                 .observeOn(Schedulers.io())
                 .doOnNext { println("value: $it, thread: ${Thread.currentThread().name}") }
                 .observeOn(Schedulers.computation())
                 .doOnNext { println("value: $it, thread: ${Thread.currentThread().name}") }
                 .subscribe()

         Thread.sleep(1000)
     }

    /**
     * Schedulers.io - Используется неограниченный пул потоков. Если в пуле есть свободный поток,
     * вернёт его. Если в пуле нет свободного потока вернёт новый поток и добавит его в пул.
     * Может стать причиной ООМ. Подходит для I/O задач, которые не юзают активно CPU
     * (т.е. задач, которые в основном висят в режиме ожидания - запрос к БД, к серверу, к диску и тд).
     *
     * Schedulers.computation - Используется ограниченный пул потоков
     * (кол-во потоков == кол-во ядер процессора)
     * Подходит для тяжёлых задач, которые активно юзают CPU (типа архивации или каких-нибудь вычислений)
     *
     * Schedulers.newThread - создаёт каждый раз новый поток. Использовать осторожно. Может стать
     * причиной ООМ. Лучше использовать Schedulers.from(Executor()) с конечным кол-вом потоков.
     *
     * Schedulers.trampoline - Выполняет всё на том потоке с которого было запущено. Юзается для тестов
     *
     * Schedulers.single - юзает один ОБЩИЙ поток. Выполняет все задачи последовательно на одном потоке.
     * Нужен для тех задач, которые должны выполняться последовательно на одном фоновом потоке
     *
     * */
    
    /**
     * Важно понимать как работает применение шедулера к элементам реактивного стрима, т.е. в какой
     * момент шедулер решает на каком потоке будет выполнен тот или иной оператор. А происходит это в
     * момент создания стрима. Как момжно видеть ниже хоть мы и используем шедулер который,
     * как может показаться, должен выполнять сабскрайб каждый раз на новом потоке, в действительности
     * выполняется каждый раз на одном и том же потоке. Потому что стрим создаётся один раз и
     * шедулер применяется один раз.
     * */
    @Test
    fun newThread1() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .subscribeOn(Schedulers.newThread())
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }

    /**
     * То же самое с observeOn
     * */
    @Test
    fun newThread2() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .observeOn(Schedulers.newThread())
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }

    /**
     * В то время как во вложенных стримах, из-за постоянного пересоздания внутреннего стрима, шедулер
     * применяется несколько раз, что приводит к тому, что сабскрайб выполняется каждый раз в
     * разных потоках.
     * */
    @Test
    fun newThreadInNestedStream() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap {
                    return@flatMap Observable.just(it)
                            .subscribeOn(Schedulers.newThread())
                }
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }

    @Test
    fun newThreadInNestedStream2() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap {
                    return@flatMap Observable.just(it)
                            .observeOn(Schedulers.newThread())
                }
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }
}