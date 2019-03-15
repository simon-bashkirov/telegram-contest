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

/**
 * Provides basic chart visualisation
 */
abstract class BaseChartView extends View {

    //Constants
    private final static float LINE_WIDTH_PX = 4f;
    private final static boolean IS_ANTI_ALIAS = true;
    private final static Paint.Style PAINT_STYLE = Paint.Style.STROKE;
    private final static Paint.Join PAINT_JOIN = Paint.Join.ROUND;
    private final static Paint.Cap STROKE_CAP = Paint.Cap.ROUND;

    //Common fields
    protected List<CurveModel> mCurves = new LinkedList<>();
    protected Map<CurveModel, List<FloatPointModel>> mNormalizedPointsMap = new HashMap<>();

    //Private field
    private Map<CurveModel, Paint> mPaintMap = new HashMap<>();
    private long minX = Long.MAX_VALUE;
    private long maxX = 0L;
    private int minY = Integer.MAX_VALUE;
    private int maxY = 0;

    //============  View constructors =============
    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //============  Public methods =============

    /**
     * Maps points given in data coordinates to view coordinates
     *
     * @param points in data coordinates
     * @return points in view coordinates
     */
    private static List<FloatPointModel> normalize(List<PointModel> points, long minX, long maxX, int minY, int maxY, int width, int height) {
        List<FloatPointModel> normalized = new ArrayList<>();

        for (PointModel point : points) {
            normalized.add(new FloatPointModel((float) (point.getX() - minX) / (maxX - minX) * width,
                    (float) (point.getY() - minY) / (1.1f * maxY - minY) * height));
        }
        return normalized;
    }

    /**
     * Loads chart containing a list of curves to be displayed
     */
    public void loadChart(ChartModel chart) {
        List<CurveModel> curves = chart.getCurves();
        for (CurveModel curveModel : curves) {
            loadCurve(curveModel);
        }
        invalidate();

    }

    //=============== Protected methods ============

    /**
     * Loads single @param curve to be displayed
     */
    public void loadCurve(CurveModel curve) {
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
     * @return points mapped to float values in view coordinates for @param curve
     */
    protected abstract List<FloatPointModel> getPoints(CurveModel curve);

    ////////////////////////////////////////////////////

    //================ View ==========================
    @Override
    protected void onDraw(Canvas canvas) {
        for (CurveModel curve : mCurves) {
            Paint paint = mPaintMap.get(curve);
            List<FloatPointModel> points = getPoints(curve);
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

    /**
     * @return paint for given int color
     */
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

    /**
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
