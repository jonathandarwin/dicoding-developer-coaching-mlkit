package com.example.dicodingdevelopercoachingmlkit.yawndetector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.max
import com.example.dicodingdevelopercoachingmlkit.R

/**
 * Created by Jonathan Darwin on 02 May 2024
 */
class OverlayView @JvmOverloads constructor(
    private val context: Context,
    attributeSet: AttributeSet? = null,
) : View(context, attributeSet) {

    private val spiderMaskBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.spiderman)

    private var leftEyePoints = emptyList<PointF>()
    private var boundingBox: Rect? = null

    private var scaleX = 1f
    private var scaleY = 1f

    var isMirror: Boolean = true

    val rectPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    val boardPaint = Paint().apply {
        color = Color.WHITE
    }

    val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 32f
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

        drawFaceBox(canvas)
        drawEyeBox(canvas)
    }

    private fun drawFaceBox(canvas: Canvas) {
        /** Left & right depend on front / back camera */
        boundingBox?.let {
            val top = (it.top) * scaleY
            val bottom = (it.bottom) * scaleY
            val left = if (isMirror) width - ((it.right) * scaleX) else (it.left) * scaleX
            val right = if (isMirror) width - ((it.left) * scaleX) else (it.right) * scaleX


            /** Draw Mask */
            val scaledspiderMaskBitmap = Bitmap.createScaledBitmap(
                spiderMaskBitmap,
                (right - left).toInt(),
                (bottom - top).toInt(),
                false
            )

            canvas.drawBitmap(
                scaledspiderMaskBitmap,
                left,
                top,
                null
            )

            /** Draw Face Box */
            canvas.drawRect(
                left,
                top,
                right,
                bottom,
                rectPaint
            )

            /** Draw Board */
            canvas.drawRect(
                left,
                top - 300,
                right,
                top,
                boardPaint
            )

            val text = "Software Engineering Term Quiz"
            val bound = Rect()
            textPaint.getTextBounds(text, 0, text.length, bound)

            val x = (left - right)
            val y = (bottom - top)

            canvas.drawText(
                text,
                x,
                y,
                textPaint,
            )

        }
    }

    private fun drawEyeBox(canvas: Canvas) {
        if (leftEyePoints.isEmpty()) return

        var mostLeft = Float.MAX_VALUE
        var mostRight = Float.MIN_VALUE
        var mostTop = Float.MAX_VALUE
        var mostBottom = Float.MIN_VALUE

        leftEyePoints.forEach {
            if (it.x < mostLeft) {
                mostLeft = it.x
            }

            if (it.x > mostRight) {
                mostRight = it.x
            }

            if (it.y < mostTop) {
                mostTop = it.y
            }

            if (it.y > mostBottom) {
                mostBottom = it.y
            }
        }

        canvas.drawRect(
            width - (mostLeft * scaleX),
            mostTop * scaleY,
            width - (mostRight * scaleX),
            mostBottom * scaleY,
            rectPaint
        )
    }
}