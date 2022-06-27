package com.example.greedygoose

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.greedygoose.foreground.FloatingLayout


val theme_map: HashMap<String, HashMap<String, Int>> = hashMapOf(
    "ENG" to hashMapOf(
        "ANGRY_LEFT" to R.drawable.eng_angry_left,
        "ANGRY_LEFT2" to R.drawable.eng_angry_left2,
        "ANGRY_RIGHT" to R.drawable.eng_angry_right,
        "ANGRY_RIGHT2" to R.drawable.eng_angry_left2,
        "BEHIND_LEFT" to R.drawable.eng_behind_left,
        "BEHIND_LEFT2" to R.drawable.eng_behind_left2,
        "BEHIND_RIGHT" to R.drawable.eng_behind_right,
        "BEHIND_RIGHT2" to R.drawable.eng_behind_left2,
        "FLYING_LEFT" to R.drawable.eng_flying_left,
        "FLYING_RIGHT" to R.drawable.eng_flying_right,
        "SITTING_LEFT" to R.drawable.eng_sitting_left,
        "SITTING_RIGHT" to R.drawable.eng_sitting_right,
        "WALKING_LEFT" to R.drawable.eng_walking_left,
        "WALKING_LEFT2" to R.drawable.eng_walking_left2,
        "WALKING_RIGHT" to R.drawable.eng_walking_right,
        "WALKING_RIGHT2" to R.drawable.eng_walking_right2,
        "WINDOW_LEFT" to R.drawable.eng_window_left,
        "WINDOW_RIGHT" to R.drawable.eng_window_right,
    ),
    "MATH" to hashMapOf(
        "ANGRY_LEFT" to R.drawable.math_angry_left,
        "ANGRY_LEFT2" to R.drawable.math_angry_left2,
        "ANGRY_RIGHT" to R.drawable.math_angry_right,
        "ANGRY_RIGHT2" to R.drawable.math_angry_left2,
        "BEHIND_LEFT" to R.drawable.math_behind_left,
        "BEHIND_LEFT2" to R.drawable.math_behind_left2,
        "BEHIND_RIGHT" to R.drawable.math_behind_right,
        "BEHIND_RIGHT2" to R.drawable.math_behind_left2,
        "FLYING_LEFT" to R.drawable.math_flying_left,
        "FLYING_RIGHT" to R.drawable.math_flying_right,
        "SITTING_LEFT" to R.drawable.math_sitting_left,
        "SITTING_RIGHT" to R.drawable.math_sitting_right,
        "WALKING_LEFT" to R.drawable.math_walking_left,
        "WALKING_LEFT2" to R.drawable.math_walking_left2,
        "WALKING_RIGHT" to R.drawable.math_walking_right,
        "WALKING_RIGHT2" to R.drawable.math_walking_right2,
        "WINDOW_LEFT" to R.drawable.math_window_left,
        "WINDOW_RIGHT" to R.drawable.math_window_right,
    ),
    "SCI" to hashMapOf(
        "ANGRY_LEFT" to R.drawable.sci_angry_left,
        "ANGRY_LEFT2" to R.drawable.sci_angry_left2,
        "ANGRY_RIGHT" to R.drawable.sci_angry_right,
        "ANGRY_RIGHT2" to R.drawable.sci_angry_left2,
        "BEHIND_LEFT" to R.drawable.sci_behind_left,
        "BEHIND_LEFT2" to R.drawable.sci_behind_left2,
        "BEHIND_RIGHT" to R.drawable.sci_behind_right,
        "BEHIND_RIGHT2" to R.drawable.sci_behind_left2,
        "FLYING_LEFT" to R.drawable.sci_flying_left,
        "FLYING_RIGHT" to R.drawable.sci_flying_right,
        "SITTING_LEFT" to R.drawable.sci_sitting_left,
        "SITTING_RIGHT" to R.drawable.sci_sitting_right,
        "WALKING_LEFT" to R.drawable.sci_walking_left,
        "WALKING_LEFT2" to R.drawable.sci_walking_left2,
        "WALKING_RIGHT" to R.drawable.sci_walking_right,
        "WALKING_RIGHT2" to R.drawable.sci_walking_right2,
        "WINDOW_LEFT" to R.drawable.sci_window_left,
        "WINDOW_RIGHT" to R.drawable.sci_window_right,
    )
)

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
                theme_map[this.theme.value]?.get("ANGRY_LEFT")
                    ?.let { it1 -> ob.setImageResource(it1) }
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