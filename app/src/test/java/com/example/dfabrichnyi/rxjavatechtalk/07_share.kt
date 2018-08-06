package com.example.dfabrichnyi.rxjavatechtalk.`0_info`

import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class `07_share` {

    /**
     * Оператор share решает проблему мультикастинга обзервабла нескольким сабскрайберам
     * */
    @Test
    fun test1() {
        val index = AtomicInteger(0)
        val timer = Observable.interval(500, TimeUnit.MILLISECONDS)
                .map { index.getAndIncrement() }

        timer.subscribe({ value -> println("1: index = $value") })
        timer.subscribe({ value -> println("2: index = $value") })
        timer.subscribe({ value -> println("3: index = $value") })

        Thread.sleep(3000)
    }














































    @Test
    fun test2() {
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