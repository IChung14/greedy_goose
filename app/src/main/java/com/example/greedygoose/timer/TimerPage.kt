package com.example.greedygoose.timer

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.R
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod
import java.util.*

/*
TODO:
1. Problem: When user goes back to home page, the user input is empty and doesn't reflect the time they picked before
 - Change set_time to hrs, mins, secs ints and update userInput textviews.
 2. Calculation of hr,min,sec is repeated a lot, could make a TimerUtil for these types of functions. Maybe combine with constants
 3. Change timerstate to not nullable
 4. When user exits app, remove all notifs
*/

class TimerPage : AppCompatActivity(), AdapterView.OnItemClickListener {

    companion object {
        fun snoozeAlarm(context: Context) {
            mod.set_elapsed_time(300000L)
            mod.get_service_intent().putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
            context.startService(mod.get_service_intent())
            mod.get_timer_state_context().setState(mod.get_running_state())
            mod.get_timer_state_context().getState()?.showUI()
        }

        fun stopAlarm() {
            mod.get_timer_state_context().getState()?.resetTimer()
            mod.get_timer_state_context().getState()?.showUI()
        }
    }

    public lateinit var listviewTimerPage: ListView
    private lateinit var timerHelper: TimerHelper
    var arrayAdapter: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mod.set_binding(TimerPageBinding.inflate(layoutInflater))
        setContentView(mod.get_binding().root)

        mod.set_service_intent(Intent(applicationContext, TimerService::class.java))

        mod.set_timer_page_context(this@TimerPage)

        if (!mod.get_is_first_create()) {
            registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

            mod.set_timer_state_context(TimerStateContext())

            mod.set_not_started_state(NotStartedState())

            mod.set_running_state(RunningState())

            mod.set_paused_state(PausedState())

            mod.get_timer_state_context().setState(mod.get_not_started_state())

            mod.set_is_first_create(true)
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mod.get_timer_state_context().getState()?.showUI()

        mod.get_binding().startBtn.setOnClickListener {
            mod.get_timer_state_context().getState()?.nextAction()
            mod.get_timer_state_context().getState()?.showUI()

            timerHelper = TimerHelper(this)
            timerHelper.runGetAppService()
        }

        mod.get_binding().resetBtn.setOnClickListener {
            mod.get_timer_state_context().getState()?.resetTimer()
            mod.get_timer_state_context().getState()?.showUI()
            mod.checkBackgroundApps = false
        }

        // *********************************
        // *********************************
        // LIST OF APPS ON PHONE STARTS HERE
        // *********************************
        // *********************************

        listviewTimerPage = findViewById(R.id.applistview);

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        // get list of all the apps installed
        val ril = packageManager.queryIntentActivities(mainIntent, 0)
        var name: String? = null
        var i = 0

        // get size of ril and create a list
        val apps = arrayOfNulls<String>(ril.size)
        for (ri in ril) {
            if (ri.activityInfo != null) {
                // get package
                val res: Resources =
                    packageManager.getResourcesForApplication(ri.activityInfo.applicationInfo)
                // if activity label res is found
                name = if (ri.activityInfo.labelRes != 0) {
                    res.getString(ri.activityInfo.labelRes)
                } else {
                    ri.activityInfo.applicationInfo.loadLabel(
                        packageManager
                    ).toString()
                }
                apps[i] = name
                i++
            }
        }

        // set all the apps name in list view
        arrayAdapter = ArrayAdapter(
            this@TimerPage,
            android.R.layout.simple_list_item_multiple_choice,
            apps
        )
        listviewTimerPage.adapter = arrayAdapter
        listviewTimerPage.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listviewTimerPage.onItemClickListener = this

    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            mod.set_elapsed_time(intent.getLongExtra(TimerService.TIME_EXTRA, 0L))
            mod.get_timer_state_context().getState()?.showUI()

            if (mod.get_elapsed_time() <= 0L) {
                NotificationUtil.removeNotification(TimerUtil.RUNNING_NOTIF_ID)
                NotificationUtil.showTimerExpired()
                stopService(mod.get_service_intent())

//                // instantiate goose with angry flag on
//                val floatingIntent = Intent(this@TimerPage, FloatingService::class.java)
//                floatingIntent.putExtra("angry", true)
//                this@TimerPage.startService(floatingIntent)
            } else {
                NotificationUtil.updateNotification("Timer is running")
            }
        }
    }

    override fun onItemClick (parent: AdapterView<*>, view: View, position: Int, id: Long) {
        var unproductive_item:String = parent.getItemAtPosition(position) as String
        val v = view as CheckedTextView
        val currentCheck = v.isChecked
        if (currentCheck) {
            mod.unproductiveApplications.set(unproductive_item, unproductive_item)
        }
        else {
            mod.unproductiveApplications.remove(unproductive_item)
        }
    }
}