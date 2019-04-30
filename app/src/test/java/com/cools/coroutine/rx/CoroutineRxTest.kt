package com.cools.coroutine.rx

import android.util.Log
import io.reactivex.Single
import kotlinx.coroutines.*
import org.junit.Test
import java.lang.Exception
import java.lang.RuntimeException

class CoroutineRxTest {


    @Test
    fun completeTest() = runBlocking {
        withContext(Dispatchers.IO) {
            val result = processRxJava().blockingGet()
            println("result.$result")
        }
    }

    @Test
    fun errorTest() = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                val result = processRxJava()
                    .flatMap {
                        errorRxJava()
                    }
                    .blockingGet()
                println("result.$result")
            }catch (e: Exception){
                println(e.message)
            }
        }
    }

    @Test
    fun errorHandlerTest() = runBlocking<Unit> {
        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            println("throwable.$throwable")
        }

        GlobalScope.launch(errorHandler) {
            val result = withContext(Dispatchers.IO) {
                errorRxJava().blockingGet()
            }
            println("result.$result")
        }.join()
    }

    private fun processRxJava(): Single<Boolean> {
        return Single.create {
            Thread.sleep(1000)
            it.onSuccess(true)
        }
    }

    private fun errorRxJava(): Single<Boolean> {
        return Single.create {
            Thread.sleep(500)
            throw RuntimeException("handle error from RxJava")
        }
    }
}