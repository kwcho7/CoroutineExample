package com.cools.coroutine.cancellation

import kotlinx.coroutines.*
import org.junit.Test

class CancellationTest{

    @Test
    fun cancellation() = runBlocking{
        val job1 = launch {
            try {
                delay(1000)
            }finally {
                println("cancel job1")
            }
        }

        launch {
            delay(500)
            job1.cancel()
        }
        job1.join()
    }

    @Test
    fun cancellationChild() = runBlocking {
        val job = launch {
            repeat(1000){
                println("Repeat.$it")
                delay(500)
            }
        }

        delay(1500)
        job.cancel()
        job.join()
        println("cancel end..")
    }
}