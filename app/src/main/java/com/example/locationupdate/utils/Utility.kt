package com.example.locationupdate.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object Utility {

    private var TAG = Utility::class.java.canonicalName

    @Suppress("DEPRECATION")// Deprecated for third party Services.
    fun isLocationServiceRunning(serviceClass: Class<*>, mActivity: Activity): Boolean {
        val manager: ActivityManager =
            mActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    fun writeToFile(fileName:String, writingContent:String, context:Context) {
        val path = context.filesDir
        try {
            val writer = FileOutputStream(File(path,fileName))
            writer.write(writingContent.toByteArray())
            writer.close()
            Log.i(TAG, "wrote to file: $fileName")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}