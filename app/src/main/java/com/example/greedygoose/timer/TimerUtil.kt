package com.example.greedygoose.timer

import com.example.greedygoose.mod

class TimerUtil {
    companion object {
        const val RUNNING_NOTIF_ID = 0
        const val EXPIRED_NOTIF_ID = 1

        fun getTimeString(time: Triple<String, String, String>): String {
            return String.format("%02d:%02d:%02d", time.first.toInt(), time.second.toInt(),
                time.third.toInt())
        }
    }
}