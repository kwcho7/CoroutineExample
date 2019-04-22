package com.cools.coroutineexample.exceptionHandler

import kotlinx.coroutines.*
import org.junit.Test
import java.io.IOException
import java.lang.ArithmeticException
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

class ExceptionHandler {

    @Test
    fun propagation() = runBlocking{
        val job = GlobalScope.launch {
            println("Throwing exception from launch")
            throw IndexOutOfBoundsException()
        }
        job.join()
        println("Join failed job")

        val deferred = GlobalScope.async {
            println("Throwing exception from async")
            throw ArithmeticException()
        }

        try {
            deferred.await()
            println("Unreached")
        } catch (e: ArithmeticException){
            println("Caught ArithmeticException")
        }
    }

    @Test
    fun coroutineExceptionHandler() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }

        val job = GlobalScope.launch(handler) {
            throw AssertionError()
        }
        val deferred = GlobalScope.async(handler) {
            throw ArithmeticException()
        }
        joinAll(job, deferred)
    }

    @Test
    fun cancelationException() = runBlocking {
        val job = launch {
            val child = launch {
                try {
                    delay(Long.MAX_VALUE)
                }finally {
                    println("Child is canceled")
                }
            }
            yield()
            println("Cancelling child")
            child.cancel()
            child.join()
            yield()
            println("Parent is not canceled.")
        }
        job.join()
    }

    @Test
    fun cancelationException2()= runBlocking {
        val handler = CoroutineExceptionHandler{ _, exception ->
            println("Caught $exception")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE)
                }finally {
                    withContext(NonCancellable){
                        println("Children are cancelled, but exception is not handled until all children terminate")
                        delay(100)
                        println("The first child finished its non cancellable block")
                    }
                }
            }

            launch {
                delay(10)
                println("Second child throws an exception")
                throw ArithmeticException()
            }
        }
        job.join()
    }


    @Test
    fun cancelationException3() = runBlocking {
        val handler = CoroutineExceptionHandler{ _, exception ->
            println("Caught original $exception")
        }

        val job = GlobalScope.launch(handler) {
            val inner = launch {
                launch {
                    launch {
                        throw IOException()
                    }
                }
            }
            try {
                inner.join()
            }catch (e: CancellationException){
                println("Rethrowing CancellationException with original cause")
                throw e
            }
        }
        job.join()
    }

    @Test
    fun superVisionJob() = runBlocking {
        val superVisor = SupervisorJob()
        with(CoroutineScope(coroutineContext + superVisor)) {
            val firstChild = launch(CoroutineExceptionHandler(){ _, _ -> }) {
                println("First child is failing")
                throw AssertionError("First child is cancelled")
            }

            val secondChild = launch {
                firstChild.join()
                println("First child is cancelled. ${firstChild.isActive}, but second one is still active")
                try {
                    delay(Long.MAX_VALUE)
                }finally {

                    println("Second child is cancelled cause supervisor is cancelled.")
                }

            }

            firstChild.join()
            println("Cancelling supervisor")
            superVisor.cancel()
            secondChild.join()
        }
    }

    @Test
    fun superVisionScope() = runBlocking {
        try {
            supervisorScope{
                val child = launch {
                    try {
                        println("Child is sleeping")
                        delay(Long.MAX_VALUE)
                    }finally {
                        println("child is cancelled")
                    }
                }
                yield()
                println("Throwing exception from scope")
                throw AssertionError()
            }
        } catch (e: AssertionError){
            println("Caught assertion error")
        }
    }

    @Test
    fun exceptionsInSupervisedCoroutine() = runBlocking {
        val handler = CoroutineExceptionHandler(){ _, exception ->
            println("Caught $exception")
        }
        supervisorScope{
            val child = launch(handler){
                println("Child throws an exception")
                throw AssertionError()
            }
            println("Scope is completing")
        }
        println("Scope is completed")
    }
}