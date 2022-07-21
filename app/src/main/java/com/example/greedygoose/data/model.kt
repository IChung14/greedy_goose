package com.example.greedygoose.data

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.NotStartedState
import com.example.greedygoose.timer.PausedState
import com.example.greedygoose.timer.RunningState
import com.example.greedygoose.timer.TimerStateContext


class model {
    private var isFirstCreate = false
    private lateinit var binding: TimerPageBinding
    private lateinit var timerPageContext: Context
    private lateinit var serviceIntent: Intent
    private lateinit var timerStateContext: TimerStateContext
    private lateinit var notStartedState: NotStartedState
    private lateinit var runningState: RunningState
    private lateinit var pausedState: PausedState
    var elapsed_time = 0L
    var set_time = 0L
    lateinit var r_notif_manager: NotificationManager
    // Timer
    var checkBackgroundApps = true
    val unproductiveApplications: MutableMap<String, String> = mutableMapOf()

    fun set_is_first_create(isFirstCreate: Boolean) {
        this.isFirstCreate = isFirstCreate
    }

    fun get_is_first_create(): Boolean {
        return this.isFirstCreate
    }

    fun set_binding(binding: TimerPageBinding) {
        this.binding = binding
    }

    fun get_binding(): TimerPageBinding {
        return this.binding
    }

    fun set_timer_page_context(context: Context) {
        this.timerPageContext = context
    }

    fun get_timer_page_context(): Context {
        return this.timerPageContext
    }

    fun set_service_intent(serviceIntent: Intent) {
        this.serviceIntent = serviceIntent
    }

    fun get_service_intent(): Intent {
        return this.serviceIntent
    }

    fun set_timer_state_context(timerStateContext: TimerStateContext) {
        this.timerStateContext = timerStateContext
    }

    fun get_timer_state_context(): TimerStateContext {
        return this.timerStateContext
    }

    fun set_not_started_state(notStartedState: NotStartedState) {
        this.notStartedState = notStartedState
    }

    fun get_not_started_state(): NotStartedState {
        return this.notStartedState
    }

    fun set_running_state(runningState: RunningState) {
        this.runningState = runningState
    }

    fun get_running_state(): RunningState {
        return this.runningState
    }

    fun set_paused_state(pausedState: PausedState) {
        this.pausedState = pausedState
    }

    fun get_paused_state(): PausedState {
        return this.pausedState
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