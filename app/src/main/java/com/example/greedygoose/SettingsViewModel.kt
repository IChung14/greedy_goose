package com.example.greedygoose

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.greedygoose.data.SPLDAccessModel

class SettingsViewModel(context: Context): ViewModel() {
    private val model = SPLDAccessModel(context = context)
    val eggCount = model.eggCount

    fun decrementEggCount(value: Int){
        model.decrementEggCount(value)
    }
}