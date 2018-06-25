package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import org.junit.Test

class empty_vs_never {

    /**
     * Observable.empty возвращает обзервабл который не эмитит никаких элементов, но вызывает onComplete
     * */
    @Test
    fun test1() {
        Observable.just(1, 2, 3, 4)
                .flatMap {
                    if (it != 2) {
                        return@flatMap Observable.just(it)
                    }

                    return@flatMap Observable.empty<Int>()
                }
                .subscribe({ println("value = $it") }, { }, { println("onComplete") })
    }

    /**
     * Observable.never возвращает обзервабл который не эмитит никаких элементов, и не вызывает onComplete
     * */
    @Test
    fun test2() {
        Observable.just(1, 2, 3, 4)
                .flatMap {
                    if (it != 2) {
                        return@flatMap Observable.just(it)
                    }

                    return@flatMap Observable.never<Int>()
                }
                .subscribe({ println("value = $it") }, { }, { println("onComplete") })
    }
}