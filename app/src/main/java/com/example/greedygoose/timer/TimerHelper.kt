package com.example.greedygoose.timer

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.greedygoose.data.SPLDAccessModel

class TimerHelper(private var context: Context) {//}, service: IBinder ) {

    private val model = SPLDAccessModel(context = context)
    private var usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private var packageManager = context.packageManager
    private var nonSystemAppInfoMap = getNonSystemAppsList()
    private val openApplicationsMap: MutableMap<String, String> = mutableMapOf()

    fun runGetAppService(): Boolean {
        val allApplications = getForegroundApp()

        println("***************")
        println(allApplications)
        //check to see if apps are unproductive
        println("num unproductive: " + model.unproductive.value.size)

        for ((key, value) in allApplications) {
            if (model.unproductive.value.contains(value)) {
                return true
            }
        }
        return false
    }

    fun getForegroundApp(): Map<String, String> {

        var foregroundAppPackageName: String? = null
        val currentTime = System.currentTimeMillis()
        // The `queryEvents` method takes in the `beginTime` and `endTime` to retrieve the usage events.
        // In our case, beginTime = currentTime - 10 minutes ( 1000 * 60 * 10 milliseconds )
        // and endTime = currentTime
        val usageEvents = usageStatsManager.queryEvents(currentTime - (1000 * 1), currentTime)
        val usageEvent = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(usageEvent)
            println(usageEvent.packageName)
            println(usageEvent.eventType)


            if(usageEvent.packageName == "com.android.vending"){

                println("comparing: ${usageEvent.packageName} : ${model.unproductive.value.toString()}")
            }

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