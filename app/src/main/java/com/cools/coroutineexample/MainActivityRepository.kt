package com.cools.coroutineexample

import android.content.Context
import com.cools.coroutineexample.dto.main.MainData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivityRepository(private val context: Context, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val list = mutableListOf<MainData>()

    suspend fun insert(mainData: MainData): Int = withContext(dispatcher){
        Thread.sleep(20)
        list.add(mainData)
        1
    }

    suspend fun findAll(): List<MainData> = withContext(dispatcher){
        delay(1000)
        list
    }

    suspend fun updateData() = withContext(dispatcher){
        list.clear()
        repeat(10){
            list.add(
                MainData("name $it", "description.$it", "http://dimg.donga.com/ugc/CDB/WEEKLY/Article/5a/bd/ea/eb/5abdeaeb2123d2738de6.jpg", System.currentTimeMillis())
            )
            delay(200)
        }
    }
}