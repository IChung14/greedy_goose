package com.example.greedygoose.data

import android.app.NotificationManager


class model {
    var is_tie_selected = false
    var is_goggle_selected = false
    var is_hard_hat_selected = false
    var timer_state = TimerState.NOT_STARTED
    var elapsed_time = 0L
    var set_time = 0L
    lateinit var r_notif_manager: NotificationManager

    fun setTimerState(timer_state: TimerState) {
        this.timer_state = timer_state
    }

    fun get_timer_state(): TimerState {
        return this.timer_state
    }

    fun set_elapsed_time(elapsed_time: Long) {
        this.elapsed_time = elapsed_time
    }

    fun get_elapsed_time(): Long {
        return this.elapsed_time
    }

    fun set_set_time(set_time: Long) {
        this.set_time = set_time
    }

    fun get_set_time(): Long {
        return this.set_time
    }

    fun set_r_notif_manager(r_notif_manager: NotificationManager) {
        this.r_notif_manager = r_notif_manager
    }

    fun get_r_notif_manager(): NotificationManager {
        return this.r_notif_manager
    }
}