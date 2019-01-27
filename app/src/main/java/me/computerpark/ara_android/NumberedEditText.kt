package me.computerpark.ara_android

/**
 * Created by XFL, modified by 컴터박 on 2/25/2018.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet


/**
 * Author S Mahbub Uz Zaman on 5/9/15.
 * Licensed Under GPL2
 */

class NumberedEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {


    private val rect: Rect

    private val paint: Paint


    init {

        rect = Rect()

        paint = Paint()

        paint.style = Paint.Style.FILL

        paint.color = Color.BLUE

        paint.textSize = 20f

    }


    override fun onDraw(canvas: Canvas) {

        var baseline = baseline

        for (i in 0 until lineCount) {

            canvas.drawText("" + (i + 1), rect.left.toFloat(), baseline.toFloat(), paint)

            baseline += lineHeight

        }

        super.onDraw(canvas)

    }


}