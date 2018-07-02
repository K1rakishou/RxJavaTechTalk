package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test
import java.util.concurrent.TimeUnit

class amb {

    /**
     * Эмитит все элементы того обзервабла, который первым эмитит элемент
     * или первым посылает терминальный ивент
     *
     * Юзкейс - есть несколько серверов, нужно определить до какого из них найменьшая задержка
     * */
    @Test
    fun test() {
        val obs1 = Observable.timer(170, TimeUnit.MILLISECONDS)
                .map { "88.244.13.41" }

        val obs2 = Observable.timer(120, TimeUnit.MILLISECONDS)
                .map { "88.244.13.42" }

        val obs3 = Observable.timer(140, TimeUnit.MILLISECONDS)
                .map { "88.244.13.43" }

        Observable.amb(listOf(obs1, obs2, obs3))
                .subscribe({ println("value = ${it}") })

        Thread.sleep(500)
    }
}