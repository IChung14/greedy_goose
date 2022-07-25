package com.example.greedygoose

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.greedygoose.data.SPLDAccessModel

class TimerViewModel(context: Context): ViewModel() {
    private val model = SPLDAccessModel(context = context)

    val currUnprod = model.unproductive
    fun setUnproductive(list: List<String>) = model.setUnproductive(list)

    init {
        setUnproductive(listOf())
    }
}