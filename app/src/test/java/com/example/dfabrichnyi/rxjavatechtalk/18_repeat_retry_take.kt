package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

class `18_repeat_retry_take` {

    /**
     * Оператор repeat переподписывается на стрим при получении терминального ивента onComplete
     * */
    @Test
    fun test1() {
        Observable.just(1)
                .repeat(5)
                .subscribe({ println("value = $it") })
    }





















































    /**
     * Оператор retry переподписывается на стрим при получении терминального ивента onError
     * */
    @Test
    fun test2() {
        val attemptNumber = AtomicInteger(0)

        Observable.just(1)
                .concatMap {
                    return@concatMap Observable.just(it)
                            .delay(1, TimeUnit.SECONDS)
                            .doOnNext {
                                print("Attempt to download a file #${attemptNumber.incrementAndGet()}...")

                                if (attemptNumber.get() < 5) {
                                    println("ERROR")
                                    throw IllegalStateException("ERROR")
                                } else {
                                    println("SUCCESS")
                                }
                            }
                }
                .retry(5)
                .subscribe({ println("value = $it") })

        Thread.sleep(6000)
    }













































    /**
     * Оператор takeUntil берёт ивенты из апстрима пока результат предиката == false,
     * после этого он отписывается от апстрима и посылает терминальный ивент onComplete
     * */
    @Test
    fun test3() {
        val flag = AtomicBoolean(false)

        Observable.timer(2, TimeUnit.SECONDS)
                .subscribe({ flag.set(true) })

        Observable.just(0)
                .zipWith(Observable.timer(400, TimeUnit.MILLISECONDS))
                .map { it.first }
                .repeat()
                .takeUntil { flag.get() }
                .subscribe({ println("value = $it") }, { println("ERROR") }, { println("DONE") })

        Thread.sleep(2500)
    }
}