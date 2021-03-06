package com.example.dfabrichnyi.rxjavatechtalk

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import org.junit.Test
import java.io.IOException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class why {

    sealed class Lifecycle {
        object Destroyed : Lifecycle()
        object Resumed : Lifecycle()
        object Paused : Lifecycle()
    }

    sealed class CameraState {
        object Destroyed : CameraState()
        data class Created(val cameraHandle: Long) : CameraState()
    }

    sealed class PermissionState {
        object Uninitialized : PermissionState()
        data class Granted(val permissions: List<String>) : PermissionState()
    }

    data class State(val lifecycle: Lifecycle,
                     val cameraState: CameraState,
                     val permissionState: PermissionState) {

        fun isStateOk(): Boolean {
            return lifecycle is Lifecycle.Resumed &&
                    cameraState is CameraState.Created &&
                    permissionState is PermissionState.Granted &&
                    (permissionState.permissions == listOf("camera", "gps"))
        }

        override fun toString(): String {
            return "[lifecycle = ${lifecycle::class.java.simpleName}, " +
                    "camera = ${cameraState::class.java.simpleName}, " +
                    "permissions = ${permissionState::class.java.simpleName}]"
        }
    }

    val random = Random()

    data class LonLat(val lon: Double, val lat: Double)
    sealed class AsyncResult {
        class Success(val message: String) : AsyncResult()
        class Error(val exception: Exception) : AsyncResult()
    }

    fun getCurrentLocation(): LonLat {
        return LonLat(random.nextDouble() * 90.0, random.nextDouble() * 180.0)
    }

    fun takePicture(): String {
        return "picture_${Math.abs(random.nextInt(1000))}"
    }

    fun sendPictureToTheServer(picture: String, lonLat: LonLat): Single<AsyncResult> {
        return Single.fromCallable {
            Thread.sleep(500)

            if (random.nextInt() % 2 == 0) {
                throw IOException("Bad connection error")
            }

            val msg = "SUCCESS, picture ${picture} with location ${lonLat} has been sent successfully"
            return@fromCallable AsyncResult.Success(msg) as AsyncResult
        }.retry(3) {
            println("ERROR")
            true
        }
        .onErrorReturn { throwable ->
            return@onErrorReturn AsyncResult.Error(Exception("ERROR ${throwable.message}"))
        }
    }

    @Test
    fun test() {
        val lifecycleState = BehaviorSubject.createDefault<Lifecycle>(Lifecycle.Destroyed)
        val cameraState = BehaviorSubject.createDefault<CameraState>(CameraState.Destroyed)
        val permissionsState = BehaviorSubject.createDefault<PermissionState>(
                PermissionState.Uninitialized)

        println("START")

        thread {
            Thread.sleep(1000)
            println(" >>> Lifecycle.Resumed")
            lifecycleState.onNext(Lifecycle.Resumed)

            Thread.sleep(5000)
            println(" >>> Lifecycle.Paused")
            lifecycleState.onNext(Lifecycle.Paused)

            Thread.sleep(5000)
            println(" >>> Lifecycle.Resumed")
            lifecycleState.onNext(Lifecycle.Resumed)

            Thread.sleep(5000)
            println(" >>> Lifecycle.Destroyed")
            lifecycleState.onNext(Lifecycle.Destroyed)
        }

        thread {
            Thread.sleep(2000)
            println(" >>> PermissionState.Granted")
            permissionsState.onNext(PermissionState.Granted(listOf("camera", "gps")))
        }

        thread {
            Thread.sleep(4000)
            println(" >>> CameraState.Created")
            cameraState.onNext(CameraState.Created(0))
        }

        val stateObservable = Observables.combineLatest(
                lifecycleState,
                cameraState,
                permissionsState
        ) { lifecycle, camera, permissions -> State(lifecycle, camera, permissions) }

        Observable.interval(500, TimeUnit.MILLISECONDS).withLatestFrom(stateObservable)
                .map { it.second }
                .doOnNext { state -> println(state) }
                .filter { state -> state.isStateOk() }
                .map { getCurrentLocation() to takePicture() }
                .flatMapSingle { (location, picture) ->
                    println("Trying to send a picture to the server")
                    return@flatMapSingle sendPictureToTheServer(picture, location)
                }
                .subscribe({ asyncResult ->
                    when (asyncResult) {
                        is AsyncResult.Success -> {
                            println(asyncResult.message)
                        }
                        is AsyncResult.Error -> {
                            println(asyncResult.exception.message)
                        }
                    }
                })

        Thread.sleep(20000)
        println("END")
    }

}