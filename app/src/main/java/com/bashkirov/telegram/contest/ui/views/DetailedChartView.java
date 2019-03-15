package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.FloatPointModel;
import com.bashkirov.telegram.contest.models.PointModel;

import java.util.Collections;
import java.util.List;

/**
 * Provides rangable anda decorated chart visualisation
 */
public class DetailedChartView extends BaseChartView {

    private float mMinRange = 0.3f;
    private float mMaxRange = 0.7f;

    public DetailedChartView(Context context) {
        this(context, null);
    }

    public DetailedChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailedChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected List<FloatPointModel> getFloatPoints(CurveModel curve) {
        List<FloatPointModel> points = mNormalizedPointsMap.get(curve);
        if (points == null) return Collections.emptyList();
        int size = points.size();
        int start = getStartIndex(size);
        int end = getEndIndex(size);
        return points.subList(start, end);
    }

    @Override
    protected boolean setScale(List<PointModel> points) {
        int size = points.size();
        int start = getStartIndex(size);
        int end = getEndIndex(size);
        List<PointModel> cutPoints = points.subList(start, end);
        boolean isChanged = false;
        for (PointModel pointModel : cutPoints) {
            if (pointModel.getY() > maxY) {
                maxY = pointModel.getY();
                isChanged = true;
            }
        }

        for (PointModel pointModel : cutPoints) {
            if (pointModel.getY() < minY) {
                minY = pointModel.getY();
                isChanged = true;
            }
        }

        minX = points.get(getStartIndex(size)).getX();
        maxX = points.get(getEndIndex(size)).getX();
        return isChanged;
    }

    private int getStartIndex(int listSize) {
        return Math.round(mMinRange * listSize);
    }

    private int getEndIndex(int listSize) {
        return Math.round(mMaxRange * listSize);
    }

}
