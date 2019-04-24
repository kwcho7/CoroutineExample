package com.cools.coroutineexample

import android.app.Application
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cools.coroutineexample.dto.main.MainData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainActivityRepository(context = application)
    val isLoading = ObservableInt(View.GONE)

    val dataLiveData = MutableLiveData<List<MainData>>().apply {
        CoroutineScope(Dispatchers.Main).launch {
            postValue(repository.findAll())
        }
    }

    fun initialized() {
    }

    fun updateData() = GlobalScope.launch(Dispatchers.Main){
        isLoading.set(View.VISIBLE)
        repository.updateData()
        dataLiveData.postValue(repository.findAll())
        isLoading.set(View.GONE)
    }
}