package com.example.dicodingdevelopercoachingmlkit.yawndetector

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
@OptIn(ExperimentalGetImage::class)
class YawnDetector(
    private val onSuccess: (isYawning: Boolean) -> Unit,
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val faceDetector = FaceDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        println("JOE LOG analyze")
        try {
            val image = imageProxy.image ?: return

            val inputImage = InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            )

            println("JOE LOG analyze...")
            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    imageProxy.close()

                    println("JOE LOG success")

                    for(face in faces) {
                        val upperLipPointList = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                        val lowerLipPointList = face.getContour(FaceContour.LOWER_LIP_TOP)?.points

                        if(upperLipPointList == null || lowerLipPointList == null) return@addOnSuccessListener

                        val upperLip = upperLipPointList[upperLipPointList.size / 2]
                        val lowerLip = lowerLipPointList[lowerLipPointList.size / 2]
                        val diff = lowerLip.y - upperLip.y

                        println("JOE LOG diff : $diff")
                        onSuccess(isYawning(diff))
                    }
                }
                .addOnFailureListener {
                    println("JOE LOG error")
                    imageProxy.close()
                }
        } catch (e: Exception) {
            println("JOE LOG error $e")
        }
    }

    private fun isYawning(diff: Float) = diff >= 20
}