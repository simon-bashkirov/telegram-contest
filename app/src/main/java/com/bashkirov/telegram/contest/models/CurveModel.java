package com.bashkirov.telegram.contest.models;

import java.util.List;

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
        this.mBounds = getBounds(points);
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

    public BoundsModel adjustBoundsHeight(BoundsModel boundsModel) {
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        long minX = boundsModel.getMinX();
        long maxX = boundsModel.getMaxX();
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

    private static BoundsModel getBounds(List<PointModel> points) {
        long minX = Long.MAX_VALUE;
        long maxX = 0L;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        for (PointModel pointModel : points) {
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
