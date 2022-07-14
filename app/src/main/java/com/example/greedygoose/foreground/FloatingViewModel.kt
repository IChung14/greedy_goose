package com.example.greedygoose.foreground

import android.app.Service
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModel
import com.example.greedygoose.data.SPLDAccessModel

class FloatingViewModel(context: Context): ViewModel() {
    private val model = SPLDAccessModel(context = context)
    private var eggCount = model.eggCount

    fun incrementEggCount(value: Int = 1){
        model.incrementEggCount(value)
    }
}