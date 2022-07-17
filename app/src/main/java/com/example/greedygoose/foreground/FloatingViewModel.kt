package com.example.greedygoose.foreground

import android.app.Service
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greedygoose.data.Action
import com.example.greedygoose.data.SPLDAccessModel

class FloatingViewModel(context: Context): ViewModel() {
    private val model = SPLDAccessModel(context = context)
    val eggCount = model.eggCount
    val theme = model.theme
    val action = MutableLiveData(Action.WALKING_LEFT)

    fun incrementEggCount(value: Int = 1){
        model.setEggCount(model.eggCount.value + value)
    }
    fun decrementEggCount(value: Int){
        model.setEggCount(model.eggCount.value - value)
    }
}