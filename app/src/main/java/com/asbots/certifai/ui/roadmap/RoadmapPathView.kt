package com.asbots.certifai.ui.roadmap

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.OverScroller
import com.asbots.certifai.data.models.NodeStatus
import kotlin.math.hypot
import kotlin.math.roundToInt

/**
 * Gamified "Your learning path" vertical journey view matching the app theme.
 * Adaptive to light and dark theme background colors:
 * - Light Mode: Clean white background card (#FFFFFF) with dark typography.
 * - Dark Mode: Sleek dark surface card (#111C2E) with bright white typography.
 *
 * Node States:
 * - Completed: Solid green circle with checkmark (✓)
 * - In Progress: Blue circle with play triangle (▶) + "You are here" pill badge
 * - Locked: Translucent circle with lock (🔒) icon
 */
class RoadmapPathView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    data class PathNode(
        val id: String,
        val title: String,
        val subtitle: String = "",
        val badge: String = "",      // e.g. "You are here", "FREE", "3 💎"
        val duration: String = "",   // e.g. "10m"
        val isFree: Boolean = false,
        val status: NodeStatus = if (isFree) NodeStatus.COMPLETED else NodeStatus.LOCKED
    )

    data class Branch(val title: String, val nodes: List<PathNode>)

    private data class Placed(
        val node: PathNode,
        var x: Float,
        var y: Float,
        val branch: Int,
        val index: Int
    )

    var onNodeClick: ((PathNode) -> Unit)? = null

    private val branches = mutableListOf<Branch>()
    private val placed = mutableListOf<Placed>()
    private val headers = mutableListOf<RectF>()

    private val density = resources.displayMetrics.density
    private fun dp(v: Float) = v * density

    private val isNightMode: Boolean
        get() {
            val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
            return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
        }

    // Dynamic theme colors
    private val cardBgColor get() = if (isNightMode) 0xFF111C2E.toInt() else 0xFFFFFFFF.toInt()
    private val cardBorderColor get() = if (isNightMode) 0xFF263449.toInt() else 0xFFE2E8F0.toInt()
    private val headerTitleColor get() = if (isNightMode) 0xFFF1F5F9.toInt() else 0xFF0F172A.toInt()
    private val activeTitleColor get() = if (isNightMode) 0xFFF1F5F9.toInt() else 0xFF0F172A.toInt()
    private val activeSubtitleColor get() = if (isNightMode) 0xFF94A3B8.toInt() else 0xFF64748B.toInt()
    private val lockedTitleColor get() = if (isNightMode) 0xFF94A3B8.toInt() else 0xFF64748B.toInt()
    private val lockedSubtitleColor get() = if (isNightMode) 0xFF64748B.toInt() else 0xFF94A3B8.toInt()
    private val lockedCircleFill get() = if (isNightMode) 0xFF182232.toInt() else 0xFFF8FAFC.toInt()
    private val lockedCircleStroke get() = if (isNightMode) 0xFF334155.toInt() else 0xFFCBD5E1.toInt()
    private val lockedIconColor get() = if (isNightMode) 0xFF94A3B8.toInt() else 0xFF64748B.toInt()
    private val trackLineColor get() = if (isNightMode) 0xFF263449.toInt() else 0xFFE2E8F0.toInt()

    // Paints
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG)
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val iconStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val text = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    // Animation / pulse
    private var pulse = 0f
    private val pulseAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1800
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            pulse = it.animatedValue as Float
            invalidate()
        }
    }

    // Pan / Zoom / Drag
    private var scale = 1f
    private var tx = 0f
    private var ty = 0f
    private var contentH = 0f
    private val minScale = 0.85f
    private val maxScale = 2.0f

    private var draggingNodeIndex: Int? = null

    private val scroller = OverScroller(context)
    private val clipPath = Path()
    private val resetChip = RectF()
    private var viewAnimator: ValueAnimator? = null

    private val nodeR get() = dp(24f)

    private val scaleDetector = ScaleGestureDetector(
        context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                if (draggingNodeIndex != null) return false
                viewAnimator?.cancel()
                val ns = (scale * d.scaleFactor).coerceIn(minScale, maxScale)
                val f = ns / scale
                tx = d.focusX - (d.focusX - tx) * f
                ty = d.focusY - (d.focusY - ty) * f
                scale = ns
                clampTranslation()
                invalidate()
                return true
            }
        }
    )

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                scroller.forceFinished(true)
                viewAnimator?.cancel()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val cx = (e.x - tx) / scale
                val cy = (e.y - ty) / scale
                val hitIndex = placed.indexOfFirst {
                    hypot(cx - it.x, cy - it.y) <= nodeR + dp(16f)
                }
                if (hitIndex != -1) {
                    draggingNodeIndex = hitIndex
                    performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    invalidate()
                }
            }

            override fun onScroll(
                e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
            ): Boolean {
                if (draggingNodeIndex != null) return true
                tx -= distanceX
                ty -= distanceY
                clampTranslation()
                invalidate()
                return true
            }

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                if (draggingNodeIndex != null) return false
                val cw = width * scale
                val ch = contentH * scale
                val minX = if (cw <= width) tx.toInt() else (width - cw).toInt()
                val maxX = if (cw <= width) tx.toInt() else 0
                val minY = if (ch <= height) ty.toInt() else (height - ch).toInt()
                val maxY = if (ch <= height) ty.toInt() else 0
                scroller.fling(
                    tx.toInt(), ty.toInt(),
                    velocityX.toInt(), velocityY.toInt(),
                    minX, maxX, minY, maxY
                )
                postInvalidateOnAnimation()
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (draggingNodeIndex != null) return false
                if (resetChip.contains(e.x, e.y)) {
                    animateTo(1f, 0f, 0f, true)
                    return true
                }
                val cx = (e.x - tx) / scale
                val cy = (e.y - ty) / scale
                val hit = placed.firstOrNull {
                    hypot(cx - it.x, cy - it.y) <= nodeR + dp(20f)
                }
                if (hit != null) {
                    performClick()
                    onNodeClick?.invoke(hit.node)
                }
                return true
            }
        }
    )

    fun setBranches(list: List<Branch>) {
        branches.clear()
        branches.addAll(list)
        requestLayout()
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pulseAnimator.start()
    }

    override fun onDetachedFromWindow() {
        pulseAnimator.cancel()
        viewAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    private fun getEffectiveStatus(node: PathNode): NodeStatus {
        if (node.status == NodeStatus.IN_PROGRESS ||
            node.badge.equals("You are here", true) ||
            node.subtitle.equals("You are here", true)
        ) {
            return NodeStatus.IN_PROGRESS
        }
        if (node.status == NodeStatus.COMPLETED ||
            node.subtitle.equals("Completed", true)
        ) {
            return NodeStatus.COMPLETED
        }
        if (node.isFree && node.status != NodeStatus.LOCKED) {
            return NodeStatus.COMPLETED
        }
        return node.status
    }

    private fun computeLayout(w: Float): Float {
        placed.clear()
        headers.clear()
        var y = dp(12f)

        val cardMarginH = dp(12f)
        val cardW = w - cardMarginH * 2f

        branches.forEachIndexed { b, br ->
            val startY = y
            val headerH = dp(52f)
            y += headerH

            val leftCircleX = cardMarginH + dp(32f)
            val rightCircleX = cardMarginH + cardW - dp(32f)

            br.nodes.forEachIndexed { i, n ->
                val nodeY = y + dp(28f)
                val isRight = (i % 2 != 0)
                val nodeX = if (isRight) rightCircleX else leftCircleX
                placed.add(Placed(n, nodeX, nodeY, b, i))
                y += dp(86f)
            }

            val cardBottom = y + dp(14f)
            headers.add(RectF(cardMarginH, startY, cardMarginH + cardW, cardBottom))
            y = cardBottom + dp(20f)
        }
        return y
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        contentH = computeLayout(w.toFloat())
        val h = if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY)
            MeasureSpec.getSize(heightMeasureSpec) else contentH.toInt().coerceAtLeast(dp(420f).toInt())
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clipPath.reset()
        clipPath.addRoundRect(
            RectF(0f, 0f, w.toFloat(), h.toFloat()),
            dp(20f), dp(20f), Path.Direction.CW
        )
        resetChip.set(w - dp(100f), h - dp(44f), w - dp(12f), h - dp(12f))
        clampTranslation()
    }

    private fun clampTranslation() {
        val cw = width * scale
        val ch = contentH * scale
        tx = if (cw <= width) (width - cw) / 2f else tx.coerceIn(width - cw, 0f)
        ty = if (ch <= height) 0f else ty.coerceIn(height - ch, 0f)
    }

    private fun animateTo(targetScale: Float, fx: Float, fy: Float, reset: Boolean) {
        viewAnimator?.cancel()
        scroller.forceFinished(true)
        val s0 = scale
        val tx0 = tx
        val ty0 = ty
        val f = targetScale / s0
        val tx1 = if (reset) 0f else fx - (fx - tx0) * f
        val ty1 = if (reset) 0f else fy - (fy - ty0) * f
        viewAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            addUpdateListener {
                val t = it.animatedFraction
                scale = s0 + (targetScale - s0) * t
                tx = tx0 + (tx1 - tx0) * t
                ty = ty0 + (ty1 - ty0) * t
                clampTranslation()
                invalidate()
            }
            start()
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            tx = scroller.currX.toFloat()
            ty = scroller.currY.toFloat()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(tx, ty)
        canvas.scale(scale, scale)

        drawHeaders(canvas)
        drawTracks(canvas)
        placed.forEach { drawNode(canvas, it) }

        canvas.restore()

        if (scale != 1f) {
            drawResetChip(canvas)
        }
    }

    private fun drawResetChip(c: Canvas) {
        fill.shader = null
        fill.style = Paint.Style.FILL
        fill.color = if (isNightMode) 0xDD0F172A.toInt() else 0xDD334155.toInt()
        c.drawRoundRect(resetChip, dp(14f), dp(14f), fill)

        text.apply {
            color = Color.WHITE
            textSize = dp(11f)
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
        }
        c.drawText(
            "${(scale * 100).roundToInt()}% · Reset",
            resetChip.centerX(), resetChip.centerY() + dp(4f), text
        )
    }

    private fun drawHeaders(c: Canvas) {
        headers.forEachIndexed { b, r ->
            // Card Container (White in light theme, dark surface in dark theme)
            fill.shader = null
            fill.style = Paint.Style.FILL
            fill.color = cardBgColor
            c.drawRoundRect(r, dp(22f), dp(22f), fill)

            // Card Border
            stroke.shader = null
            stroke.style = Paint.Style.STROKE
            stroke.strokeWidth = dp(1.2f)
            stroke.color = cardBorderColor
            c.drawRoundRect(r, dp(22f), dp(22f), stroke)

            // Header Title ("Your learning path")
            text.apply {
                color = headerTitleColor
                textSize = dp(17f)
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.LEFT
            }
            val title = branches[b].title
            val textY = r.top + dp(34f)
            c.drawText(title, r.left + dp(20f), textY, text)
        }
    }

    private fun drawTracks(c: Canvas) {
        headers.forEachIndexed { b, cardRect ->
            val branchNodes = placed.filter { it.branch == b }
            if (branchNodes.size < 2) return@forEachIndexed

            val centerX = cardRect.centerX()
            val topY = branchNodes.first().y
            val bottomY = branchNodes.last().y

            // Dynamic background vertical center line
            stroke.shader = null
            stroke.style = Paint.Style.STROKE
            stroke.strokeWidth = dp(2f)
            stroke.color = trackLineColor
            c.drawLine(centerX, topY, centerX, bottomY, stroke)

            // Active completed/in-progress section highlight line
            val lastActiveIndex = branchNodes.indexOfLast {
                val st = getEffectiveStatus(it.node)
                st == NodeStatus.COMPLETED || st == NodeStatus.IN_PROGRESS
            }
            if (lastActiveIndex > 0) {
                val activeEndY = branchNodes[lastActiveIndex].y
                stroke.color = 0xFF10B981.toInt() // Green progress track
                stroke.strokeWidth = dp(2.5f)
                c.drawLine(centerX, topY, centerX, activeEndY, stroke)
            }
        }
    }

    private fun drawNode(c: Canvas, p: Placed) {
        val status = getEffectiveStatus(p.node)
        val r = if (status == NodeStatus.IN_PROGRESS) dp(26f) else nodeR
        val isRight = (p.index % 2 != 0)
        val isDragging = draggingNodeIndex != null && placed.getOrNull(draggingNodeIndex!!)?.node?.id == p.node.id

        // Drag highlight halo
        if (isDragging) {
            stroke.shader = null
            stroke.style = Paint.Style.STROKE
            stroke.strokeWidth = dp(4f)
            stroke.color = 0xFF38BDF8.toInt()
            c.drawCircle(p.x, p.y, r + dp(8f), stroke)
        }

        // Radar ripple for IN_PROGRESS node
        if (status == NodeStatus.IN_PROGRESS && !isDragging) {
            stroke.shader = null
            stroke.style = Paint.Style.STROKE
            stroke.strokeWidth = dp(3f)
            stroke.color = (0x2563EB and 0x00FFFFFF) or
                    ((255 * (1f - pulse) * 0.7f).toInt() shl 24)
            c.drawCircle(p.x, p.y, r + dp(6f) + dp(14f) * pulse, stroke)
        }

        // Draw Node Circle Icon
        when (status) {
            NodeStatus.COMPLETED -> {
                // Solid Green Circle (#10B981)
                fill.shader = null
                fill.style = Paint.Style.FILL
                fill.color = 0xFF10B981.toInt()
                c.drawCircle(p.x, p.y, r, fill)

                // Checkmark (✓)
                drawCheckmark(c, p.x, p.y, r, Color.WHITE)
            }
            NodeStatus.IN_PROGRESS -> {
                // Vibrant Electric Blue Circle (#2563EB)
                fill.shader = null
                fill.style = Paint.Style.FILL
                fill.color = 0xFF2563EB.toInt()
                c.drawCircle(p.x, p.y, r, fill)

                // Play Triangle (▶)
                drawPlayTriangle(c, p.x, p.y, r, Color.WHITE)
            }
            NodeStatus.LOCKED -> {
                // Translucent / Light Circle
                fill.shader = null
                fill.style = Paint.Style.FILL
                fill.color = lockedCircleFill
                c.drawCircle(p.x, p.y, r, fill)

                // Border stroke
                stroke.shader = null
                stroke.style = Paint.Style.STROKE
                stroke.strokeWidth = dp(1.5f)
                stroke.color = lockedCircleStroke
                c.drawCircle(p.x, p.y, r, stroke)

                // Lock Icon (🔒)
                drawLockIcon(c, p.x, p.y, r, lockedIconColor)
            }
        }

        // Text & Badge Positioning
        val maxTextW = dp(120f)

        if (isRight) {
            // Text on LEFT of circle
            val tx = p.x - r - dp(14f)
            text.textAlign = Paint.Align.RIGHT

            when (status) {
                NodeStatus.IN_PROGRESS -> {
                    // Title
                    text.color = activeTitleColor
                    text.textSize = dp(15f)
                    text.typeface = Typeface.DEFAULT_BOLD
                    val titleStr = TextUtils.ellipsize(p.node.title, text, maxTextW, TextUtils.TruncateAt.END).toString()
                    c.drawText(titleStr, tx, p.y - dp(2f), text)

                    // "You are here" Pill Badge
                    drawYouAreHereBadge(c, tx, p.y + dp(6f), alignRight = true)
                }
                NodeStatus.COMPLETED -> {
                    // Title
                    text.color = activeTitleColor
                    text.textSize = dp(15f)
                    text.typeface = Typeface.DEFAULT_BOLD
                    val titleStr = TextUtils.ellipsize(p.node.title, text, maxTextW, TextUtils.TruncateAt.END).toString()
                    c.drawText(titleStr, tx, p.y - dp(3f), text)

                    // Subtitle
                    text.color = activeSubtitleColor
                    text.textSize = dp(13f)
                    text.typeface = Typeface.DEFAULT
                    val sub = if (p.node.subtitle.isNotEmpty()) p.node.subtitle else "Completed"
                    c.drawText(sub, tx, p.y + dp(16f), text)
                }
                NodeStatus.LOCKED -> {
                    // Title
                    text.color = lockedTitleColor
                    text.textSize = dp(15f)
                    text.typeface = Typeface.DEFAULT_BOLD
                    val titleStr = TextUtils.ellipsize(p.node.title, text, maxTextW, TextUtils.TruncateAt.END).toString()
                    c.drawText(titleStr, tx, p.y - dp(3f), text)

                    // Subtitle
                    text.color = lockedSubtitleColor
                    text.textSize = dp(13f)
                    text.typeface = Typeface.DEFAULT
                    val sub = if (p.node.subtitle.isNotEmpty()) p.node.subtitle else "Locked"
                    c.drawText(sub, tx, p.y + dp(16f), text)
                }
            }
        } else {
            // Text on RIGHT of circle
            val tx = p.x + r + dp(14f)
            text.textAlign = Paint.Align.LEFT

            when (status) {
                NodeStatus.IN_PROGRESS -> {
                    // Title
                    text.color = activeTitleColor
                    text.textSize = dp(15f)
                    text.typeface = Typeface.DEFAULT_BOLD
                    val titleStr = TextUtils.ellipsize(p.node.title, text, maxTextW, TextUtils.TruncateAt.END).toString()
                    c.drawText(titleStr, tx, p.y - dp(2f), text)

                    // "You are here" Pill Badge
                    drawYouAreHereBadge(c, tx, p.y + dp(6f), alignRight = false)
                }
                NodeStatus.COMPLETED -> {
                    // Title
                    text.color = activeTitleColor
                    text.textSize = dp(15f)
                    text.typeface = Typeface.DEFAULT_BOLD
                    val titleStr = TextUtils.ellipsize(p.node.title, text, maxTextW, TextUtils.TruncateAt.END).toString()
                    c.drawText(titleStr, tx, p.y - dp(3f), text)

                    // Subtitle
                    text.color = activeSubtitleColor
                    text.textSize = dp(13f)
                    text.typeface = Typeface.DEFAULT
                    val sub = if (p.node.subtitle.isNotEmpty()) p.node.subtitle else "Completed"
                    c.drawText(sub, tx, p.y + dp(16f), text)
                }
                NodeStatus.LOCKED -> {
                    // Title
                    text.color = lockedTitleColor
                    text.textSize = dp(15f)
                    text.typeface = Typeface.DEFAULT_BOLD
                    val titleStr = TextUtils.ellipsize(p.node.title, text, maxTextW, TextUtils.TruncateAt.END).toString()
                    c.drawText(titleStr, tx, p.y - dp(3f), text)

                    // Subtitle
                    text.color = lockedSubtitleColor
                    text.textSize = dp(13f)
                    text.typeface = Typeface.DEFAULT
                    val sub = if (p.node.subtitle.isNotEmpty()) p.node.subtitle else "Locked"
                    c.drawText(sub, tx, p.y + dp(16f), text)
                }
            }
        }
    }

    private fun drawCheckmark(c: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        iconStroke.color = color
        iconStroke.strokeWidth = dp(2.8f)
        path.reset()
        path.moveTo(cx - r * 0.35f, cy)
        path.lineTo(cx - r * 0.05f, cy + r * 0.3f)
        path.lineTo(cx + r * 0.4f, cy - r * 0.25f)
        c.drawPath(path, iconStroke)
    }

    private fun drawPlayTriangle(c: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        fill.shader = null
        fill.style = Paint.Style.FILL
        fill.color = color
        path.reset()
        path.moveTo(cx - r * 0.22f, cy - r * 0.35f)
        path.lineTo(cx + r * 0.38f, cy)
        path.lineTo(cx - r * 0.22f, cy + r * 0.35f)
        path.close()
        c.drawPath(path, fill)
    }

    private fun drawLockIcon(c: Canvas, cx: Float, cy: Float, r: Float, color: Int) {
        // Lock body
        fill.shader = null
        fill.style = Paint.Style.FILL
        fill.color = color
        val bodyW = r * 0.52f
        val bodyH = r * 0.42f
        val bodyTop = cy - r * 0.02f
        val bodyRect = RectF(cx - bodyW / 2f, bodyTop, cx + bodyW / 2f, bodyTop + bodyH)
        c.drawRoundRect(bodyRect, dp(3f), dp(3f), fill)

        // Shackle
        stroke.shader = null
        stroke.style = Paint.Style.STROKE
        stroke.strokeWidth = dp(2f)
        stroke.color = color
        val shackleR = r * 0.2f
        val shackleTop = bodyTop - shackleR * 1.4f
        val shackleRect = RectF(cx - shackleR, shackleTop, cx + shackleR, bodyTop + shackleR)
        c.drawArc(shackleRect, 180f, 180f, false, stroke)
    }

    private fun drawYouAreHereBadge(c: Canvas, x: Float, y: Float, alignRight: Boolean) {
        val textStr = "You are here"
        text.textSize = dp(11.5f)
        text.typeface = Typeface.DEFAULT_BOLD
        text.textAlign = Paint.Align.LEFT

        val textW = text.measureText(textStr)
        val hPad = dp(10f)
        val badgeW = textW + hPad * 2f
        val badgeH = dp(22f)

        val badgeLeft = if (alignRight) x - badgeW else x
        val badgeRect = RectF(badgeLeft, y, badgeLeft + badgeW, y + badgeH)

        // Adaptive Pill Background
        fill.shader = null
        fill.style = Paint.Style.FILL
        fill.color = if (isNightMode) 0xFF0F2942.toInt() else 0xFFDBEAFE.toInt()
        c.drawRoundRect(badgeRect, dp(11f), dp(11f), fill)

        // Pill Stroke (#2563EB)
        stroke.shader = null
        stroke.style = Paint.Style.STROKE
        stroke.strokeWidth = dp(1f)
        stroke.color = 0xFF2563EB.toInt()
        c.drawRoundRect(badgeRect, dp(11f), dp(11f), stroke)

        // Text Color (#93C5FD in dark mode, #1D4ED8 in light mode)
        text.color = if (isNightMode) 0xFF93C5FD.toInt() else 0xFF1D4ED8.toInt()
        c.drawText(textStr, badgeLeft + hPad, badgeRect.centerY() + dp(4f), text)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val cx = (e.x - tx) / scale
        val cy = (e.y - ty) / scale

        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (draggingNodeIndex != null) {
                    val idx = draggingNodeIndex!!
                    val p = placed[idx]
                    p.x = cx
                    p.y = cy
                    invalidate()
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                draggingNodeIndex = null
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        scaleDetector.onTouchEvent(e)
        if (!scaleDetector.isInProgress && draggingNodeIndex == null) {
            gestureDetector.onTouchEvent(e)
        }
        return true
    }

    override fun performClick(): Boolean = super.performClick()
}
