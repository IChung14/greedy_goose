package com.example.greedygoose.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import com.example.greedygoose.R
import com.example.greedygoose.TimerState
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.mod


/*
TODO:
Problem: When user goes back to home page, the user input is empty and doesn't reflect the time they picked before
 - Change set_time to hrs, mins, secs ints and update userInput textviews.
*/

class TimerPage : AppCompatActivity() {

    private lateinit var binding: TimerPageBinding
    private lateinit var serviceIntent: Intent
    private var RUNNING_NOTIF_ID = 0
    private var EXPIRED_NOTIF_ID = 1

    public lateinit var listviewTimerPage: ListView
    var arrayAdapter: ArrayAdapter<*>? = null
//    var text: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (mod.get_timer_state() == TimerState.PAUSED) {
            showTimer()
            binding.startBtn.text = "RESUME"
        } else if (mod.get_timer_state() == TimerState.RUNNING) {
            showTimer()
            binding.startBtn.text = "PAUSE"
        }

        binding.startBtn.setOnClickListener {
            if (mod.get_timer_state() == TimerState.PAUSED) {
                resumeTimer()
            }
            else if (mod.get_timer_state() == TimerState.RUNNING) {
                pauseTimer()
            }
            else if (mod.get_timer_state() == TimerState.NOT_STARTED) {
                val hrs = binding.userInputHrs.text.toString()
                val mins = binding.userInputMins.text.toString()
                val secs = binding.userInputSecs.text.toString()

                // TODO: handle cases for above 60
                var elapsedHrs = 0L
                var elapsedMins = 0L
                var elapsedSecs = 0L

                if (hrs.isNotEmpty()) {
                    elapsedHrs = hrs.toLong() * DateUtils.HOUR_IN_MILLIS
                }

                if (mins.isNotEmpty()) {
                    elapsedMins = mins.toLong() * DateUtils.MINUTE_IN_MILLIS
                }

                if (secs.isNotEmpty()) {
                    elapsedSecs = secs.toLong() * DateUtils.SECOND_IN_MILLIS
                }

                mod.set_elapsed_time(elapsedHrs + elapsedMins + elapsedSecs)
                mod.set_set_time(elapsedHrs + elapsedMins + elapsedSecs)

                if (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() || mod.get_elapsed_time() == 0L) {
                    // TODO: Add snackbar to tell user to input a valid time
                    return@setOnClickListener
                }

                mod.set_r_notif_manager(NotificationUtil.showTimerRunning(this@TimerPage))
                NotificationUtil.removeNotifiation(EXPIRED_NOTIF_ID)
                startTimer()
            }
        }

        binding.resetBtn.setOnClickListener {
            NotificationUtil.removeNotifiation(EXPIRED_NOTIF_ID)
            resetTimer()
        }

        serviceIntent = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))

        // *********************************
        // *********************************
        // LIST OF APPS ON PHONE STARTS HERE
        // *********************************
        // *********************************

        listviewTimerPage = findViewById(R.id.applistview);
//        text = findViewById(R.id.appTextView);

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
        arrayAdapter = ArrayAdapter(this@TimerPage,
            R.layout.support_simple_spinner_dropdown_item, apps
        )
        listviewTimerPage.adapter = arrayAdapter

//        listviewTimerPage.setAdapter(
//            ArrayAdapter(
//                this@TimerPage,
//                android.R.layout.simple_list_item_1,
//                apps
//            )
//        )
        println("WOOHOO")
        println(apps.size.toString())
        println(apps[0])
        println(apps[1])
        println(apps[2])
        println(apps[3])

        println(listviewTimerPage.adapter.count)

//        listView.setAdapter(
//            ArrayAdapter(
//                this@MainActivity,
//                android.R.layout.simple_list_item_1,
//                apps
//            )
//        )
//        // write total count of apps available.
//        text.setText(ril.size.toString() + " Apps are installed")

    }

//    public fun getallapps(view: View) {
//        val mainIntent = Intent(Intent.ACTION_MAIN, null)
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
//
//        // get list of all the apps installed
//        val ril = packageManager.queryIntentActivities(mainIntent, 0)
//        val componentList: List<String> = ArrayList()
//        var name: String? = null
//        var i = 0
//
//        // get size of ril and create a list
//        val apps = arrayOfNulls<String>(ril.size)
//        for (ri in ril) {
//            if (ri.activityInfo != null) {
//                // get package
//                val res: Resources =
//                    packageManager.getResourcesForApplication(ri.activityInfo.applicationInfo)
//                // if activity label res is found
//                name = if (ri.activityInfo.labelRes != 0) {
//                    res.getString(ri.activityInfo.labelRes)
//                } else {
//                    ri.activityInfo.applicationInfo.loadLabel(
//                        packageManager
//                    ).toString()
//                }
//                apps[i] = name
//                i++
//            }
//        }
//        // set all the apps name in list view
//        listviewTimerPage.setAdapter(
//            ArrayAdapter(
//                this@TimerPage,
//                android.R.layout.simple_list_item_1,
//                apps
//            )
//        )
//
//    }

    private fun pauseTimer() {
        binding.startBtn.text = "RESUME"
        NotificationUtil.updateNotification(this@TimerPage, "Timer is paused")
        stopService(serviceIntent)
        mod.set_timer_state(TimerState.PAUSED)
    }

    private fun resumeTimer() {
        binding.startBtn.text = "PAUSE"
        startTimer()
        mod.set_timer_state(TimerState.RUNNING)
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, mod.get_elapsed_time())
        startService(serviceIntent)

        mod.set_timer_state(TimerState.RUNNING)
        binding.startBtn.text = "PAUSE"
        showTimer()
    }

    private fun resetTimer() {
        binding.startBtn.text = "START"
        showUserInput()
        NotificationUtil.removeNotifiation(RUNNING_NOTIF_ID)
        stopService(serviceIntent)
        mod.set_elapsed_time(mod.get_set_time())
        mod.set_timer_state(TimerState.NOT_STARTED)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent)
        {
            mod.set_elapsed_time(intent.getLongExtra(TimerService.TIME_EXTRA, 0L))
            updateTextUI()

            if (mod.get_elapsed_time() <= 0L) {
                NotificationUtil.removeNotifiation(RUNNING_NOTIF_ID)
                NotificationUtil.showTimerExpired(this@TimerPage)
                resetTimer()
            } else {
                NotificationUtil.updateNotification(this@TimerPage, "Timer is running")
            }
        }
    }

    private fun updateTextUI() {
        val hr = mod.get_elapsed_time()/1000/3600
        val min = (mod.get_elapsed_time()/1000 - hr*3600) / 60
        val sec = (mod.get_elapsed_time()/1000) % 60

        binding.timerText.text = String.format("%02d:%02d:%02d", hr, min, sec)
    }

    private fun showTimer() {
        updateTextUI()
        binding.userInputHrs.visibility = View.INVISIBLE
        binding.userInputMins.visibility = View.INVISIBLE
        binding.userInputSecs.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
    }

    private fun showUserInput() {
        binding.userInputHrs.visibility = View.VISIBLE
        binding.userInputMins.visibility = View.VISIBLE
        binding.userInputSecs.visibility = View.VISIBLE
        binding.timerText.visibility = View.INVISIBLE
    }
}
