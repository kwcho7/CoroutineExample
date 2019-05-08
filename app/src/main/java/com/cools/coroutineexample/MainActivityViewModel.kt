package com.cools.coroutineexample

import android.app.Application
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cools.coroutineexample.dto.main.MainData
import com.cools.coroutineexample.log.logv
import kotlinx.coroutines.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainActivityRepository(context = application)
    val isLoading = ObservableInt(View.GONE)
    private val job = Job()
    private val exceptionHandler = CoroutineExceptionHandler{ context, throwable ->
        logv("exceptionHandler.${throwable}")
    }

    private val scope = CoroutineScope(Dispatchers.Main + job + exceptionHandler)

    val dataLiveData = MutableLiveData<List<MainData>>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            postValue(repository.findAll())
        }
    }

    fun initialized() {
    }

    override fun onCleared() {
        super.onCleared()
        scope.coroutineContext.cancelChildren()
    }

    fun updateData() = scope.launch{
        isLoading.set(View.VISIBLE)
        repository.updateData()
        dataLiveData.postValue(repository.findAll())
        isLoading.set(View.GONE)
    }
}