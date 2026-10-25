package com.asbots.certifai.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.asbots.certifai.R

class LiquidWaveNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.border_light)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val path = Path()
    private val rectF = RectF()

    private var activeCenterX = -1f
    private var targetCenterX = -1f
    private var activeWidth = 0f
    private var targetWidth = 0f

    private var animator: ValueAnimator? = null
    private var isHumpVisible = false

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        if (width <= 0 || height <= 0) {
            super.onDraw(canvas)
            return
        }

        path.reset()

        val cornerRadius = 32f
        val topY = 16f // Give 16dp top space for the liquid hump bulge
        val bottomY = height - 4f

        if (!isHumpVisible || activeCenterX < 0) {
            // Standard rounded capsule
            rectF.set(4f, topY, width - 4f, bottomY)
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, bgPaint)
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)
            super.onDraw(canvas)
            return
        }

        val humpWidth = 68f
        val humpHeight = 16f
        val leftX = (activeCenterX - humpWidth / 2f).coerceAtLeast(cornerRadius + 4f)
        val rightX = (activeCenterX + humpWidth / 2f).coerceAtMost(width - cornerRadius - 4f)

        // Draw Liquid Wave Hump Path
        path.moveTo(cornerRadius + 4f, topY)

        // Top line to start of hump
        if (leftX > cornerRadius + 4f) {
            path.lineTo(leftX, topY)
        }

        // Cubic Bezier curve for the smooth liquid wave hump
        val c1x = leftX + humpWidth * 0.25f
        val c1y = topY
        val c2x = activeCenterX - humpWidth * 0.22f
        val c2y = topY - humpHeight
        val peakX = activeCenterX
        val peakY = topY - humpHeight

        path.cubicTo(c1x, c1y, c2x, c2y, peakX, peakY)

        val c3x = activeCenterX + humpWidth * 0.22f
        val c3y = topY - humpHeight
        val c4x = rightX - humpWidth * 0.25f
        val c4y = topY
        val endX = rightX

        path.cubicTo(c3x, c3y, c4x, c4y, endX, topY)

        // Line to right top corner
        path.lineTo(width - cornerRadius - 4f, topY)

        // Right top arc
        rectF.set(width - 2 * cornerRadius - 4f, topY, width - 4f, topY + 2 * cornerRadius)
        path.arcTo(rectF, 270f, 90f)

        // Right side down to bottom
        path.lineTo(width - 4f, bottomY - cornerRadius)

        // Right bottom arc
        rectF.set(width - 2 * cornerRadius - 4f, bottomY - 2 * cornerRadius, width - 4f, bottomY)
        path.arcTo(rectF, 0f, 90f)

        // Bottom line to left
        path.lineTo(cornerRadius + 4f, bottomY)

        // Left bottom arc
        rectF.set(4f, bottomY - 2 * cornerRadius, 4f + 2 * cornerRadius, bottomY)
        path.arcTo(rectF, 90f, 90f)

        // Left side up
        path.lineTo(4f, topY + cornerRadius)

        // Left top arc
        rectF.set(4f, topY, 4f + 2 * cornerRadius, topY + 2 * cornerRadius)
        path.arcTo(rectF, 180f, 90f)

        path.close()

        canvas.drawPath(path, bgPaint)
        canvas.drawPath(path, borderPaint)

        super.onDraw(canvas)
    }

    fun animateHumpTo(targetView: View) {
        targetView.post {
            val targetX = targetView.x + targetView.width / 2f
            val targetW = targetView.width.toFloat()

            if (targetX <= 0) return@post

            if (!isHumpVisible) {
                isHumpVisible = true
                activeCenterX = targetX
                activeWidth = targetW
                invalidate()
                return@post
            }

            val startX = activeCenterX
            val startW = activeWidth

            animator?.cancel()
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 380L
                interpolator = OvershootInterpolator(1.2f)
                addUpdateListener { anim ->
                    val fraction = anim.animatedValue as Float
                    activeCenterX = startX + (targetX - startX) * fraction
                    activeWidth = startW + (targetW - startW) * fraction
                    invalidate()
                }
            }
            animator?.start()
        }
    }
}
