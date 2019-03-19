package com.bashkirov.telegram.contest.models;

/**
 * Provides model for point in view coordinates
 */
public class ViewPointModel {

    private final float mX, mY;

    public ViewPointModel(float x, float y) {
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
