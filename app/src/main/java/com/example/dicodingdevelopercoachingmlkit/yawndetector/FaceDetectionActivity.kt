package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.lifecycle.lifecycleScope
import com.example.dicodingdevelopercoachingmlkit.databinding.ActivityFaceDetectionBinding
import com.example.dicodingdevelopercoachingmlkit.util.CameraPermissionHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Jonathan Darwin on 29 April 2024
 */
class FaceDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceDetectionBinding

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

    private val termList = listOf(
        "API",
        "Clean Architecture",
        "SOLID",
        "YAGNI",
        "Code Smell",
        "Boilerplate Code",
        "Bug",
        "Compile Time",
        "Race Condition",
        "Time Complexity",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaceDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (cameraPermissionHelper.allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setupView()
        setupObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        analyzerExecutor.shutdown()
    }

    private fun setupView() {
        binding.btnStart.setOnClickListener {
            startTermRandomizer()
        }
    }

    private fun setupObserver() {
        binding.preview.previewStreamState.observe(this) {
            showStartButton(it == PreviewView.StreamState.STREAMING)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        analyzerExecutor = Executors.newSingleThreadExecutor()

        binding.preview.post {
            lifecycleScope.launch {

                val cameraProvider = ProcessCameraProvider.getInstance(this@FaceDetectionActivity).await()

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
                    FaceDetector(
                        onFace = { faceInfo ->
                            binding.overlay.setFaceInfo(faceInfo)
                        },
                    )
                )

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                binding.overlay.isMirror = when (cameraSelector.lensFacing) {
                    CameraSelector.LENS_FACING_BACK -> false
                    else -> true
                }

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        this@FaceDetectionActivity,
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

    private fun startTermRandomizer(timer: Int = 3) {
        showStartButton(false)

        val job = lifecycleScope.launch {
            while(isActive) {
                binding.overlay.text = termList.random()
                delay(100)
            }
        }

        lifecycleScope.launch {
            var second = 0
            while(second < timer) {
                delay(1000)
                second++
            }

            job.cancel()

            showStartButton(true)
        }
    }

    private fun showStartButton(isShow: Boolean) {
        binding.btnStart.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    private fun showMessage(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}