package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.TimeUnit

class `15_time` {

    /**
     * Ждёт заданное время, затем эмитит обзёрвабл содержащий 0L. С помощью оператора timeInterval
     * можно сделать так чтобы timer возвращал время прошедшее с предыдущего эмита
     * */
    @Test
    fun testTimer() {
        Observable.timer(250, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribe { value -> println("delta time = ${value.time()}ms") }

        Thread.sleep(1000)
    }

    /**
     * Эмитит обзёрвабл через равные промежутки времени.
     * */
    @Test
    fun testInterval() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .timeInterval()
                .subscribe { value -> println("delta time = ${value.time()}ms") }

        Thread.sleep(1000)
    }

    /**
     * Оба этих оператора можно помбинировать с другими создавая задержки/периодический опрос чего-либо.
     * */
    @Test
    fun testExample() {
        val infiniteSequence = Observable.just(1, 2, 3, 4, 5)
                .repeat()

        Observable.interval(100, TimeUnit.MILLISECONDS).zipWith(infiniteSequence)
                //Неожиданное поведение, если убрать subscribeOn и если переместить его в верхний стрим
                .subscribeOn(Schedulers.computation())
                .map { it.second }
                .doOnNext { println("thread = ${Thread.currentThread().name}") }
                .subscribe { value -> println(value) }

        Thread.sleep(3000)
    }
}