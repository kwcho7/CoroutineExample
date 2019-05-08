package com.cools.coroutine.exception

import kotlinx.coroutines.*
import org.junit.Test

class ExceptionHandleTest {

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        println("exceptionHandler.$throwable")
    }

    @Test
    fun launchException() = runBlocking{
        val job = GlobalScope.launch {
            throw IllegalStateException()
        }
        job.join()
        println("launchException end..")
    }

    @Test
    fun asyncException() = runBlocking {
        val result = GlobalScope.async {
            throw RuntimeException("exception")
        }

        // await 할때 exception 이 발생한다.
        println("result.${result.await()}")
    }

    @Test
    fun launchExceptionHandler() = runBlocking {
        val job = GlobalScope.launch(exceptionHandler) {
            throw IllegalStateException()
        }
        job.join()
        println("launchException end..")
    }

    @Test
    fun asyncExceptionHandler() = runBlocking {
        val result = GlobalScope.async {
            throw RuntimeException("exception")
        }
        try {
            // await 할때 exception 이 발생한다.
            println("result.${result.await()}")
        }catch (e: Exception){
            println("exception.${e}")
        }
    }

}