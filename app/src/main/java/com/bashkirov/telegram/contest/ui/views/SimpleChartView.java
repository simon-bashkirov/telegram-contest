package com.bashkirov.telegram.contest.ui.views;


import android.content.Context;
import android.util.AttributeSet;

/**
 * Provides simple chart visualisation with fixed (total) range
 */
public class SimpleChartView extends BaseChartView {

    public SimpleChartView(Context context) {
        this(context, null);
    }

    public SimpleChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
