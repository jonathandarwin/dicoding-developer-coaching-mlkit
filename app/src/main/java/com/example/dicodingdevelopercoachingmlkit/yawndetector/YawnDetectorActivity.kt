package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityYawnDetectorBinding
import com.example.dicodingdevelopercoachingmlkit.util.CameraPermissionHelper
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class YawnDetectorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityYawnDetectorBinding

    private lateinit var analyzerExecutor: ExecutorService

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

    override fun onDestroy() {
        super.onDestroy()
        analyzerExecutor.shutdown()
    }

    private fun startCamera() {
        analyzerExecutor = Executors.newSingleThreadExecutor()

        binding.preview.post {
            lifecycleScope.launch {

                val cameraProvider = ProcessCameraProvider.getInstance(this@YawnDetectorActivity).await()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.preview.surfaceProvider)
                    }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetRotation(binding.preview.display.rotation)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(
                    analyzerExecutor,
                    YawnDetector(
                        onSuccess = { isYawning ->
                            runOnUiThread {
                                binding.tvYawn.visibility = if (isYawning) View.VISIBLE else View.GONE
                            }
                        },
                        onLeftEyePoints = { points, imageWidth, imageHeight ->
                            binding.overlay.setLeftEyePoints(
                                points,
                                imageWidth,
                                imageHeight
                            )
                        },
                        onFace = { boundingBox, imageWidth, imageHeight ->
                            binding.overlay.setFaceBoundingBox(
                                boundingBox,
                                imageWidth,
                                imageHeight
                            )
                        }
                    )
                )

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this@YawnDetectorActivity,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )

                } catch(throwable: Throwable) {
                    showMessage("Error when starting camera.")
                }
            }
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}