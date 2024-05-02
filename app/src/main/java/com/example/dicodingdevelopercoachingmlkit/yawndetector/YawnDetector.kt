package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.graphics.PointF
import android.graphics.Rect
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
    private val onLeftEyePoints: (leftEyePoints: List<PointF>, imageWidth: Int, imageHeight: Int) -> Unit,
    private val onFace: (boundingBox: Rect, imageWidth: Int, imageHeight: Int) -> Unit,
) : ImageAnalysis.Analyzer {

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .build()

    private val faceDetector = FaceDetection.getClient(options)

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val image = imageProxy.image ?: return
            val imageWidth = image.width
            val imageHeight = image.height

            val inputImage = InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            )

            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    imageProxy.close()

                    for(face in faces) {
                        onFace(face.boundingBox, imageWidth, imageHeight)
                        val upperLipPointList = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                        val lowerLipPointList = face.getContour(FaceContour.LOWER_LIP_TOP)?.points

                        if(upperLipPointList == null || lowerLipPointList == null) return@addOnSuccessListener

                        val upperLip = upperLipPointList[upperLipPointList.size / 2]
                        val lowerLip = lowerLipPointList[lowerLipPointList.size / 2]
                        val diff = lowerLip.y - upperLip.y

                        onSuccess(isYawning(diff))

                        face.getContour(FaceContour.LEFT_EYE)?.points?.let {
                            onLeftEyePoints(it, imageWidth, imageHeight)
                        }
                    }
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
        } catch (e: Exception) {
            imageProxy.close()
        }
    }

    private fun isYawning(diff: Float) = diff >= 20
}