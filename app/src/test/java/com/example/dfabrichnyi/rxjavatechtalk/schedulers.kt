package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class schedulers {

    /**
     *
     * Schedulers.io - Используется неограниченный пул потоков
     * Подходит для io задач (обращения к БД, к серверу и тд)
     *
     * Schedulers.computation - Используется ограниченный пул потоков (кол-во потоков == кол-во ядер процессора)
     * Подходит для тяжёлых задач (типа архивации или высчитывания какой-нибудь хуйни (мд5 от кучи файлов например))
     *
     * Schedulers.newThread - создаёт новый поток при каждом обращении к шедулеру, виновник OOM и Could not allocate JNI Env
     * Лучше не использовать вообще
     *
     * Schedulers.trampoline - Выполняет всё на том потоке с которого было запущено. Юзается для тестов
     *
     * Schedulers.single - юзает один ОБЩИЙ поток. Выполняет все задачи последовательно на одном потоке.
     * Нужен для тех задач, которые должны выполняться последовательно на одном фоновом потоке
     *
     * */


    /**
     * Задачки
     * */

    
    /**
     * Сколько разных потоков будет выведено в результате выполнения следующего теста? Один или больше?
     * (пример: Thread-1, Thread-2, Thread-3 - 3 потока)
     * */
    @Test
    fun newThread() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .subscribeOn(Schedulers.newThread())
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }

    /**
     * Тот же вопрос
     * */
    @Test
    fun newThreadInNestedStream2() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
                .flatMap {
                    return@flatMap Observable.just(it)
                            .subscribeOn(Schedulers.newThread())
                }
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
                            .observeOn(Schedulers.newThread())
                }
                .subscribe({ println("value: $it, thread: ${Thread.currentThread().name}") })

        Thread.sleep(1000)
    }
}