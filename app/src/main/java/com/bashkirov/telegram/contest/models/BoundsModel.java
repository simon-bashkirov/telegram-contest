package com.bashkirov.telegram.contest.models;

import java.util.Arrays;

public class BoundsModel {

    BoundsModel(long minX, long maxX, int minY, int maxY) {
        this.mMinX = minX;
        this.mMaxX = maxX;
        this.mMinY = minY;
        this.mMaxY = maxY;
    }

    private long mMinX, mMaxX;

    private int mMinY, mMaxY;

    public long getMinX() {
        return mMinX;
    }

    public long getMaxX() {
        return mMaxX;
    }

    public int getMinY() {
        return mMinY;
    }

    public int getMaxY() {
        return mMaxY;
    }

    public BoundsModel megre(BoundsModel other) {
        if (other == null) return this;
        return new BoundsModel(
                Math.min(this.mMinX, other.mMaxX),
                Math.max(this.mMaxX, other.mMaxX),
                Math.min(this.mMinY, other.mMinY),
                Math.max(this.mMaxY, other.mMaxY));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof BoundsModel)) return false;
        BoundsModel that = (BoundsModel) o;
        return mMinX == that.mMinX &&
                mMaxX == that.mMaxX &&
                mMinY == that.mMinY &&
                mMaxY == that.mMaxY;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{mMinX, mMaxX, mMinY, mMaxY});
    }
}
