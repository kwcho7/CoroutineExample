package com.cools.coroutineexample

import android.provider.Settings
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

class SharedMutableStateAndConcurrency {

    suspend fun CoroutineScope.massiveRun(action: suspend () -> Unit) {
        val n = 100
        val k = 1000
        val time = measureTimeMillis {
            val jobs = List(n){
                launch {
                    repeat(k){ action() }
                }
            }
            jobs.forEach { it.join() }
        }
        println("Completed ${n * k} actions in $time ms")
    }

    @Test
    fun problem() = runBlocking{
        var counter = 0
        GlobalScope.massiveRun {
            counter++
        }
        println("Counter = $counter")
    }

    val fixtedContext = newFixedThreadPoolContext(2, "pool")

    @Test
    fun problem2() = runBlocking{
        var counter = 0
        CoroutineScope(fixtedContext).massiveRun {
            counter++
        }
        println("Counter = $counter")
    }

    @Volatile
    var counter = 0

    @Test
    fun volatilesNoHelp() = runBlocking{
        GlobalScope.massiveRun{
            counter++
        }
        println("Counter = $counter")
    }

    var counterAtomic = AtomicInteger()

    @Test
    fun threadSafeDataStructures() = runBlocking{
        GlobalScope.massiveRun {
            counterAtomic.incrementAndGet()
        }
        println("Counter Atomic : ${counterAtomic.get()}")
    }

    @Test
    fun threadConfinementFineGrained() = runBlocking {
        GlobalScope.massiveRun {
            withContext(coroutineContext){
                counter++
            }
        }
        println("Counter : $counter")
    }

    @Test
    fun threadConfinementCoarseGrained() = runBlocking {
        CoroutineScope(coroutineContext).massiveRun {
            counter++
        }
        println("Counter : $counter")
    }

    val mutex = Mutex()
    @Test
    fun mutualExclusion() = runBlocking {
        GlobalScope.massiveRun {
            mutex.withLock {
                counter++
            }
        }
        println("Counter : $counter")
    }


    @Test
    fun actors() = runBlocking<Unit> {
        val counter = counterActor()
        GlobalScope.massiveRun {
            counter.send(IncCounter)
        }
        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("Counter : ${response.await()}")
        counter.close()
    }

    fun CoroutineScope.counterActor() = actor<CounterMsg> {
        var counter = 0
        for(msg in channel){
            when(msg){
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }



}

sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>): CounterMsg()