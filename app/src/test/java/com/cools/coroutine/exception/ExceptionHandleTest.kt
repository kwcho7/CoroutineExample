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

    private val job = Job() // SupervisorJob
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val supervisorJob = SupervisorJob() // SupervisorJob
    private val supervisorScope = CoroutineScope(Dispatchers.IO + supervisorJob)


    private fun doWork(): Deferred<String> = scope.async {
        throw RuntimeException("doWork Exception.")
    }

    private fun loadData() = scope.launch {
        try {
            doWork().await()
        } catch (e: java.lang.Exception){
            println("occur exception.$e")
        }
    }

    /**
     * Child job 이 실패하면 부모까지 전달되어 오류가 발생한다.
     * 해당 오류를 해결하려면 SupervisorJob 을 이용해야한다.
     * SupervisorJob 은 자식이 실패 또는 취소로인해 부모가 취소되지 않으며 다른 Child 에게도 영향을 주지 않는다.
     */
    @Test
    fun doWorkTest1() = runBlocking<Unit> {
        val loadJob = loadData()
        loadJob.join()
    }

    /**
     * SupervisorJob 는 코루틴 범위에서 비동기를 명시적으로 실행할 경우에만 동작한다.
     * loadData2 의 async 는 부모 코루틴 범위에서 시작했기 때문에 오류가 발생한다.
     * 이를 해결하기 위해서는 coroutinScope{} 을 사용하여 async 를 깜싸야한다.
     */
    @Test
    fun doWorkTest2() = runBlocking<Unit> {
        val loadJob = loadData2()
        loadJob.join()
    }

    private fun loadData2() = supervisorScope.launch {
        try {
            async { throw RuntimeException("RuntimeException.") }.await()
        } catch (e: Exception) {
            println("e.$e")
        }
    }


    /**
     * SupervisorJob 으로 실행하며 async 를 명시적으로 coroutineScop 으로 감쌌으므로 오류가 발생하지 않는다.
     */
    @Test
    fun doWorkTest3() = runBlocking<Unit> {
        val loadJob = loadData3()
        loadJob.join()
    }


    private fun loadData3() = supervisorScope.launch {
        try {
            coroutineScope {
                async { throw RuntimeException("RuntimeException.") }.await()
            }
        } catch (e: Exception) {
            println("e.$e")
        }
    }

}