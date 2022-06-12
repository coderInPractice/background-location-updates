package com.example.locationupdate.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log

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
        try {
            context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(writingContent.toByteArray())
            }
        } catch (securityException : SecurityException) {
            securityException.printStackTrace()
        }

    }

    fun readFromFile(fileName:String, context: Context) {
        context.openFileInput(fileName).bufferedReader().useLines { lines ->
            lines.fold("") { some, text ->
                Log.i(TAG, "readFromFile: $some\n$text")
                "$some\n$text"
            }
        }
    }
}