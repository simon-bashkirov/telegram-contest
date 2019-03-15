package com.bashkirov.telegram.contest.ui.views;


import android.content.Context;
import android.util.AttributeSet;

import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.FloatPointModel;
import com.bashkirov.telegram.contest.models.PointModel;

import java.util.List;

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

    //============== BaseChartView =============
    @Override
    public List<FloatPointModel> getFloatPoints(CurveModel curveModel) {
        return mNormalizedPointsMap.get(curveModel);
    }

    @Override
    protected boolean setScale(List<PointModel> points) {
        boolean isChanged = false;
        for (PointModel pointModel : points) {
            if (pointModel.getX() > maxX) {
                maxX = pointModel.getX();
                isChanged = true;
            }
        }

        for (PointModel pointModel : points) {
            if (pointModel.getX() < minX) {
                minX = pointModel.getX();
                isChanged = true;
            }
        }

        for (PointModel pointModel : points) {
            if (pointModel.getY() > maxY) {
                maxY = pointModel.getY();
                isChanged = true;
            }
        }

        for (PointModel pointModel : points) {
            if (pointModel.getY() < minY) {
                minY = pointModel.getY();
                isChanged = true;
            }
        }
        return isChanged;
    }

}
