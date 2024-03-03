package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null
    private var realisation = 0

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            textSize = getDimension(R.styleable.StatsView_textSize, textSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, generationRandomColor()),
                getColor(R.styleable.StatsView_color2, generationRandomColor()),
                getColor(R.styleable.StatsView_color3, generationRandomColor()),
                getColor(R.styleable.StatsView_color4, generationRandomColor())
            )
            realisation = getInt(R.styleable.StatsView_realisation, realisation)
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val paintDot = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = colors[0]
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val paintBack = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        color = 0x80808080.toInt()
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(center.x, center.y, radius, paintBack)
        if (data.isEmpty()) {
            return
        }

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )

        var startAngle = -90F
        val maxAngle = 360 * progress + startAngle

        when (realisation) {
            1 -> for ((index, datum) in data.withIndex()) {
                val angle = datum * 360F
                paint.color = colors.getOrElse(index) { generationRandomColor() }
                canvas.drawArc(oval, startAngle, angle * progress, false, paint)
                startAngle += angle
            }

            2 -> for ((index, datum) in data.withIndex()) {
                val angle = datum * 360F
                paint.color = colors.getOrElse(index) { generationRandomColor() }
                canvas.drawArc(oval, startAngle + progress * 360, angle * progress, false, paint)
                startAngle += angle
            }

            3 -> for ((index, datum) in data.withIndex()) {
                val angle = datum * 360F
                paint.color = colors.getOrNull(index) ?: generationRandomColor()
                canvas.drawArc(oval, startAngle + 45F, -angle / 2 * progress, false, paint)
                canvas.drawArc(oval, startAngle + 45F, angle / 2 * progress, false, paint)
                startAngle += angle
            }

            else -> for ((index, datum) in data.withIndex()) {
                val angle = datum * 360F
                val rotationAngle = min(angle, maxAngle - startAngle)
                paint.color = colors.getOrElse(index) { generationRandomColor() }
                canvas.drawArc(oval, startAngle, rotationAngle, false, paint)
                startAngle += angle
                if (startAngle > maxAngle) return
            }
        }
        canvas.drawPoint(center.x, center.y - radius, paintDot)
    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2500
            interpolator = LinearInterpolator()
        }.also { it.start() }
    }

    private fun generationRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

}