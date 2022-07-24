package com.example.greedygoose

import android.content.*
import android.content.res.Resources
import android.os.Bundle
import android.os.IBinder
import android.text.format.DateUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.greedygoose.databinding.TimerPageBinding
import com.example.greedygoose.timer.TimerService
import com.google.android.material.snackbar.Snackbar

class TimerPage : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var binding: TimerPageBinding

    private lateinit var viewModel: TimerViewModel
    private lateinit var timerService: TimerService
    private lateinit var listviewTimerPage: ListView

    private val apps = mutableMapOf<String,String>()

    private var timerBound: Boolean = false
    var arrayAdapter: ArrayAdapter<*>? = null

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            timerBound = true

            timerService.stateTimer.timerState.observe(this@TimerPage){
                it.showUI(binding)
            }
            timerService.stateTimer.elapsedTime.observe(this@TimerPage){
                timerService.stateTimer.timerState.value?.showUI(binding)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            timerBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = TimerViewModel(this)
        binding = TimerPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // when entering TimerPage, instantiate TimerService right away
        startService(Intent(applicationContext, TimerService::class.java))
        bindService(
            Intent(applicationContext, TimerService::class.java),
            connection,
            Context.BIND_AUTO_CREATE
        )

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // *********************************
        // LIST OF APPS ON PHONE STARTS HERE
        // *********************************

        listviewTimerPage = findViewById(R.id.applistview)

        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        // get list of all the apps installed
        val ril = packageManager.queryIntentActivities(mainIntent, 0)
        var appName: String? = null

        // get size of ril and create a list
        for (ri in ril) {
            if (ri.activityInfo != null) {
                // get package
                val res: Resources =
                    packageManager.getResourcesForApplication(ri.activityInfo.applicationInfo)
                // if activity label res is found
                appName = if (ri.activityInfo.labelRes != 0) {
                        res.getString(ri.activityInfo.labelRes)
                    } else {
                        ri.activityInfo.applicationInfo.loadLabel(
                            packageManager
                        ).toString()
                    }
                apps[appName] = ri.activityInfo.packageName
            }
        }

        // set all the apps name in list view
        arrayAdapter = ArrayAdapter(
            this@TimerPage,
            android.R.layout.simple_list_item_multiple_choice,
            apps.keys.toList()
        )
        listviewTimerPage.adapter = arrayAdapter
        listviewTimerPage.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        listviewTimerPage.onItemClickListener = this

        binding.startBtn.setOnClickListener {
            val hrs = binding.userInputHrs.text.toString()
            val mins = binding.userInputMins.text.toString()
            val secs = binding.userInputSecs.text.toString()

            val elapsedHrs = if (hrs.isNotEmpty()) hrs.toLong() * DateUtils.HOUR_IN_MILLIS else 0L
            val elapsedMins = if (mins.isNotEmpty()) mins.toLong() * DateUtils.MINUTE_IN_MILLIS else 0L
            val elapsedSecs = if (secs.isNotEmpty()) secs.toLong() * DateUtils.SECOND_IN_MILLIS else  0L

            if (timerService.stateTimer.elapsedTime.value!! == 0L && (hrs.isEmpty() && mins.isEmpty() && secs.isEmpty() ||
                elapsedHrs+elapsedMins+elapsedSecs == 0L)) {
                Snackbar.make(binding.root,
                    "Please input a time greater than 0 sec",
                    Snackbar.LENGTH_SHORT
                ).show()
            }else{
                // STARTS TIMER
                timerService.onTimerStartPressed(elapsedHrs, elapsedMins, elapsedSecs)
            }
        }

        binding.resetBtn.setOnClickListener {
            timerService.onTimerResetPressed()
        }
    }

    override fun onStop() {
        timerBound = false
        super.onStop()
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }

    override fun onItemClick (parent: AdapterView<*>, view: View, position: Int, id: Long) {
        var unproductiveItem:String = parent.getItemAtPosition(position) as String
        val v = view as CheckedTextView
        val currentCheck = v.isChecked

        val newList = viewModel.currUnprod.value.toMutableList()
        apps[unproductiveItem]?.let {
            if (currentCheck) {
                newList.add(it)
            }
            else {
                newList.remove(it)
            }
        }
        viewModel.setUnproductive(newList)
    }
}