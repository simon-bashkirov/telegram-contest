package com.bashkirov.telegram.contest.models;

import java.util.List;

public class CurveModel {

    private final List<PointModel> mPoints;
    private final int mColor;
    private final String mName;
    private final String mType;

    public CurveModel(List<PointModel> points, int color, String name, String type) {
        this.mPoints = points;
        this.mColor = color;
        this.mName = name;
        this.mType = type;
    }

    public List<PointModel> getPoints() {
        return mPoints;
    }

    public int getColor() {
        return mColor;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }
}
