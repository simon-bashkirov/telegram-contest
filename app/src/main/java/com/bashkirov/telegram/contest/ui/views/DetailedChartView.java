package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.bashkirov.telegram.contest.models.BoundsModel;
import com.bashkirov.telegram.contest.models.ChartModel;

/**
 * Provides rangable anda decorated chart visualisation
 */
public class DetailedChartView extends BaseChartView implements Rangable {

    public DetailedChartView(Context context) {
        this(context, null);
    }

    public DetailedChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailedChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    private BoundsModel mInitialBounds;

    @Override
    public void loadChart(ChartModel chart) {
        super.loadChart(chart);
        mInitialBounds = getBounds();
        setRange(0f, 0.31f);
    }

    //============ Rangable =====================
    @Override
    public void setRange(float start, float end) {
        if (end < start) return;
        BoundsModel initialBounds = mInitialBounds;
        if (initialBounds == null) return;
        Float xStartShift = start * (initialBounds.getMaxX() - initialBounds.getMinX());
        Float xEndShift = end * (initialBounds.getMaxX() - initialBounds.getMinX());
        BoundsModel newBounds = new BoundsModel(initialBounds.getMinX() + xStartShift.longValue(),
                initialBounds.getMinX() + xEndShift.longValue(), initialBounds.getMinY(), initialBounds.getMaxY());
        setBounds(newBounds);
        invalidate();
    }

    @Override
    public void shiftRange(float shift) {
        BoundsModel initialBounds = mInitialBounds;
        BoundsModel oldBounds = getBounds();
        if (initialBounds == null) return;
        Float xShift = shift * (initialBounds.getMaxX() - initialBounds.getMinX());
        BoundsModel newBounds = new BoundsModel(oldBounds.getMinX() + xShift.longValue(),
                oldBounds.getMaxX() + xShift.longValue(), initialBounds.getMinY(), initialBounds.getMaxY());
        setBounds(newBounds);
        invalidate();
    }

    @Override
    public void extendRange(float percent, Direction direction) {
        invalidate();
    }

}
