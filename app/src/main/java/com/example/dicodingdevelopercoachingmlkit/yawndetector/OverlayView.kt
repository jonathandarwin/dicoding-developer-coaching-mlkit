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

    private val boardPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.primary)
    }

    private val backBoardPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.secondary)
    }

    private val textPaint = Paint(Paint.FAKE_BOLD_TEXT_FLAG).apply {
        color = Color.WHITE
        textSize = 40f
    }

    var isMirror: Boolean = true

    var text: String = "Soft Eng Terms Quiz"
        set(value) {
            field = value

            invalidate()
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
    }

    private fun drawFaceBox(canvas: Canvas) {
        /** Left & right depend on front / back camera */
        boundingBox?.let {
            val top = (it.top) * scaleY
            val bottom = (it.bottom) * scaleY
            val left = if (isMirror) width - ((it.right) * scaleX) else (it.left) * scaleX
            val right = if (isMirror) width - ((it.left) * scaleX) else (it.right) * scaleX


            /** TODO: Uncomment this to draw a mask */
//            val scaledspiderMaskBitmap = Bitmap.createScaledBitmap(
//                spiderMaskBitmap,
//                (right - left).toInt(),
//                (bottom - top).toInt(),
//                false
//            )
//
//            canvas.drawBitmap(
//                scaledspiderMaskBitmap,
//                left,
//                top,
//                null
//            )

            /** TODO: Uncomment this to draw face box */
//            canvas.drawRect(
//                left,
//                top,
//                right,
//                bottom,
//                rectPaint
//            )

            /** Draw Board */
            val topBoard = top - 300
            val bottomBoard = top

            canvas.drawRect(
                left + 15,
                topBoard + 15,
                right + 15,
                bottomBoard + 15,
                backBoardPaint
            )

            canvas.drawRect(
                left,
                topBoard,
                right,
                bottomBoard,
                boardPaint
            )


            val bound = Rect()
            textPaint.getTextBounds(text, 0, text.length, bound)

            val x = left + ((right - left) / 2) - bound.centerX()
            val y = topBoard + ((bottomBoard - topBoard) / 2) - bound.centerY()

            canvas.drawText(
                text,
                x,
                y,
                textPaint,
            )

        }
    }
}