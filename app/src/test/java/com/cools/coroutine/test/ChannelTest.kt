package com.cools.coroutine.test

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ChannelTest {

    @Test
    fun channelOfferTest() = runBlocking<Unit> {
        val channel = Channel<Int>()
        launch {
            for(ret in channel){
                println("result.${ret}")
            }
        }

        repeat(10){
            channel.offer(it)
            delay(10)
        }
        channel.close()
        println("done.")
    }


    @Test
    fun channelSendTest() = runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(10){
                channel.send(it)
            }
            channel.close()
        }

        for(ret in channel){
            println("result.${ret}")
        }
        println("done.")
    }
}