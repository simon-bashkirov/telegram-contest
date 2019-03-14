package com.bashkirov.telegram.contest.ui.views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.FloatPointModel;
import com.bashkirov.telegram.contest.models.PointModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SimpleChartView extends View {

    private final static float LINE_WIDTH_PX = 4f;
    private final static boolean IS_ANTI_ALIAS = true;
    // private final static int PAINT_COLOR = Color.BLACK;
    private final static Paint.Style PAINT_STYLE = Paint.Style.STROKE;
    private final static Paint.Join PAINT_JOIN = Paint.Join.ROUND;
    private final static Paint.Cap STROKE_CAP = Paint.Cap.ROUND;

    private List<CurveModel> mCurves = new LinkedList<>();
    private Map<CurveModel, Paint> mPaintMap = new HashMap<>();
    private Map<CurveModel, List<FloatPointModel>> mNormalizedPointsMap = new HashMap<>();

    private long minX = Long.MAX_VALUE;
    private long maxX = 0L;

    private int minY = Integer.MAX_VALUE;
    private int maxY = 0;

    public SimpleChartView(Context context) {
        this(context, null);
    }

    public SimpleChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void loadChart(ChartModel chart) {
        List<CurveModel> curves = chart.getCurves();
        for (CurveModel curveModel : curves) {
            setCurve(curveModel);
        }

    }

    private static List<FloatPointModel> normalize(List<PointModel> points, long minX, long maxX, int minY, int maxY, int width, int height) {
        List<FloatPointModel> normalized = new ArrayList<>();

        for (PointModel point : points) {
            normalized.add(new FloatPointModel((float) (point.getX() - minX) / (maxX - minX) * width,
                    (float) (point.getY() - minY) / (1.1f * maxY - minY) * height));
        }
        return normalized;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (CurveModel curve : mCurves) {
            Paint paint = mPaintMap.get(curve);
            List<FloatPointModel> points = mNormalizedPointsMap.get(curve);
            if (paint == null || points == null) return;
            float height = getHeight();

            for (int i = 0; i < points.size() - 1; i++) {
                FloatPointModel point = points.get(i);
                FloatPointModel nextPoint = points.get(i + 1);
                canvas.drawLine(point.getX(), height - point.getY(), nextPoint.getX(), height - nextPoint.getY(), paint);
            }

        }
        super.onDraw(canvas);
    }

    private Paint getPaintForColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(IS_ANTI_ALIAS);
        paint.setStrokeWidth(LINE_WIDTH_PX);
        paint.setStyle(PAINT_STYLE);
        paint.setStrokeJoin(PAINT_JOIN);
        paint.setStrokeCap(STROKE_CAP);
        return paint;
    }

    public void setCurve(CurveModel curve) {
        mCurves.add(curve);
        mPaintMap.put(curve, getPaintForColor(curve.getColor()));
        List<PointModel> points = curve.getPoints();
        boolean scaleChanged = setScale(points);
        int width = getWidth();
        int height = getHeight();
        if (scaleChanged) {
            mNormalizedPointsMap.clear();
            for (CurveModel aCurve : mCurves) {
                List<FloatPointModel> normalized = normalize(aCurve.getPoints(), minX, maxX, minY, maxY, width, height);
                mNormalizedPointsMap.put(aCurve, normalized);
            }
        } else {
            List<FloatPointModel> normalized = normalize(curve.getPoints(), minX, maxX, minY, maxY, width, height);
            mNormalizedPointsMap.put(curve, normalized);
        }
    }

    /**
     * @param points
     * @return true if scale was changed
     */
    private boolean setScale(List<PointModel> points) {
        boolean isChanged = false;
        for (PointModel pointModel : points) {
            if (pointModel.getX() > maxX) {
                maxX = pointModel.getX();
                isChanged = true;
            }
        }

        for (PointModel pointModel : points) {
            if (pointModel.getX() < minX) {
                minX = pointModel.getX();
                isChanged = true;
            }
        }

        for (PointModel pointModel : points) {
            if (pointModel.getY() > maxY) {
                maxY = pointModel.getY();
                isChanged = true;
            }
        }

        for (PointModel pointModel : points) {
            if (pointModel.getY() < minY) {
                minY = pointModel.getY();
                isChanged = true;
            }
        }
        return isChanged;
    }

}
