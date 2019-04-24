package com.cools.coroutineexample.main

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.cools.coroutineexample.MainActivityViewModel
import kotlinx.coroutines.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityViewModelTest {
    private val lifecycleOwner = LifecycleOwnerImpl()
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() = runBlocking(Dispatchers.Main) {
        val application = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        viewModel = MainActivityViewModel(application)
        lifecycleOwner.create()
    }

    @After
    fun release() = runBlocking(Dispatchers.Main){
        lifecycleOwner.destroy()
    }

    @Test
    fun initTest() = runBlocking<Unit>{
        launch(Dispatchers.Main) {
            viewModel.initialized()
            viewModel.dataLiveData.observe(lifecycleOwner, Observer {
                Assert.assertNotNull(viewModel.dataLiveData.value)
                cancel()
            })
            delay(Long.MAX_VALUE)
        }
    }

    @Test
    fun updateTest() = runBlocking<Unit> {
        launch(Dispatchers.Main) {
            viewModel.initialized()
            viewModel.updateData()

            viewModel.dataLiveData.observe(lifecycleOwner, Observer {
                Assert.assertNotNull(viewModel.dataLiveData.value)
                if(viewModel.dataLiveData.value?.isNotEmpty() == true){
                    cancel()
                }
            })
            delay(Long.MAX_VALUE)
        }
    }

    class LifecycleOwnerImpl: LifecycleOwner{
        private val lifeCycle = LifecycleRegistry(this)

        fun create(){
            lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
            lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }

        fun destroy(){
            lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            lifeCycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }

        override fun getLifecycle(): Lifecycle {
            return lifeCycle
        }

    }
}