package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityYawnDetectorBinding
import com.example.dicodingdevelopercoachingmlkit.util.CameraPermissionHelper

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class YawnDetectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYawnDetectorBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            showMessage("Please allow camera permission to use the feature.")
        }
    }

    private val cameraPermissionHelper = CameraPermissionHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYawnDetectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (cameraPermissionHelper.allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.preview.surfaceProvider)
                }


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(throwable: Throwable) {
                showMessage("Error when starting camera.")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}