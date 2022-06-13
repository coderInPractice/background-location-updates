package com.example.locationupdate.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.*


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
        val file = File(context.filesDir, fileName)
        try {
            val buffer = BufferedWriter(FileWriter(file))
            buffer.append(writingContent)
            buffer.newLine()
            buffer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun readFromFile(fileName:String, context: Context):LiveData<String> {
        val file = File(context.filesDir, fileName)

        val text:MutableLiveData<String> = MutableLiveData()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.postValue(line)
                //Toast.makeText(context, "$text", Toast.LENGTH_SHORT).show()
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return text
    }
}