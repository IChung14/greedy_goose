package com.example.greedygoose.data

import androidx.lifecycle.LiveData
import android.content.SharedPreferences

abstract class SharedPreferenceLiveData<T>(val sharedPrefs: SharedPreferences,
                                           val key: String,
                                           val defValue: T) : LiveData<T>() {

    override fun getValue(): T {
        return getValueFromPreferences(key, defValue)
    }

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == this.key) {
            value = getValueFromPreferences(key, defValue)
        }
    }

    abstract fun getValueFromPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class SharedPreferenceIntLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Int) :
    SharedPreferenceLiveData<Int>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Int): Int = sharedPrefs.getInt(key, defValue)
}

class SharedPreferenceBooleanLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Boolean) :
    SharedPreferenceLiveData<Boolean>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Boolean): Boolean = sharedPrefs.getBoolean(key, defValue)
}

class SharedPreferenceThemeLiveData(sharedPrefs: SharedPreferences, key: String, defValue: Theme) :
    SharedPreferenceLiveData<Theme>(sharedPrefs, key, defValue) {
    override fun getValueFromPreferences(key: String, defValue: Theme): Theme =
        Theme.values()[sharedPrefs.getInt(key, defValue.ordinal)]
}

fun SharedPreferences.intLiveData(key: String, defValue: Int): SharedPreferenceLiveData<Int> {
    return SharedPreferenceIntLiveData(this, key, defValue)
}

fun SharedPreferences.booleanLiveData(key: String, defValue: Boolean): SharedPreferenceLiveData<Boolean> {
    return SharedPreferenceBooleanLiveData(this, key, defValue)
}

fun SharedPreferences.themeLiveData(key: String, defValue: Theme): SharedPreferenceLiveData<Theme> {
    return SharedPreferenceThemeLiveData(this, key, defValue)
}