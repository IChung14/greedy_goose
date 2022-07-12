package com.example.greedygoose.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager

class SPLDAccessModel(val context: Context) {

    private val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)

    val EggCount: LiveData<Int>
        get() = SharedPreferenceIntLiveData(sharedPreference, ModelKeys.EGG_COUNT.key, 0)

    fun setEggCount(num: Int){
        with(sharedPreference.edit()){
            putInt(ModelKeys.EGG_COUNT.key, num)
            apply()
        }
    }
}

enum class ModelKeys(val key: String){
    EGG_COUNT("EggCount"),
}