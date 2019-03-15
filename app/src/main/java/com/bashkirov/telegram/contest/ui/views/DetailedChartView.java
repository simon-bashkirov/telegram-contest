package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Provides rangable anda decorated chart visualisation
 */
public class DetailedChartView extends BaseChartView implements Rangable {

    private float mMinRange = 0.0f;
    private float mMaxRange = 0.1f;

    public DetailedChartView(Context context) {
        this(context, null);
    }

    public DetailedChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailedChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //============ Rangable =====================
    @Override
    public void setRange(float start, float end) {
        mMinRange = start;
        mMaxRange = end;
        invalidate();
    }

    @Override
    public void shiftRange(float shift) {
        float minRange = mMinRange + shift;
        float maxRange = mMaxRange + shift;
        mMinRange = minRange < 1f ? minRange : mMinRange;
        mMaxRange = maxRange <= 1f ? maxRange : mMaxRange;
        invalidate();
    }

    @Override
    public void extendRange(float percent, Direction direction) {
        switch (direction) {
            case LEFT:
                mMinRange -= percent;
                break;

            case RIGHT:
                mMaxRange -= percent;
                break;
        }
        invalidate();
    }

    private int getStartIndex(int listSize) {
        return Math.round(mMinRange * listSize);
    }

    private int getEndIndex(int listSize) {
        return Math.round(mMaxRange * listSize);
    }

}
