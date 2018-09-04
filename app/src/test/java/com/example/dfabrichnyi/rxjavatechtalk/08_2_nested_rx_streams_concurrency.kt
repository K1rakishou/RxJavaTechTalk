package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class `08_2_nested_rx_streams_concurrency` {

    /**
     * Ещё один пример для использования вложенных стримов - concurrency.
     *
     * По-умолчанию все обзёрваблы проходящие через стрим выполняются последовательно.
     * */

    @Test
    fun test() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .subscribeOn(Schedulers.io())
                .doOnNext { println("value = $it, thread = ${Thread.currentThread().name}") }
                .subscribe()

        Thread.sleep(100)
    }



























































    /**
     * Параллельного выполнения в rxjava можно добиться путём запуска нескльких вложенных подстримов
     * работающих независимо друг от друга и в конце объединения (merge) всех результатов обратно в
     * один стрим. Для достижения параллельного выполнения можно заюзать, например, всем известный
     * оператор flatMap
     * */
    @Test
    fun test2() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .flatMap {
                    return@flatMap Observable.just(it)
                            .subscribeOn(Schedulers.io())
                            .doOnNext { println("value = $it, thread = ${Thread.currentThread().name}") }
                }
                .subscribe()

        Thread.sleep(100)
    }




























































    /**
     * Или можно заюзать ParallelFlowable
     * */
    @Test
    fun test3() {
        Flowable.just(1, 2, 3, 4, 5, 6, 7, 8)
                .parallel()
                .runOn(Schedulers.io())
                .doOnNext { println("value = $it, thread = ${Thread.currentThread().name}") }
                .sequential()
                .subscribe()

        Thread.sleep(100)
    }
}
