package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

/**
 * Created by Jonathan Darwin on 02 May 2024
 */
class OverlayView @JvmOverloads constructor(
    private val context: Context,
    private val attributeSet: AttributeSet? = null,
) : View(context, attributeSet) {

    private var leftEyePoints = emptyList<PointF>()
    private var boundingBox: Rect? = null

    private var scaleX = 1f
    private var scaleY = 1f

    val rectPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    fun setLeftEyePoints(
        leftEyePoints: List<PointF>,
        imageWidth: Int,
        imageHeight: Int,
    ) {
        this.leftEyePoints = leftEyePoints

        scaleX = width / imageHeight.toFloat()
        scaleY = height / imageWidth.toFloat()
        invalidate()
    }

    fun setFaceBoundingBox(
        boundingBox: Rect,
        imageWidth: Int,
        imageHeight: Int,
    ) {
        this.boundingBox = boundingBox

        scaleX = width / imageHeight.toFloat()
        scaleY = height / imageWidth.toFloat()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        boundingBox?.let {
            canvas.drawRect(
                width - ((it.left) * scaleX),
                (it.top) * scaleY,
                width - ((it.right) * scaleX),
                (it.bottom) * scaleY,
                rectPaint
            )
        }

        if (leftEyePoints.isEmpty()) return

        val left = leftEyePoints[0]
        val right = leftEyePoints[leftEyePoints.size / 2]
        val top = leftEyePoints[leftEyePoints.size / 4]
        val bottom = leftEyePoints[leftEyePoints.size / 4 * 3]

        canvas.drawRect(
            width - ((left.x) * scaleX),
            (top.y) * scaleY,
            width - ((right.x) * scaleX),
            (bottom.y) * scaleY,
            rectPaint
        )
    }
}