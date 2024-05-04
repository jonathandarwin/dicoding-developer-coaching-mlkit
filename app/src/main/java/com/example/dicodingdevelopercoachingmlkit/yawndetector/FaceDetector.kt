package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * Created by Jonathan Darwin on 30 April 2024
 */
@OptIn(ExperimentalGetImage::class)
class FaceDetector(
    private val onFace: (faceInfo: FaceInfo) -> Unit,
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
                        val upperLipBottomPointList = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
                        val lowerLipTopPointList = face.getContour(FaceContour.LOWER_LIP_TOP)?.points

                        if(upperLipBottomPointList == null || lowerLipTopPointList == null) return@addOnSuccessListener

                        val upperLip = upperLipBottomPointList[upperLipBottomPointList.size / 2]
                        val lowerLip = lowerLipTopPointList[lowerLipTopPointList.size / 2]
                        val diff = lowerLip.y - upperLip.y

                        onFace(
                            FaceInfo(
                                imageWidth = imageWidth,
                                imageHeight = imageHeight,
                                faceBox = face.boundingBox,
                                mouthBox = getMouthRect(face),
                                isYawning = isYawning(diff)
                            )
                        )
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

    private fun getMouthRect(face: Face): RectF {
        val upperLipTopPointList = face.getContour(FaceContour.UPPER_LIP_TOP)?.points.orEmpty()
        val lowerLipBottomPointList = face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points.orEmpty()

        val mouthPoints = upperLipTopPointList + lowerLipBottomPointList

        var left = Float.MAX_VALUE
        var right = Float.MIN_VALUE
        var top = Float.MAX_VALUE
        var bottom = Float.MIN_VALUE

        mouthPoints.forEach {
            if (it.x < left) {
                left = it.x
            }

            if (it.x > right) {
                right = it.x
            }

            if (it.y < top) {
                top = it.y
            }

            if (it.y > bottom) {
                bottom = it.y
            }
        }

        return RectF(
            left,
            right,
            top,
            bottom
        )
    }
}