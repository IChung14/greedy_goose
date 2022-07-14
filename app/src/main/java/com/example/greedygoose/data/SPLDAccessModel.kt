package com.example.greedygoose.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager

const val DEFAULT_EGG_COUNT = 100

class SPLDAccessModel(val context: Context) {

    private val sharedPreference =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    val eggCount: LiveData<Int> = SharedPreferenceIntLiveData(
        sharedPreference,
        ModelKeys.EGG_COUNT.key,
        DEFAULT_EGG_COUNT
    )

    fun incrementEggCount(num: Int){
        with(sharedPreference.edit()){
            putInt(
                ModelKeys.EGG_COUNT.key,
                sharedPreference.getInt(ModelKeys.EGG_COUNT.key,DEFAULT_EGG_COUNT)+num
            )
            apply()
        }
    }

    fun decrementEggCount(num: Int){
        with(sharedPreference.edit()){
            putInt(
                ModelKeys.EGG_COUNT.key,
                sharedPreference.getInt(ModelKeys.EGG_COUNT.key,DEFAULT_EGG_COUNT)-num
            )
            apply()
        }
    }
}

enum class ModelKeys(val key: String){
    EGG_COUNT("EggCount"),
}