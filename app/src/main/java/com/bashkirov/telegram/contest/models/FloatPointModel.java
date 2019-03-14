package com.bashkirov.telegram.contest.models;

public class FloatPointModel {
    private final float mX;
    private final float mY;

    public FloatPointModel(float x, float y) {
        this.mX = x;
        this.mY = y;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }
}
