package com.example.greedygoose.data

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import com.example.greedygoose.R

const val DEFAULT_EGG_COUNT = 100

class SPLDAccessModel(val context: Context) {

    private val sharedPreference =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    val eggCount = sharedPreference.intLiveData(ModelKeys.EGG_COUNT.key, DEFAULT_EGG_COUNT)
    fun setEggCount(num: Int){
        with(sharedPreference.edit()){
            putInt(ModelKeys.EGG_COUNT.key, num)
            apply()
        }
    }

    val theme = sharedPreference.themeLiveData(ModelKeys.THEME.key, Theme.NONE)
    fun setTheme(theme: Theme){
        with(sharedPreference.edit()){
            putInt(ModelKeys.THEME.key, theme.ordinal)
            apply()
        }
    }

    val purchased = mapOf(
        Theme.MATH to sharedPreference.booleanLiveData(Theme.MATH.key, false),
        Theme.SCI to sharedPreference.booleanLiveData(Theme.SCI.key, false),
        Theme.ENG to sharedPreference.booleanLiveData(Theme.ENG.key, false)
    )
    fun setPurchased(item: Theme){
        with(sharedPreference.edit()){
            putBoolean(item.key, true)
            apply()
        }
    }

}

enum class Theme(val key: String){
    MATH("math"),
    SCI("sci"),
    ENG("eng"),
    NONE("none")
}

enum class ModelKeys(val key: String){
    EGG_COUNT("EggCount"),
    THEME("Theme"),
}

val memes: List<Int> = listOf(
    R.drawable.meme_1, R.drawable.meme_2, R.drawable.meme_3, R.drawable.meme_4,
    R.drawable.meme_5, R.drawable.meme_6, R.drawable.meme_7
)

enum class TimerState {
    NOT_STARTED, RUNNING, PAUSED
}

enum class Action {
    ANGRY_LEFT, ANGRY_LEFT2, ANGRY_RIGHT, ANGRY_RIGHT2, BEHIND_LEFT, BEHIND_LEFT2, BEHIND_RIGHT,
    BEHIND_RIGHT2, FLYING_LEFT, FLYING_RIGHT, SITTING_LEFT, SITTING_RIGHT, WALKING_LEFT, WALKING_LEFT2,
    WALKING_RIGHT, WALKING_RIGHT2, WINDOW_LEFT, WINDOW_RIGHT, ANGRY_LEFT_MIDDLE, ANGRY_RIGHT_MIDDLE,
    WALKING_LEFT_MIDDLE, WALKING_RIGHT_MIDDLE
}

enum class Direction {
    LEFT, RIGHT
}

