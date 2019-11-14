package com.straatinfo.straatinfo.Services

import android.content.Context
import android.graphics.*
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import java.util.*


class BadgeDrawerArrowDrawable : DrawerArrowDrawable {
    private var SIZE_FACTOR = .4f
    private var HALF_SIZE_FACTOR = SIZE_FACTOR / 2

    private var backgroundPaint: Paint? = null
    private var textPaint: Paint? = null
    private var text: String? = null
    private var enabled = true

    constructor(context: Context?) : super(context) {
        backgroundPaint = Paint()
        backgroundPaint?.color = Color.RED
        backgroundPaint?.isAntiAlias = true
        backgroundPaint?.textAlign = Paint.Align.CENTER
        backgroundPaint?.textSize = .8f * intrinsicHeight


        textPaint = Paint()
        textPaint?.color = Color.WHITE
        textPaint?.isAntiAlias  = false
        textPaint?.typeface = Typeface.DEFAULT_BOLD
        textPaint?.textAlign = Paint.Align.CENTER

        textPaint?.textSize = .4f * intrinsicHeight
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (!enabled) {
            return
        }

        val bounds = bounds
        val x = (1 - HALF_SIZE_FACTOR) * bounds.width()
        val y = HALF_SIZE_FACTOR * bounds.height()
        canvas.drawCircle(x, y, SIZE_FACTOR * bounds.width(), backgroundPaint)

        if (text == null || text?.length == 0) {
            return
        }

        val textBounds = Rect()
        try {
            textPaint!!.getTextBounds(text!!, 0, 2, textBounds)
        } catch (e: Exception) {

        }
        canvas.drawText(text, x, (y + textBounds.height() / 2) + ((y + textBounds.height() / 2) * .10f), textPaint)
    }

    fun setEnabled(enabled: Boolean) {
        if (this.enabled != enabled) {
            this.enabled = enabled
            invalidateSelf()
        }
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun setText(text: String) {
        if (!Objects.equals(this.text, text)) {
            this.text = text
            invalidateSelf()
        }
    }

    fun getText(): String? {
        return text
    }

    fun setBackgroundColor(color: Int) {
        if (backgroundPaint?.getColor() != color) {
            backgroundPaint?.setColor(color)
            invalidateSelf()
        }
    }

    fun getBackgroundColor(): Int? {
        return backgroundPaint?.getColor()
    }

    fun setTextColor(color: Int) {
        if (textPaint?.getColor() != color) {
            textPaint?.setColor(color)
            invalidateSelf()
        }
    }

    fun getTextColor(): Int? {
        return textPaint?.getColor()
    }
}