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

public class SimpleCharView extends View {

    private final static float LINE_WIDTH_PX = 4f;
    private final static boolean IS_ANTI_ALIAS = true;
    // private final static int PAINT_COLOR = Color.BLACK;
    private final static Paint.Style PAINT_STYLE = Paint.Style.STROKE;
    private final static Paint.Join PAINT_JOIN = Paint.Join.ROUND;
    private final static Paint.Cap STROKE_CAP = Paint.Cap.ROUND;

    private List<CurveModel> mCurves = new LinkedList<>();
    private Map<CurveModel, Paint> mPaintMap = new HashMap<>();
    private Map<CurveModel, List<FloatPointModel>> mNormalizedPointsMap = new HashMap<>();

    public SimpleCharView(Context context) {
        this(context, null);
    }

    public SimpleCharView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleCharView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void loadChart(ChartModel chart) {
        List<CurveModel> curves = chart.getCurves();
        for (CurveModel curveModel : curves) {
            setCurve(curveModel);
        }

    }

    public void setCurve(CurveModel curve) {
        mCurves.add(curve);
        mPaintMap.put(curve, getPaintForColor(curve.getColor()));


        int width = getWidth();
        int height = getHeight();

        List<PointModel> points = curve.getPoints();

        long maxX = 0;
        for (PointModel pointModel : points) {
            if (pointModel.getX() > maxX) {
                maxX = pointModel.getX();
            }
        }
        long minX = maxX;
        for (PointModel pointModel : points) {
            if (pointModel.getX() < minX) {
                minX = pointModel.getX();
            }
        }

        long maxY = 0;
        for (PointModel pointModel : points) {
            if (pointModel.getY() > maxY) {
                maxY = pointModel.getY();
            }
        }
        long minY = maxY;
        for (PointModel pointModel : points) {
            if (pointModel.getY() < minY) {
                minY = pointModel.getY();
            }
        }
        List<FloatPointModel> normalized = new ArrayList<>();

        float minYScale = minY;
        float maxYScale = 1.1f * maxY;

        for (PointModel point : points) {
            normalized.add(new FloatPointModel((float) (point.getX() - minX) / (maxX - minX) * width,
                    (float) (point.getY() - minYScale) / (maxYScale - minYScale) * height));
        }
        mNormalizedPointsMap.put(curve, normalized);
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


}
