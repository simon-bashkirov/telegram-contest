package com.bashkirov.telegram.contest.models;

public class PointModel {
    private final long mX;
    private final int mY;

    public PointModel(long x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public long getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

}
