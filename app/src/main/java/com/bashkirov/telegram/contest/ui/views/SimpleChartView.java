package com.bashkirov.telegram.contest.ui.views;


import android.content.Context;
import android.util.AttributeSet;

import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.FloatPointModel;

import java.util.List;

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
    public List<FloatPointModel> getPoints(CurveModel curveModel) {
        return mNormalizedPointsMap.get(curveModel);
    }

}
