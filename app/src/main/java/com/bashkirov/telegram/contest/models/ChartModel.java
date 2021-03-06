package com.bashkirov.telegram.contest.models;

import java.util.List;

/**
 * Provides model for chart data
 */
public class ChartModel {

    private final List<CurveModel> mCurves;

    public ChartModel(List<CurveModel> curves) {
        this.mCurves = curves;
    }

    public List<CurveModel> getCurves() {
        return mCurves;
    }
}