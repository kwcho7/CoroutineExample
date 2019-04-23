package kr.cools.demo.kotlin.coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.junit.Test

class Channels {

    @Test
    fun sendReceive() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for(x in 1..5){
                channel.send(x * x)
                delay(100)
            }
        }

        repeat(5) {
            println(channel.receive())

        }
        println("Done")
    }


    @Test
    fun closingAndIterationOverChannel() = runBlocking {
        val channel = Channel<Int>()
        launch {
            for (x in 1..5) channel.send( x * x)
            channel.close()
        }

        for (y in channel) println(y)
        println("Done")
    }

    fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
        for(x in 1..5) send(x * x)
    }


    @Test
    fun buildingChannelProducers() = runBlocking {
        val squares = produceSquares()
        squares.consumeEach {
            println(it)
        }
        println("Done")
    }

    fun CoroutineScope.produceNumbers() = produce {
        var x = 1
        while(true) send(x++)
    }

    fun CoroutineScope.square(numbers: ReceiveChannel<Int>): ReceiveChannel<Int> = produce {
        for(x in numbers) send(x * x)
    }

    @Test
    fun pipeline() = runBlocking {
        val numbers = produceNumbers()
        val squares = square(numbers)
        for( i in 1..5) println(squares.receive())
        println("Done")
        coroutineContext.cancelChildren()
    }

    fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
        var x = start
        while(true){
            send(x++)
        }
    }

    fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
        for(x in numbers){
            if(x%prime != 0) send(x)
        }
    }

    @Test
    fun primeNumbersWithPipeline() = runBlocking {
        var cur = numbersFrom(2)
        for(i in 1..10) {
            val prime = cur.receive()
            println("output : $prime")
            cur = filter(cur, prime)
        }
        coroutineContext.cancelChildren()
    }

    fun CoroutineScope.produceNumbersDelay() = produce<Int> {
        var x = 1
        while(true){
            send(x++)
            delay(100)
        }
    }

    fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>) = launch {
        for(msg in channel) {
            println("Processor #$id received $msg ")
        }
    }

    @Test
    fun fanOut() = runBlocking {
        val producer = produceNumbersDelay()
        repeat(5) {
            launchProcessor(it, producer)
        }
        delay(950)
        producer.cancel()
    }


    suspend fun sendString(channel: SendChannel<String>, s: String, time:Long) {
        while(true){
            println("send1")
            delay(time)
            channel.send(s)
            println("send2")
        }
    }

    @Test
    fun fanIn() = runBlocking {
        val channel = Channel<String>()
        launch {
            sendString(channel, "foo", 200L)
        }
        launch {
            sendString(channel, "BAR", 500L)
        }
        repeat(6) {
            println(channel.receive())
        }
        coroutineContext.cancelChildren()
    }


    @Test
    fun bufferedChannel() = runBlocking {
        val channel = Channel<Int>(4)
        val sender = launch {
            repeat(10) {
                println("Sending $it")
                channel.send(it)
            }
        }
        delay(1000)
        sender.cancel()
    }


    data class Ball(var hits: Int)

    @Test
    fun main() = runBlocking {
        val table = Channel<Ball>()
        launch {
            player("ping", table)
        }
        launch {
            player("pong", table)
        }

        table.send(Ball(0))
        delay(1000)
        coroutineContext.cancelChildren()
    }

    suspend fun player(name: String, table: Channel<Ball>) {
        for(ball in table){
            ball.hits++
            println("$name $ball")
            delay(300)
            table.send(ball)
        }
    }


    @Test
    fun tickerChannel() = runBlocking<Unit> {
        val tickerChannel = ticker(100, 0)
        var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }

        println("Initial element is available immediately $nextElement")

        nextElement = withTimeoutOrNull(50) { tickerChannel.receive() }
        println("Next element is not ready in 50ms: $nextElement")

        nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
        println("Next element is not ready in 100ms: $nextElement")

        println("Consumer pauses ofr 150ms")
        delay(150L)

        nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
        println("Next element is available immediately after large consumer delay: $nextElement")

        nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
        println("Next element is ready in 50ms after consumer pause in 150ms: $nextElement")

        tickerChannel.cancel()
    }

    @Test
    fun tikcerChannelWhile() = runBlocking {
        val tickerChannel = ticker(1000, 0)

        launch {
            val startTime = System.currentTimeMillis()
            while(true){
                withTimeoutOrNull(1000){
                    tickerChannel.receive()
                    println("rec.${System.currentTimeMillis() - startTime}")
                }
            }
        }
        delay(3000L)
        coroutineContext.cancelChildren()
    }
}
