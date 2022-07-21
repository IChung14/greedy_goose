package com.example.greedygoose.timer

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.greedygoose.mod

class TimerHelper(private var context: Context ) {

    private var usageStatsManager : UsageStatsManager = context.getSystemService( Context.USAGE_STATS_SERVICE ) as UsageStatsManager
    private var packageManager : PackageManager = context.packageManager
    private var nonSystemAppInfoMap : Map<String,String>
    private val openApplicationsMap: MutableMap<String, String> = mutableMapOf()

    init {
        nonSystemAppInfoMap = getNonSystemAppsList()
    }

    fun runGetAppService() {
        //          **********
//          Start/Pause is clicked
//          **********
//            TODO:
//            - if user starts timer, call what is running every 2 secs
        //            x- check to see if what is running is unproductive
        //              x - if so, check if timer is running. if it is, cool. if not, start
        //              - else, check if timer is running, if it is, pause it. if not, cool
//            x- if user pauses or resets the timer, exit the while loop

        // if already checking apps
        if (mod.checkBackgroundApps) {
            mod.checkBackgroundApps = false
            mod.get_timer_state_context().getState()?.nextAction()
            mod.get_timer_state_context().getState()?.showUI()
            println("backgroundapps are no longer checking")
        }
        // if this starts the checking process
        else {
            mod.checkBackgroundApps = true
            println("backgroundapps are being checked every 2 seconds")

//                while (mod.checkBackgroundApps) {
            for (i in 1..5) {
//                timerHelper = TimerHelper(this)
//                val allApplications = timerHelper.getForegroundApp()

                val allApplications = getForegroundApp()

                //check to see if apps are unproductive
                for ((key,value) in allApplications) {
                    if (mod.unproductiveApplications.containsKey(key)) {
                        if (mod.timerStatus !== "RUNNING") {
                            mod.timerStatus = "RUNNING"
                            mod.get_timer_state_context().getState()?.nextAction()
                            mod.get_timer_state_context().getState()?.showUI()
                        }
                    }
                    else {
                        if (mod.timerStatus === "RUNNING") {
                            mod.timerStatus === "PAUSED"
                            mod.get_timer_state_context().getState()?.nextAction()
                            mod.get_timer_state_context().getState()?.showUI()
                        }
                    }
                }
                println("**********")
                println("should only be running every 1 second")
                Thread.sleep(1_000)  // wait for 1 second
            }
        }
//            timerHelper = TimerHelper(this)
//            val allApplications = timerHelper.getForegroundApp()
//            println("**********************")
//            println("ALL APPLICATIONS BEGIN")
//            println("**********************")
//            for ((key,value) in allApplications) {
//                if (mod.unproductiveApplications.containsKey(key)) {
//
//                }
//            }
    }

    fun getForegroundApp() : Map<String,String> {

        var foregroundAppPackageName : String? = null
        val currentTime = System.currentTimeMillis()
        // The `queryEvents` method takes in the `beginTime` and `endTime` to retrieve the usage events.
        // In our case, beginTime = currentTime - 10 minutes ( 1000 * 60 * 10 milliseconds )
        // and endTime = currentTime
        val usageEvents = usageStatsManager.queryEvents( currentTime - (1000*2) , currentTime )
        val usageEvent = UsageEvents.Event()
        while ( usageEvents.hasNextEvent() ) {
            usageEvents.getNextEvent( usageEvent )
                foregroundAppPackageName =
                    if ( nonSystemAppInfoMap.containsKey( usageEvent.packageName ) ) {
                        nonSystemAppInfoMap[ usageEvent.packageName ]
                    }
                    else {
                        null
                    }
                if (openApplicationsMap.containsKey(foregroundAppPackageName) === false) {
                    openApplicationsMap[foregroundAppPackageName.toString()] = foregroundAppPackageName.toString()
                }
//                println("HERE IS THE COUNT TO THE MAP: " + openApplicationsMap.count())
        }
        return openApplicationsMap
    }

    // We only need non-system apps which were accessed by the user. The Map returned by this method
    // is used to filter the usage history.
    private fun getNonSystemAppsList() : Map<String,String> {
        val appInfos = packageManager.getInstalledApplications( PackageManager.GET_META_DATA )
        val appInfoMap = HashMap<String,String>()
        for ( appInfo in appInfos ) {
            if ( appInfo.flags != ApplicationInfo.FLAG_SYSTEM ) {
                appInfoMap[ appInfo.packageName ]= packageManager.getApplicationLabel( appInfo ).toString()
            }
        }
        return appInfoMap
    }

}