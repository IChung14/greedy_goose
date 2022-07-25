package com.example.greedygoose.timer

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.greedygoose.R
import com.example.greedygoose.data.SPLDAccessModel

class TimerHelper(private var context: Context) {

    private val model = SPLDAccessModel(context = context)
    private var usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private var packageManager = context.packageManager
    private var nonSystemAppInfoMap = getNonSystemAppsList()

    // <packagelable, packagename>
    private val openApplicationsMap: MutableMap<String, String> =
        mutableMapOf(context.getString(R.string.app_name) to context.packageName)

    fun runGetAppService(): Boolean {
        val allApplications = getForegroundApp()

        //check to see if apps are unproductive
        for ((key, value) in allApplications) {
            if (model.unproductive.value.contains(value)) {
                return true
            }
        }
        return false
    }

    private fun getForegroundApp(): Map<String, String> {
        var foregroundAppPackageName: String? = null
        val currentTime = System.currentTimeMillis()
        //runs every second
        val usageEvents = usageStatsManager.queryEvents(currentTime - (1000 * 1), currentTime)
        val usageEvent = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(usageEvent)

            foregroundAppPackageName =
                if (nonSystemAppInfoMap.containsKey(usageEvent.packageName)) {
                    nonSystemAppInfoMap[usageEvent.packageName]
                } else {
                    null
                }

            if(usageEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED){
                if (openApplicationsMap.containsKey(foregroundAppPackageName) === false) {
                    openApplicationsMap[foregroundAppPackageName.toString()] = usageEvent.packageName
                }
            }else if(usageEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED ||
                    usageEvent.eventType == UsageEvents.Event.ACTIVITY_STOPPED){
                openApplicationsMap.remove(foregroundAppPackageName.toString())
            }
        }
        return openApplicationsMap
    }

    // We only need non-system apps which were accessed by the user. The Map returned by this method
    // is used to filter the usage history.
    private fun getNonSystemAppsList(): Map<String, String> {
        val appInfos = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appInfoMap = HashMap<String, String>()
        for (appInfo in appInfos) {
            if (appInfo.flags != ApplicationInfo.FLAG_SYSTEM) {
                appInfoMap[appInfo.packageName] =
                    packageManager.getApplicationLabel(appInfo).toString()
            }
        }
        return appInfoMap
    }
}