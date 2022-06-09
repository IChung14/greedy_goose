package com.example.greedygoose;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler


class Entertainment() : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entertainment)
        Handler().postDelayed({finish()}, 5000)
    }
}
