package com.example.greedygoose.timer

import com.example.greedygoose.mod

class TimerUtil {
    companion object {
        const val ACTION_STOP = "stop"
        const val ACTION_SNOOZE = "snooze"
        const val RUNNING_NOTIF_ID = 0
        const val EXPIRED_NOTIF_ID = 1

        fun getTime(): Triple<String, String, String> {
            val hr = mod.elapsed_time/1000/3600
            val min = (mod.elapsed_time/1000 - hr*3600) / 60
            val sec = (mod.elapsed_time/1000) % 60

            return Triple(hr.toString(), min.toString(), sec.toString())
        }

        fun getTimeString(): String {
            val time = getTime()
            return String.format("%02d:%02d:%02d", time.first.toInt(), time.second.toInt(),
                time.third.toInt())
        }
    }
}