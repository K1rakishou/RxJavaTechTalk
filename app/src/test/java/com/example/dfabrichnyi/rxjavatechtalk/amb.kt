package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit

class amb {

    /**
     * Эмитит все элементы того обзервабла, который первым эмитит элемент
     * или первым посылает терминальный ивент
     * */
    @Test
    fun test() {
        val obs1 = Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { 1 }

        val obs2 = Observable.timer(160, TimeUnit.MILLISECONDS)
                .map { 2 }

        val obs3 = Observable.timer(140, TimeUnit.MILLISECONDS)
                .map { 3 }

        Observable.amb(listOf(obs1, obs2, obs3))
                .subscribe({ println("value = ${it}") })

        Thread.sleep(500)
    }
}