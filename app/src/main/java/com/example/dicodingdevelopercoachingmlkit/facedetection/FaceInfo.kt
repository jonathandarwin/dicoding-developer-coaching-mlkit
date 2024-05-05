package com.example.dicodingdevelopercoachingmlkit.facedetection

import android.graphics.Rect
import android.graphics.RectF

/**
 * Created by Jonathan Darwin on 04 May 2024
 */
data class FaceInfo(
    val imageWidth: Int,
    val imageHeight: Int,
    val faceBox: Rect,
    val mouthBox: RectF,
    val isYawning: Boolean,
)