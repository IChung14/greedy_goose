package com.example.greedygoose

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.greedygoose.foreground.FloatingLayout

class model {
    private var egg_count = MutableLiveData<Int>(100)
    private var theme = MutableLiveData<String>("MATH")
    private var entertainment = MutableLiveData<Boolean>(false)
    private var floatingLayout: FloatingLayout? = null

    fun increase_egg_count(amt: Int) {
        this.egg_count.value = this.egg_count.value?.plus(amt)
    }

    fun decrease_egg_count(amt: Int) {
        this.egg_count.value = this.egg_count.value?.minus(amt)

    }

    fun get_egg_count(): Int? {
        return this.egg_count.value
    }

    // Pass in a UI element that you want to be updated based on the egg_count
    // For now, only taking in a textview
    fun observe_egg(context: LifecycleOwner, ob: TextView) {
        this.egg_count.observe(context, Observer {
            ob.text = this.egg_count.value.toString()
        })
    }

    fun set_theme(theme: String) {
        this.theme.value = theme
    }

    fun get_theme(): MutableLiveData<String> {
        return this.theme
    }

    fun toggle_theme() {
        if (this.theme.value == "MATH") {
            this.theme.value = "ENG"
        } else {
            this.theme.value = "MATH"
        }
    }

    // Pass in a UI element that you want to be updated based on the egg_count
    // For now, only taking in a textview
    fun observe_theme(context: LifecycleOwner, ob: ImageView, type: String) {
        this.theme.observe(context, Observer {
            if (this.theme.value == "ENG") {
                ob.setImageResource(R.drawable.math_angry_left)
            } else {
                ob.setImageResource(R.drawable.sci_angry_left)
            }
        })
    }

    fun set_entertainment(b: Boolean) {
        this.entertainment.value = b
    }

    fun observe_entertainment(life: LifecycleOwner, context: Context) {
        this.entertainment.observe(life, Observer {
            if (it == true) {
                mod.floatingLayout = FloatingLayout(context, R.drawable.egg_small, life)
                mod.floatingLayout!!.setView()
            }
        })
    }
}