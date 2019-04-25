package com.cools.coroutine.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.junit.Test

class ActorTest {

    @Test
    fun actorSendOffser() = runBlocking<Unit>{
        val event = GlobalScope.actor<String>(capacity = 1) {
            for(receive in channel){
                println("receive.${receive}")
                delay(500)
            }
        }
        event.offer("a")
        delay(100)
        event.offer("b")
        delay(100)
    }

    @Test
    fun actorViewClickTest() = runBlocking {
        val view1 = View("view1")

        view1.clickListener = object: View.OnClickListener{

            val dataProvider = DataProvider()

            override fun onClick(view: View) {
                println("onClick enter $view")
                CoroutineScope(coroutineContext).launch {
                    dataProvider.getData().forEach {
                        println("forEach.$it")
                    }
                }
                println("onClick exit $view")
            }
        }

        println("performClick 1")
        view1.performClick()
        println("performClick 2")
        view1.performClick()
        println("performClick 3")

    }

    class DataProvider(val dispatcher: CoroutineDispatcher = Dispatchers.IO){
        suspend fun getData(): List<String> = withContext(dispatcher){
            val result = mutableListOf<String>()
            repeat(10){
                result.add(it.toString())
                delay(100)
            }
            result
        }
    }

    class View(val name:String = "View"){

        var clickListener: OnClickListener? = null

        fun performClick(){
            clickListener?.onClick(this)
        }

        interface OnClickListener{
            fun onClick(view: View)
        }

        override fun toString(): String {
            return name
        }
    }
}