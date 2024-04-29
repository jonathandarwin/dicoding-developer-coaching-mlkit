package com.example.dicodingdevelopercoachingmlkit.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class CameraPermissionHelper(
    private val activity: Activity
) {

    fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }

//    fun requestPermission() {
//        ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CAMERA_PERMISSION)
//    }
//
//    fun checkPermissionResult(
//        requestCode: Int,
//        onGranted: () -> Unit,
//        onDeclined: () -> Unit,
//    ) {
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if (allPermissionsGranted()) {
//                onGranted()
//            } else {
//                onDeclined()
//            }
//        }
//    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}