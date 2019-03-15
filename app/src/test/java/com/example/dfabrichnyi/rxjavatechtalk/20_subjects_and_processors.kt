package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class `19_subjects_and_processors` {

    /**
        Виды сабжектов:
        - PublishSubject
        - BehaviorSubject
        - ReplaySubject
        - UnicastSubject
        - AsyncSubject

        Виды процессоров:
        - PublishProcessor
        - BehaviorProcessor
        - ReplayProcessor
        - UnicastProcessor
        - AsyncProcessor

         Разница между сабжектом и процессором - сабжект наследуется от обзёрвабла, а процессор от
         фловабла, следовательно у процессоров есть поддержка backpressure. У сабжектов её нету.
         Остальное всё совпадает.

         Зачем они вообще нужны? Сабжекты и процессоры одновременно наследуются как от обзёрвабла/фловабла
         так и от обзёрвера. Что это значит? То что сабжект/процессор может одновременно как эмитить
         элементы так и подписываться на них. Это удобный переходник из императивного мира в реактивный.
         Очень удобны для конвертации коллбеков (или любых других асинхронных операций) в реактивные стримы.
     * */

    /**
     * PublishSubject - это "горячий" обзёрвабл, который мультикастит (по дефолту) ивент всем
     * подписавшимся на него. После получения терминального ивента (onComplete/onError) -
     * завершает свою работу.
     * */
    @Test
    fun test_PublishSubject() {
        val ps = PublishSubject.create<Int>()
        ps.subscribe { value -> println(value) }

        ps.onNext(1)
        ps.onNext(2)
        ps.onNext(3)
        ps.onNext(4)
        ps.onNext(5)
        ps.onComplete()

        ps.onNext(6)
    }


    /**
     * BehaviorSubject похож на PublishSubject с тем различием, что он запоминает последний
     * заэмиченный ивент и рассылает его все подписчикам. При получении нового ивента старый
     * обновляется. Этот тип сабжекта очень удобен когда нужно хранить какое-то состояние которое
     * может временами меняться (например лайфсайкл активити/фрагмента или состояние сети и тп).
     * */
    @Test
    fun test_BehaviorSubject() {
        // Мы можем проинициализировать этот сабжект дефолтным значением, которое будет сразу же
        // разослано все подписчикам.
        val bs = BehaviorSubject.createDefault(-1)
        bs.subscribe { value -> println(value) }

        bs.onNext(1)
        bs.onNext(2)
        bs.onNext(3)
        bs.onNext(4)
        bs.onNext(5)

        bs.subscribe { value -> println(value) }
        bs.subscribe { value -> println(value) }
        bs.subscribe { value -> println(value) }
        bs.subscribe { value -> println(value) }

        bs.onComplete()
    }

    /**
     * ReplaySubject очень похож на BehaviorSubject с той разницей, что он может сохранять несколько
     * последних ивентов, которые потом будут разосланы всем подписчикам. Так же можно задавать
     * максимальное количество хранимых элементов и даже время их хранения.
     * */
    @Test
    fun test_ReplaySubject() {
        val rs = ReplaySubject.createWithTimeAndSize<Int>(100, TimeUnit.MILLISECONDS, Schedulers.computation(), 3)
        rs.subscribe { value -> println(value) }

        rs.onNext(1)
        rs.onNext(2)
        rs.onNext(3)
        rs.onNext(4)
        rs.onNext(5)

        println("====")
        rs.subscribe { value -> println(value) }

        println("====")
        Thread.sleep(200)
        rs.subscribe { value -> println(value) }
    }

    /**
     * UnicastSubject похож на PublishSubject с той разницей, что UnicastSubject будет буферизировать
     * все ивенты проходящие через него, когда у него нет подписчика. У UnicastSubject может быть
     * только один подписчик за всё время его существования. Если попытаться подписаться на него
     * два и более раз, то он бросит исключение с сообщением "Only a single observer allowed".
     *
     * В RxJavaExtensions либе есть ещё один тип сабжекта - DispatchWorkSubject который
     * похож на UnicastSubject с той разницей, что на него может быть подписано несколько подписчиков.
     * */
    @Test
    fun test_UnicastSubject() {
        val us = UnicastSubject.create<Int>()

        us.onNext(1)
        us.onNext(2)
        us.onNext(3)
        us.onNext(4)
        us.onNext(5)
        us.onNext(6)
        us.onNext(7)
        us.onNext(8)

        val d1 = us.subscribe { value -> println(value) }

        Thread.sleep(100)
        d1.dispose()

        us.subscribe { value -> println(value) }
    }

    /**
     * AsyncSubject эмитит последний прошедший через него ивент всем подписчикам при получении
     * onComplete
     * */
    @Test
    fun test_AsyncSubject() {
        val asb = AsyncSubject.create<Int>()
        asb.subscribe { value -> println(value) }

        asb.onNext(1)
        asb.onNext(2)
        asb.onNext(3)
        asb.onNext(4)
        asb.onComplete()
    }


    /**
     * Все сабжекты и процессоры по-умолчанию потоконебезопасны. Их можно конвертировать в
     * потокобезопасный SerializedSubject с помощью метода toSerialized().
     * Например AsyncSubject.create<Int>().toSerialized()
     * */
}