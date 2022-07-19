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
    var isFirstCreate = false
    lateinit var binding: TimerPageBinding
    lateinit var timerPageContext: Context
    lateinit var serviceIntent: Intent
    lateinit var timerStateContext: TimerStateContext
    lateinit var notStartedState: NotStartedState
    lateinit var runningState: RunningState
    lateinit var pausedState: PausedState
    var elapsed_time = 0L
    var set_time = 0L
    lateinit var r_notif_manager: NotificationManager

}