package kr.cools.demo.kotlin.coroutine

import kotlinx.coroutines.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class Basic {
    @Test
    fun main() = runBlocking {
        GlobalScope.launch {
            delay(1000)
            println("word!(${Thread.currentThread().id})")
        }
        println("hello(${Thread.currentThread().id}),")
        delay(2000)
    }

    @Test
    fun jobTest() = runBlocking {
        val job = GlobalScope.launch {
            delay(1000)
            println("world!!")
        }

        println("hello,")
        job.join()
    }

    @Test
    fun launch() = runBlocking {
        launch {
            delay(1000)
            println("world!")
        }
        println("hello,")
    }

    @Test
    fun inScopeBuilder() = runBlocking {
        launch {
            delay(2000)
            println("Task from runBlocking(${Thread.currentThread().id})")
        }

        coroutineScope {
            launch {
                delay(500)
                println("Task from nested launch(${Thread.currentThread().id})")
            }

            delay(100)
            println("Task from coroutine scope(${Thread.currentThread().id})")
        }

        println("Coroutine scope is over(${Thread.currentThread().id})")
    }


    @Test
    fun suspendTest() = runBlocking {
        launch {
            doWorld()
        }
        println("Hello,(${Thread.currentThread().id})")
    }

    suspend fun doWorld() {
        delay(1000)
        println("world!!(${Thread.currentThread().id})")
    }


    @Test
    fun areTest() = runBlocking {
        repeat(100){
            launch {
                delay(1000)
                print(".")
            }
        }
    }

    @Test
    fun repeatExit() = runBlocking{
        GlobalScope.launch {
            repeat(1000) { i ->
                println("I'm sleeping $i")
                delay(500)
            }
        }
        delay(5000)
    }

}
