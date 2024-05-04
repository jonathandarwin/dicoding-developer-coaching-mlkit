package com.example.dicodingdevelopercoachingmlkit.yawndetector

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
    private val onFaceDetected: (faceInfo: FaceInfo) -> Unit,
    private val onFailure: (throwable: Throwable) -> Unit,
) : ImageAnalysis.Analyzer {

    /** Step 1 : Setup configuration */
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

            /** Step 2 : Run inference */
            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    imageProxy.close()

                    for(face in faces) {
                        /** Step 3 : Show result to UI */
                        onFaceDetected(
                            FaceInfo(
                                imageWidth = imageWidth,
                                imageHeight = imageHeight,
                                faceBox = face.boundingBox,
                                mouthBox = getMouthRect(face),
                                isYawning = isYawning(face),
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    imageProxy.close()
                    onFailure(it)
                }
        } catch (throwable: Throwable) {
            imageProxy.close()
            onFailure(throwable)
        }
    }

    /**
     * Step:
     * 1. Get the bottom side of upper lip & top side of bottom lip points
     * 2. Get the middle points of each points
     * 3. Find the difference between 2 points
     * 4. If the difference is larger than the threshold, we consider it as yawning
     *
     * Note:
     * This approach is not effective & not ready for production. This is just
     * a simulation for learning purpose.
     */
    private fun isYawning(face: Face): Boolean {
        val upperLipBottomPointList = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points
        val lowerLipTopPointList = face.getContour(FaceContour.LOWER_LIP_TOP)?.points

        if(upperLipBottomPointList == null || lowerLipTopPointList == null) return false

        val upperLip = upperLipBottomPointList[upperLipBottomPointList.size / 2]
        val lowerLip = lowerLipTopPointList[lowerLipTopPointList.size / 2]
        val diff = lowerLip.y - upperLip.y

        return diff >= 20
    }

    /**
     * Step:
     * 1. Get top side of upper lip & bottom side of lower lip points
     * 2. Combine all the points into 1 list
     * 3. Get the most left, top, right, bottom points from the list
     */
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
            top,
            right,
            bottom
        )
    }
}