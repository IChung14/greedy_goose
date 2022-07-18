package com.example.greedygoose

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.greedygoose.data.SPLDAccessModel
import com.example.greedygoose.data.Theme
import java.lang.Math.random
import kotlin.random.Random

class SettingsViewModel(context: Context): ViewModel() {
    private val model = SPLDAccessModel(context = context)
    val eggCount = model.eggCount
    val theme = model.theme
    val purchased = model.purchased

    init{
        setPurchased(Theme.MATH, 0)
    }

    fun setTheme(theme: Theme){
        model.setTheme(theme)
    }
    fun setPurchased(item: Theme, price: Int): Boolean{
        if(model.eggCount.value - price>=0){
            model.setPurchased(item)
            model.setEggCount(model.eggCount.value - price)
            return true
        }
        return false
    }

    // get appropriate accessory button image src
    @DrawableRes
    fun getThemeSrc(theme: Theme): Int{
        val isSelected = this.theme.value == theme
        val isActive = this.purchased[theme]?.value == true
        return when (theme) {
            Theme.MATH ->
                if (isSelected) R.drawable.highlighted_tie
                else if (!isActive) R.drawable.grey_tie
                else R.drawable.pink_tie
            Theme.SCI ->
                if (isSelected) R.drawable.highlighted_goggles
                else if (!isActive) R.drawable.grey_goggles
                else R.drawable.science_goggle
            else -> // ENG
                if (isSelected) R.drawable.highlighted_hardhat
                else if (!isActive) R.drawable.grey_hardhat
                else R.drawable.yellow_hard_hat
        }
    }
}