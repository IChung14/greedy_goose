package com.example.greedygoose

import androidx.lifecycle.ViewModel

class TimerViewModel(): ViewModel() {
    var unproductiveApplications: MutableMap<String, String> = mutableMapOf()
}