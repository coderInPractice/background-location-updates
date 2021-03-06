package com.example.locationupdate.locationService

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.locationupdate.utils.AppsConstant.FILE_NAME
import com.example.locationupdate.utils.Utility
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY

class LocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        var currLoc = MutableLiveData<String>()
    }


    private val locationRequest: LocationRequest = create().apply {
        interval = (60 * 5000)
        fastestInterval = (60 * 2000)
        priority = PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 5000
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                for (location in locationList) {
                    Toast.makeText(this@LocationService, "Latitude: " + location.latitude.toString() + '\n' +
                            "Longitude: "+ location.longitude, Toast.LENGTH_LONG).show()
                    val informationToWrite = "Latitude: ${location.latitude}\nLongitude: ${location.longitude}\n"
                    Utility.writeToFile(FILE_NAME, informationToWrite, this@LocationService)
                    Log.d("Location d", location.latitude.toString())
                    Log.i("Location i", location.latitude.toString())
                    currLoc.postValue(informationToWrite)
                }
                val location = locationList.last()
//                Toast.makeText(this@LocationService, "Latitude: " + location.latitude.toString() + '\n' +
//                        "Longitude: "+ location.longitude, Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() else startForeground(1, Notification())

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(applicationContext, "Permission required", Toast.LENGTH_LONG).show()
            return
        }else{
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChanel() {
        val notificationChannelId = "Location channel id"
        val channelName = "Background Service"
        val channel = NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_NONE)
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)
        val notificationBuilder =
            NotificationCompat.Builder(this, notificationChannelId)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Location updates:")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}