val themeMap: HashMap<Theme, HashMap<Action, Int>> = hashMapOf(
    Theme.ENG to hashMapOf(
        Action.ANGRY_LEFT to R.drawable.eng_angry_left,
        Action.ANGRY_LEFT2 to R.drawable.eng_angry_left2,
        Action.ANGRY_RIGHT to R.drawable.eng_angry_right,
        Action.ANGRY_RIGHT2 to R.drawable.eng_angry_right2,
        Action.BEHIND_LEFT to R.drawable.eng_behind_left,
        Action.BEHIND_LEFT2 to R.drawable.eng_behind_left2,
        Action.BEHIND_RIGHT to R.drawable.eng_behind_right,
        Action.BEHIND_RIGHT2 to R.drawable.eng_behind_right2,
        Action.FLYING_LEFT to R.drawable.eng_flying_left,
        Action.FLYING_RIGHT to R.drawable.eng_flying_right,
        Action.SITTING_LEFT to R.drawable.eng_sitting_left,
        Action.SITTING_RIGHT to R.drawable.eng_sitting_right,
        Action.WALKING_LEFT to R.drawable.eng_walking_left,
        Action.WALKING_LEFT2 to R.drawable.eng_walking_left2,
        Action.WALKING_RIGHT to R.drawable.eng_walking_right,
        Action.WALKING_RIGHT2 to R.drawable.eng_walking_right2,
        Action.WINDOW_LEFT to R.drawable.eng_window_left,
        Action.WINDOW_RIGHT to R.drawable.eng_window_right,
        Action.ANGRY_LEFT_MIDDLE to R.drawable.eng_angry_leftmiddle,
        Action.ANGRY_RIGHT_MIDDLE to R.drawable.eng_angry_rightmiddle,
        Action.WALKING_LEFT_MIDDLE to R.drawable.eng_walking_leftmiddle,
        Action.WALKING_RIGHT_MIDDLE to R.drawable.eng_walking_rightmiddle,
    ),
    Theme.MATH to hashMapOf(
        Action.ANGRY_LEFT to R.drawable.math_angry_left,
        Action.ANGRY_LEFT2 to R.drawable.math_angry_left2,
        Action.ANGRY_RIGHT to R.drawable.math_angry_right,
        Action.ANGRY_RIGHT2 to R.drawable.math_angry_right2,
        Action.BEHIND_LEFT to R.drawable.math_behind_left,
        Action.BEHIND_LEFT2 to R.drawable.math_behind_left2,
        Action.BEHIND_RIGHT to R.drawable.math_behind_right,
        Action.BEHIND_RIGHT2 to R.drawable.math_behind_right2,
        Action.FLYING_LEFT to R.drawable.math_flying_left,
        Action.FLYING_RIGHT to R.drawable.math_flying_right,
        Action.SITTING_LEFT to R.drawable.math_sitting_left,
        Action.SITTING_RIGHT to R.drawable.math_sitting_right,
        Action.WALKING_LEFT to R.drawable.math_walking_left,
        Action.WALKING_LEFT2 to R.drawable.math_walking_left2,
        Action.WALKING_RIGHT to R.drawable.math_walking_right,
        Action.WALKING_RIGHT2 to R.drawable.math_walking_right2,
        Action.WINDOW_LEFT to R.drawable.math_window_left,
        Action.WINDOW_RIGHT to R.drawable.math_window_right,
        Action.ANGRY_LEFT_MIDDLE to R.drawable.math_angry_leftmiddle,
        Action.ANGRY_RIGHT_MIDDLE to R.drawable.math_angry_rightmiddle,
        Action.WALKING_LEFT_MIDDLE to R.drawable.math_walking_leftmiddle,
        Action.WALKING_RIGHT_MIDDLE to R.drawable.math_walking_rightmiddle,
    ),
    Theme.SCI to hashMapOf(
        Action.ANGRY_LEFT to R.drawable.sci_angry_left,
        Action.ANGRY_LEFT2 to R.drawable.sci_angry_left2,
        Action.ANGRY_RIGHT to R.drawable.sci_angry_right,
        Action.ANGRY_RIGHT2 to R.drawable.sci_angry_right2,
        Action.BEHIND_LEFT to R.drawable.sci_behind_left,
        Action.BEHIND_LEFT2 to R.drawable.sci_behind_left2,
        Action.BEHIND_RIGHT to R.drawable.sci_behind_right,
        Action.BEHIND_RIGHT2 to R.drawable.sci_behind_right2,
        Action.FLYING_LEFT to R.drawable.sci_flying_left,
        Action.FLYING_RIGHT to R.drawable.sci_flying_right,
        Action.SITTING_LEFT to R.drawable.sci_sitting_left,
        Action.SITTING_RIGHT to R.drawable.sci_sitting_right,
        Action.WALKING_LEFT to R.drawable.sci_walking_left,
        Action.WALKING_LEFT2 to R.drawable.sci_walking_left2,
        Action.WALKING_RIGHT to R.drawable.sci_walking_right,
        Action.WALKING_RIGHT2 to R.drawable.sci_walking_right2,
        Action.WINDOW_LEFT to R.drawable.sci_window_left,
        Action.WINDOW_RIGHT to R.drawable.sci_window_right,
        Action.ANGRY_LEFT_MIDDLE to R.drawable.sci_angry_leftmiddle,
        Action.ANGRY_RIGHT_MIDDLE to R.drawable.sci_angry_rightmiddle,
        Action.WALKING_LEFT_MIDDLE to R.drawable.sci_walking_leftmiddle,
        Action.WALKING_RIGHT_MIDDLE to R.drawable.sci_walking_rightmiddle,
    ),
    Theme.NONE to hashMapOf(
        Action.ANGRY_LEFT to R.drawable.none_angry_left,
        Action.ANGRY_LEFT2 to R.drawable.none_angry_left2,
        Action.ANGRY_RIGHT to R.drawable.none_angry_right,
        Action.ANGRY_RIGHT2 to R.drawable.none_angry_right2,
        Action.BEHIND_LEFT to R.drawable.none_behind_left,
        Action.BEHIND_LEFT2 to R.drawable.none_behind_left2,
        Action.BEHIND_RIGHT to R.drawable.none_behind_right,
        Action.BEHIND_RIGHT2 to R.drawable.none_behind_right2,
        Action.FLYING_LEFT to R.drawable.none_flying_left,
        Action.FLYING_RIGHT to R.drawable.none_flying_right,
        Action.SITTING_LEFT to R.drawable.none_sitting_left,
        Action.SITTING_RIGHT to R.drawable.none_sitting_right,
        Action.WALKING_LEFT to R.drawable.none_walking_left,
        Action.WALKING_LEFT2 to R.drawable.none_walking_left2,
        Action.WALKING_RIGHT to R.drawable.none_walking_right,
        Action.WALKING_RIGHT2 to R.drawable.none_walking_right2,
        Action.WINDOW_LEFT to R.drawable.none_window_left,
        Action.WINDOW_RIGHT to R.drawable.none_window_right,
        Action.ANGRY_LEFT_MIDDLE to R.drawable.none_angry_leftmiddle,
        Action.ANGRY_RIGHT_MIDDLE to R.drawable.none_angry_rightmiddle,
        Action.WALKING_LEFT_MIDDLE to R.drawable.none_walking_leftmiddle,
        Action.WALKING_RIGHT_MIDDLE to R.drawable.none_walking_rightmiddle,
    )
)