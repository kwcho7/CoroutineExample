package kr.cools.demo.kotlin.coroutine

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.RuntimeException

class CancellationAndTimeouts {

    @Test
    fun jobCancelTest() = runBlocking{
        val job = launch {
            repeat(1000) { i ->
                println("I'm sleeping $i")
                delay(500)
            }
        }
        delay(6000)
        println("main: I'm tried of waiting!!")
        job.cancel()

        job.join()
        println("main: Now I can quit.")
    }

    @Test
    fun cancelWhileComputation() = runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while(isActive) {
                if(System.currentTimeMillis() >= nextPrintTime) {
                    println("I'm sleeping ${i++}..")
                    nextPrintTime += 500
                }
            }
        }
        delay(1300)
        println("main: I'm tried of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit")
    }


    @Test
    fun closingResourceFinally() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("I'm sleeping $i")
                    delay(500L)
                }
            }
            finally {
                println("I'm running finally")
            }
        }
        delay(1300L)
        println("main: I'm tried of waiting!!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    @Test
    fun runNonCancellableBlock() = runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    println("I'm sleeping $i")
                    delay(500)
                }
            }finally {
                withContext(NonCancellable){
                    println("I'm running finally")
                    delay(1000L)
                    println("And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L)
        println("main: I'm tried of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    @Test
    fun timeout() = runBlocking{
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i")
                delay(500L)
            }
        }
    }

    @Test
    fun timeoutResult() = runBlocking {
        val result = withTimeoutOrNull(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i")
                delay(500L)
            }
            "Done" // will get cancelled before it produces this result
        }
        println("result.$result")
    }
}