package com.example.greedygoose

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.greedygoose.data.SPLDAccessModel
import com.example.greedygoose.data.Theme

class SettingsViewModel(context: Context): ViewModel() {
    private val model = SPLDAccessModel(context = context)
    val eggCount = model.eggCount
    val theme = model.theme

    fun decrementEggCount(value: Int){
        model.setEggCount(model.eggCount.value - value)
    }

    fun setTheme(theme: Theme){
        model.setTheme(theme)
    }
}