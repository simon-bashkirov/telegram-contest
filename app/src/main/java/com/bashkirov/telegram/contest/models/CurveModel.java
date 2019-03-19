package com.bashkirov.telegram.contest.models;

import java.util.List;

/**
 * Provides model for curve
 */
public class CurveModel {

    private final List<PointModel> mPoints;
    private final int mColor;
    private final String mName;
    private final String mType;
    private final BoundsModel mBounds;

    public CurveModel(List<PointModel> points, int color, String name, String type) {
        this.mPoints = points;
        this.mColor = color;
        this.mName = name;
        this.mType = type;
        this.mBounds = initBounds();
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

    public BoundsModel getBounds() {
        return mBounds;
    }

    /**
     * Returns new bounds with Y limits adjusted to data given X limits
     *
     * @param minX min X limit
     * @param maxX max X limit
     * @return adjusted bounds
     */
    public BoundsModel adjustBoundsHeight(long minX, long maxX) {
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        for (PointModel pointModel : getPoints()) {
            long x = pointModel.getX();
            if (x >= minX && x <= maxX) {
                int y = pointModel.getY();
                if (y > maxY) {
                    maxY = y;
                }
                if (y < minY) {
                    minY = y;
                }
            }
        }
        return new BoundsModel(minX, maxX, minY, maxY);
    }

    /**
     * Initializes bounds for this curve
     *
     * @return bounds
     */
    @SuppressWarnings("ConstantConditions")
    private BoundsModel initBounds() {
        long minX = Long.MAX_VALUE;
        long maxX = 0L;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        for (PointModel pointModel : mPoints) {
            long x = pointModel.getX();
            int y = pointModel.getY();
            if (x > maxX) {
                maxX = x;
            }
            if (x < minX) {
                minX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (y < minY) {
                minY = y;
            }
        }

        return new BoundsModel(minX, maxX, minY, maxY);
    }
}
