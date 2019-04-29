package com.cools.coroutineexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cools.coroutineexample.databinding.ActivityMainBinding
import com.cools.coroutineexample.databinding.ItemMainDataBinding
import com.cools.coroutineexample.dto.main.MainData
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(MainActivityViewModel::class.java)
    }

    private lateinit var dataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataBinding.viewModel = viewModel
        dataBinding.lifecycleOwner = this
        dataBinding.mainRecyclerView.apply {
            adapter = MainItemAdapter()
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        }
    }

    inner class MainItemViewHolder(val dataBinding: ItemMainDataBinding, view: View): RecyclerView.ViewHolder(view){
        fun bind(mainData: MainData){
            dataBinding.viewModel = mainData
            dataBinding.executePendingBindings()
        }
    }

    inner class MainItemAdapter: RecyclerView.Adapter<MainItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainItemViewHolder {
            val viewGroup = LayoutInflater.from(parent.context).inflate(R.layout.item_main_data, parent, false)
            val dataBinding = DataBindingUtil.bind<ItemMainDataBinding>(viewGroup)
            return MainItemViewHolder(dataBinding!!, dataBinding.root)
        }

        override fun getItemCount(): Int {
            return viewModel.dataLiveData.value?.size ?: 0
        }

        override fun onBindViewHolder(holder: MainItemViewHolder, position: Int) {
            viewModel.dataLiveData.value?.let {
                holder.bind(it[position])
            }
        }
    }
}

@BindingAdapter("bindItems")
fun bindItems(textView: RecyclerView, items: List<MainData>?){
    items?.apply {
        textView.adapter?.also { adapter ->
            adapter.notifyDataSetChanged()
        }
    }
}

@BindingAdapter("bindImage")
fun bindImage(imageView: ImageView, imageURL: String?) {
    imageURL?.let {
        Glide.with(imageView.context)
            .load(it)
            .fitCenter()
            .circleCrop()
            .into(imageView)
    }
}

@BindingAdapter("toDateFormat")
fun convertDateFormat(textView: TextView, timeMs: Long?){
    timeMs?.let {
        textView.text = timeMs.toDateFormat()
    }
}

fun Long.toDateFormat(): String {
    return SimpleDateFormat("yyyy-MM-dd").format(Date(this))
}