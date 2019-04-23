package kr.cools.demo.kotlin.coroutine

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.math.log

class CoroutineContextDispatchers {

    @Test
    fun dispatcherAndThread() = runBlocking<Unit>{
        println("dispatcherAndThread ${Thread.currentThread().name}")
        launch {
            println("main runBlocking : I'm working in thread ${Thread.currentThread().name}")
        }

        launch(Dispatchers.Unconfined) {
            println("Unconfined : I'm working in thread ${Thread.currentThread().name}")
        }

        launch(Dispatchers.Default) {
            println("Default : I'm working in thread ${Thread.currentThread().name}")
        }

        launch(newSingleThreadContext("MyOwnThread")) {
            println("NewSingleThreadContext : I'm working in thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun unconfinedVsConfinedDispatcher() = runBlocking<Unit> {
        launch(Dispatchers.Unconfined) {
            println("Unconfined : I'm working in thread ${Thread.currentThread().name}")
            delay(500)
            println("Unconfined : After delay in thread ${Thread.currentThread().name}")
        }

        launch {
            println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
            delay(1000)
            println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun debuggingCoroutinesAndThreads() = runBlocking<Unit> {
        val a = async{
            println("I'm computing a piece of the answer ${Thread.currentThread().name}")
            6
        }

        val b = async{
            println("I'm computing another piece of the answer ${Thread.currentThread().name}")
            7
        }
        println("The answer is ${a.await() * b.await()}")
    }

    @Test
    fun jumpingBetweenThreads() = runBlocking{
        newSingleThreadContext("Ctx1").use { ctx1 ->
            newSingleThreadContext("Ctx2").use { ctx2 ->
                runBlocking(ctx1) {
                    println("Started in ctx1 ${Thread.currentThread().name}")

                    withContext(ctx2) {
                        println("working in ctx2 ${Thread.currentThread().name}")
                    }

                    println("Back to ctx1 ${Thread.currentThread().name}")
                }
            }
        }

        println("My job is $coroutineContext[Job]")
    }


    @Test
    fun childrenOfCorountine() = runBlocking{
        val request = launch {
            GlobalScope.launch {
                println("job1 : I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1 : I am not affected by cancellation of the request")
            }

            launch {
                delay(100)
                println("job2 : I am a child of the request coroutine")
                delay(1000)
                println("job2 : I will not execute this line if my parent request is cancelled")
            }

        }

        delay(500)
        request.cancel()
        delay(1000)
        println("main: Who has survived reques cancellation?")
    }

    @Test
    fun parentalResponsibilities() = runBlocking{
        val request = launch {
            repeat(2){ i ->
                launch {
                    delay((i + 1) * 200L)
                    println("Coroutine $i is done. ${Thread.currentThread().name}")
                }
            }
            println("request : I'm done and I don't explicitly join my children that are still active ${Thread.currentThread().name}")
        }
        request.join()
        println("Now processing of the request is complete")
    }

    @Test
    fun namingCoroutineForDebugging() = runBlocking {
        println("Started main coroutine")

        val v1 = async(CoroutineName("v1coroutine")) {
            delay(500)
            println("Computing v1")
            252
        }

        val v2 = async(CoroutineName("v2coroutine")) {
            delay(1000)
            println("Computing v2")
            6
        }

        println("The answer for v1/v2 = ${v1.await() / v2.await()}")
    }

    @Test
    fun combiningContextElements() = runBlocking<Unit> {
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("I'm working in thread ${Thread.currentThread().name}")
        }
    }

    @Test
    fun activityTest() = runBlocking{
        val activity = Activity()
        activity.doSomething()
        println("Launched coroutine ${Thread.currentThread().name}")
        delay(500L)
        println("Destroying activity! ${Thread.currentThread().name}")
        activity.destory()
        delay(10000L)
    }


    class Activity: CoroutineScope by CoroutineScope(Dispatchers.Default) {

         fun doSomething() {
             repeat(10) { i ->
                launch {
                    delay((i + 1) * 200L)
                    println("Coroutine $i is done. ${Thread.currentThread().name}")
                }
             }
         }

        fun destory(){
            cancel()
        }
    }

    @Test
    fun threadLocalData() = runBlocking {
    }

}