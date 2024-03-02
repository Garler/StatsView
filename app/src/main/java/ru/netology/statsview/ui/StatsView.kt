package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
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
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
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
        var startAngle = -90F
        var inputData = data.sum()
        inputData = if (inputData < 1) 1F else data.sum()

        data.forEachIndexed() { index, datum ->
            val angle = datum * 360F / inputData
            paint.color = colors.getOrElse(index) { generationRandomColor() }
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }
        canvas.drawText(
            "%.2f%%".format(data.sum() * 100 / inputData),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
        canvas.drawPoint(center.x, center.y - radius, paintDot)
    }

    private fun generationRandomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

}