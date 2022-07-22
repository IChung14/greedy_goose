package com.example.greedygoose.timer

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.greedygoose.SettingsViewModel
import com.example.greedygoose.TimerViewModel

class TimerHelper(private var context: Context) {//}, service: IBinder ) {

    private lateinit var viewModel: TimerViewModel
    private var usageStatsManager : UsageStatsManager = context.getSystemService( Context.USAGE_STATS_SERVICE ) as UsageStatsManager
    private var packageManager : PackageManager = context.packageManager
    private var nonSystemAppInfoMap : Map<String,String>
    private val openApplicationsMap: MutableMap<String, String> = mutableMapOf()

    init {
        nonSystemAppInfoMap = getNonSystemAppsList()
        viewModel = TimerViewModel()
    }

    fun runGetAppService(): Boolean {
//        viewModel = SettingsViewModel(context)
        val allApplications = getForegroundApp()

        //check to see if apps are unproductive
        println("num unproductive: " + viewModel.unproductiveApplications.size)

        for ((key,value) in allApplications) {
            if (viewModel.unproductiveApplications.containsKey(key)) {
                return true
            }
        }
        return false
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