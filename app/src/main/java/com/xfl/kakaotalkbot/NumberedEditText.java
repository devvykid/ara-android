package com.xfl.kakaotalkbot;

/**
 * Created by XFL on 2/25/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;


/**
 * Author S Mahbub Uz Zaman on 5/9/15.
 * <p>
 * Lisence Under GPL2
 */

public class NumberedEditText extends AppCompatEditText {


    private Rect rect;

    private Paint paint;


    public NumberedEditText(Context context, AttributeSet attrs) {

        super(context, attrs);

        rect = new Rect();

        paint = new Paint();

        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.BLUE);

        paint.setTextSize(20);

    }


    @Override

    protected void onDraw(Canvas canvas) {

        int baseline = getBaseline();

        for (int i = 0; i < getLineCount(); i++) {

            canvas.drawText("" + (i + 1), rect.left, baseline, paint);

            baseline += getLineHeight();

        }

        super.onDraw(canvas);

    }


}