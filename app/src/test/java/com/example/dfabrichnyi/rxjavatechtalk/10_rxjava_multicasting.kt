package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test

class `10_rxjava_multicasting` {

    /**
     * Иногда нужно разослать ивенты в несколько реактивных стримов. По-умолчанию это работать не будет.
     * В примере ниже, второй сабскрайбер не получит ивентов до тех пор, пока первый не получит терминальный ивент
     * */
    @Test
    fun test() {
        val observables = Observable.just(1, 2, 3, 4, 5)

        observables.subscribe({ value -> println("observer1 = $value") }, { }, { println("OnComplete") })
        observables.subscribe({ value -> println("observer2 = $value") }, { }, { println("OnComplete") })
    }

    /**
     * Здесь нам на помощь приходит ConnectableObservable, который превращает холодный стрим в горячий.
     * Теперь стримы получают ивенты одновременно.
     * */
    @Test
    fun test2() {
        val observables = Observable.just(1, 2, 3, 4, 5)
                .publish()
                .autoConnect(2)

        observables.subscribe({ value -> println("observer1 = $value") }, { }, { println("OnComplete") })
        observables.subscribe({ value -> println("observer2 = $value") }, { }, { println("OnComplete") })
    }
}