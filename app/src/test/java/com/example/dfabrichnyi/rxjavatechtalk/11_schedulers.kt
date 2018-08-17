import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class `11_schedulers` {

    /**
     *
     * Schedulers.io - Используется неограниченный пул потоков. Если в пуле есть свободный поток,
     * вернёт его. Если в пуле нет свободного потока вернёт новый поток и добавит его в пул.
     * Может стать причиной ООМ. Подходит для I/O задач, которые не юзают активно CPU
     * (т.е. задач, которые в основном висят в режиме ожидания - запрос к БД, к серверу, к диску и тд).
     *
     * Schedulers.computation - Используется ограниченный пул потоков
     * (кол-во потоков == кол-во ядер процессора)
     * Подходит для тяжёлых задач, которые активно юзают CPU (типа архивации или высчитывания
     * какой-нибудь хуйни (например, брутфорс MD5 хешей))
     *
     * Schedulers.newThread - создаёт новый поток при каждом (не совсем) обращении к шедулеру.
     * Лучше не использовать. Если очень нужно, то следует заюзать Schedulers.from(Executor())
     *
     * Schedulers.trampoline - Выполняет всё на том потоке с которого было запущено. Юзается для тестов
     *
     * Schedulers.single - юзает один ОБЩИЙ поток. Выполняет все задачи последовательно на одном потоке.
     * Нужен для тех задач, которые должны выполняться последовательно на одном фоновом потоке
     *
     * */
    
    /**
     * Сколько разных потоков будет выведено в результате выполнения следующего теста? Один или больше?
     * (пример: Thread-1, Thread-2, Thread-3 - 3 потока)
     * */
    @Test
    fun newThread1() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .subscribeOn(Schedulers.newThread())
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }

    @Test
    fun newThread2() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .observeOn(Schedulers.newThread())
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }

    /**
     * Тот же вопрос
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