package com.example.locationupdate


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.example.locationupdate.locationService.LocationService
import com.example.locationupdate.utils.AppsConstant.DATA_PRESENT
import com.example.locationupdate.utils.AppsConstant.MY_SHARED_PREF
import com.example.locationupdate.utils.Utility

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MY_FINE_LOCATION_REQUEST = 100
        private const val MY_BACKGROUND_LOCATION_REQUEST = 101
    }
    private var mLocationService:LocationService = LocationService()
    private lateinit var mServiceIntent: Intent
    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var currentLoc:TextView
    private var currentLocLiveData: LiveData<String> = LocationService.currLoc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn = findViewById(R.id.startServiceBtn)
        stopBtn = findViewById(R.id.StopServiceBtn)
        currentLoc = findViewById(R.id.current_loc_tv)

        startBtn.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                        AlertDialog.Builder(this).apply {
                            setTitle("Background permission")
                            setMessage(R.string.background_location_permission_message)
                            setPositiveButton("Start service anyway"
                            ) { _, _ ->
                                startService()
                            }
                            setNegativeButton("Grant background Permission"
                            ) { _, _ ->
                                requestBackgroundLocationPermission()
                            }
                        }.create().show()

                    }else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){
                        startService()
                    }
                } else {
                    startService()
                }
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder(this)
                        .setTitle("ACCESS_FINE_LOCATION")
                        .setMessage("Location permission required")
                        .setPositiveButton(
                            "OK"
                        ) { _, _ ->
                            requestFineLocationPermission()
                        }
                        .create()
                        .show()
                } else {
                    requestFineLocationPermission()
                }
            }
        }

        stopBtn.setOnClickListener {
            stopService()
        }

    }

    override fun onStart() {
        super.onStart()
        val preferences = getSharedPreferences(MY_SHARED_PREF, MODE_PRIVATE)
        val isNameAndMobileSet = preferences.getBoolean(DATA_PRESENT, false)
        if (!isNameAndMobileSet) {
            Intent(this, BasicDetailActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
        currentLocLiveData.observe(this, {
            currentLoc.text = it
        })
    }
    @SuppressLint("InlinedApi")
    private fun requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            MY_BACKGROUND_LOCATION_REQUEST
        )
    }

    private fun requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,),
            MY_FINE_LOCATION_REQUEST
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Toast.makeText(this, requestCode.toString(), Toast.LENGTH_LONG).show()
        when (requestCode) {
            MY_FINE_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            requestBackgroundLocationPermission()
                        }

                        startService()
                    }

                } else {
                    Toast.makeText(this, "ACCESS_FINE_LOCATION permission denied", Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", this.packageName, null)))
                    }
                }
                return
            }
            MY_BACKGROUND_LOCATION_REQUEST -> {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Background location Permission Granted", Toast.LENGTH_LONG).show()
                        startService()
                    }
                } else {
                    Toast.makeText(this, "Background location permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun startService(){
        mLocationService = LocationService()
        mServiceIntent = Intent(this, mLocationService.javaClass)
        if (!Utility.isLocationServiceRunning(mLocationService.javaClass, this)) {
            startService(mServiceIntent)
            Toast.makeText(this, getString(R.string.service_start_successfully), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopService(){
        mLocationService = LocationService()
        mServiceIntent = Intent(this, mLocationService.javaClass)
        if (Utility.isLocationServiceRunning(mLocationService.javaClass, this)) {
            stopService(mServiceIntent)
            Toast.makeText(this, "Service stopped!!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Service is already stopped!!", Toast.LENGTH_SHORT).show()
        }
    }
}