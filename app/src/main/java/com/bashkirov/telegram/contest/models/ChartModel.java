package com.bashkirov.telegram.contest.models;

import java.util.List;

public class ChartModel {

    private final List<CurveModel> mCurves;

    public ChartModel(List<CurveModel> curves) {
        this.mCurves = curves;
    }

    public Iterable<CurveModel> getCurves() {
        return mCurves;
    }
}