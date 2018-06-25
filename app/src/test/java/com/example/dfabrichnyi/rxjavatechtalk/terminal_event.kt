package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test

class terminal_event {


    /**
     * Терминальный ивент сигнализирует о том, что оператор находящийся выше по стриму закончил свою работу
     * Каждый оператор все отсылает вниз по стрим терминальный ивент. Без терминального ивента многие опрераторы
     * просто не будут работать
     * */
    @Test
    fun test() {
        Observable.create<Int> { emitter -> emitter.onNext(1) }
                .blockingSubscribe({ println("value = $it") }, { }, { println("complete") })
    }

    /**
     * В данном примере o2 никогда не начнёт эмитит ьсвои элементы
     * */
    @Test
    fun test2() {
        val o1 = Observable.create<Int> { emitter -> emitter.onNext(1) }
        val o2 = Observable.create<Int> { emitter -> emitter.onNext(2) }

        Observable.concat(o1, o2)
                .subscribe({ println("value = $it") }, { }, { println("complete") })

        Thread.sleep(7000)
    }
}