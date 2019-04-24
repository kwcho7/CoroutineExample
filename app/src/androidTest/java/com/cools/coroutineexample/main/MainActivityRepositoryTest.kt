package com.cools.coroutineexample.main

import androidx.test.platform.app.InstrumentationRegistry
import com.cools.coroutineexample.MainActivityRepository
import com.cools.coroutineexample.dto.main.MainData
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MainActivityRepositoryTest {

    private lateinit var mainActivityRepository:MainActivityRepository

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().context
        mainActivityRepository = MainActivityRepository(context)
    }

    @Test
    fun insertTest() = runBlocking{
        val result = mainActivityRepository.insert(
            MainData(
            "name1", "Name1 description", "http://dimg.donga.com/ugc/CDB/WEEKLY/Article/5a/bd/ea/eb/5abdeaeb2123d2738de6.jpg", System.currentTimeMillis()
            )
        )
        Assert.assertEquals(1, result)
    }

    @Test
    fun insertMultipleTest() = runBlocking {
        var result = 0
        repeat(100){
            val insertResult = mainActivityRepository.insert(
                MainData(
                    "name $it", "Name $it description", "http://dimg.donga.com/ugc/CDB/WEEKLY/Article/5a/bd/ea/eb/5abdeaeb2123d2738de6.jpg", System.currentTimeMillis()
                )
            )
            result += insertResult
        }
        Assert.assertEquals(100, result)
    }

    @Test
    fun finaAllDataTest() = runBlocking {
        val mainDatas = mainActivityRepository.findAll()
        Assert.assertEquals(0, mainDatas.size)
    }

    @Test
    fun insertAndFindTest() = runBlocking{
        insertMultipleTest()
        val mainDatas = mainActivityRepository.findAll()
        Assert.assertEquals(100, mainDatas.size)
    }
}