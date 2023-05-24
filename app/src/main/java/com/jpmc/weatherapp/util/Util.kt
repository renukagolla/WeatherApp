package com.jpmc.weatherapp.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

fun getCity(activity: Activity): String? {
    activity.getSharedPreferences("myPreference", AppCompatActivity.MODE_PRIVATE).apply {
        return getString("city", null)
    }
}

fun putCity(activity: Activity, city: String) {
    activity.getSharedPreferences("myPreference", AppCompatActivity.MODE_PRIVATE).apply {
        edit().putString("city", city).apply()
    }
}

fun checkPermissions(activity: Activity): Boolean {
    if (ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

fun isLocationEnabled(activity: Activity): Boolean {
    val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}