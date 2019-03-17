package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bashkirov.telegram.contest.models.BoundsModel;
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
class BaseChartView extends View {

    //Constants
    private final static float LINE_WIDTH_PX = 4f;
    private final static boolean IS_ANTI_ALIAS = true;
    private final static Paint.Style PAINT_STYLE = Paint.Style.STROKE;
    private final static Paint.Join PAINT_JOIN = Paint.Join.ROUND;
    private final static Paint.Cap STROKE_CAP = Paint.Cap.ROUND;

    private int mBaseYPadding = 0;

    //Common fields
    protected List<CurveModel> mCurves = new LinkedList<>();
    protected Map<CurveModel, List<FloatPointModel>> mNormalizedPointsMap = new HashMap<>();

    //Private field
    private Map<CurveModel, Paint> mPaintMap = new HashMap<>();
    private BoundsModel mBounds;


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
     * Loads chart containing a list of curves to be displayed
     */
    public void loadChart(ChartModel chart) {
        List<CurveModel> curves = chart.getCurves();
        for (CurveModel curveModel : curves) {
            loadCurve(curveModel);
        }
        invalidate();
    }

    /**
     * Loads single @param curve to be displayed
     */
    public void loadCurve(CurveModel curve) {
        mCurves.add(curve);
        mPaintMap.put(curve, getPaintForColor(curve.getColor()));
        adjustBoundsForCurve(curve);
    }

    public void setBounds(BoundsModel bounds) {
        mNormalizedPointsMap.clear();
        BoundsModel adjustedBounds = null;
        for (CurveModel aCurve : mCurves) {
            if (adjustedBounds == null) adjustedBounds = aCurve.adjustBoundsHeight(bounds);
            else adjustedBounds = adjustedBounds.megre(aCurve.adjustBoundsHeight(bounds));
            List<FloatPointModel> normalized = normalize(aCurve.getPoints(), adjustedBounds);
            mNormalizedPointsMap.put(aCurve, normalized);
        }
        mBounds = adjustedBounds;
    }

    public BoundsModel getBounds() {
        return mBounds;
    }

    //========= Protected methods ==============

    /**
     * @return paint for given int color
     */
    protected Paint getPaintForColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(IS_ANTI_ALIAS);
        paint.setStrokeWidth(LINE_WIDTH_PX);
        paint.setStyle(PAINT_STYLE);
        paint.setStrokeJoin(PAINT_JOIN);
        paint.setStrokeCap(STROKE_CAP);
        paint.setTextSize(30f);
        return paint;
    }

    /**
     * Maps given points in data coordinates to view coordinates
     *
     * @param points in data coordinates
     * @return points in view coordinates
     */
    protected List<FloatPointModel> normalize(List<PointModel> points, BoundsModel bounds) {
        List<FloatPointModel> normalized = new ArrayList<>();
        for (PointModel point : points) {
            normalized.add(getFloatPointForPoint(point, bounds));
        }
        return normalized;
    }

    /**
     * @param point  in data coordinates
     * @param bounds curve bounds
     * @return Maps given point in data coordinates to view coordinates
     */
    protected FloatPointModel getFloatPointForPoint(PointModel point, BoundsModel bounds) {
        int width = getWidth();
        int height = getHeight() - mBaseYPadding;
        long minX = bounds.getMinX();
        long maxX = bounds.getMaxX();
        int minY = bounds.getMinY();
        int maxY = bounds.getMaxY();
        return new FloatPointModel((float) (point.getX() - minX) / (maxX - minX) * width,
                height - (float) (point.getY() - minY) / (maxY - minY) * (height));
    }

    public void initBaseYPadding(int mBaseYPadding) {
        this.mBaseYPadding = mBaseYPadding;
    }

    //================ View ==========================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (CurveModel curve : mCurves) {
            Paint paint = mPaintMap.get(curve);
            List<FloatPointModel> points = mNormalizedPointsMap.get(curve);
            if (paint == null || points == null) return;
            for (int i = 0; i < points.size() - 1; i++) {
                FloatPointModel point = points.get(i);
                FloatPointModel nextPoint = points.get(i + 1);
                canvas.drawLine(point.getX(), point.getY(), nextPoint.getX(), nextPoint.getY(), paint);
            }

        }
    }
    /////////////////////////////////////////////////////

    /**
     * Adjusts bounds for the given curve
     */
    private void adjustBoundsForCurve(CurveModel curve) {
        BoundsModel curveBounds = curve.getBounds();
        BoundsModel existingBounds = mBounds;
        BoundsModel mergedBounds = curveBounds.megre(existingBounds);
        if (mergedBounds.equals(existingBounds)) {
            List<FloatPointModel> normalized = normalize(curve.getPoints(), existingBounds);
            mNormalizedPointsMap.put(curve, normalized);
        } else {
            //New bounds should be set
            setBounds(mergedBounds);
        }
    }
}
