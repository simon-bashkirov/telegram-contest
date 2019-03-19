package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.BoundsModel;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.PointModel;
import com.bashkirov.telegram.contest.models.ViewPointModel;

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
    private final static boolean IS_ANTI_ALIAS = true;
    private final static Paint.Style PAINT_STYLE = Paint.Style.STROKE;
    private final static Paint.Join PAINT_JOIN = Paint.Join.ROUND;
    private final static Paint.Cap STROKE_CAP = Paint.Cap.ROUND;

    //Common fields
    protected List<CurveModel> mCurves = new LinkedList<>();
    protected Map<CurveModel, List<ViewPointModel>> mNormalizedPointsMap = new HashMap<>();
    protected Map<CurveModel, Boolean> mCurvesVisibility = new HashMap<>();

    //Private field
    private Map<CurveModel, Paint> mPaintMap = new HashMap<>();
    private BoundsModel mBounds;
    private int mBaseYPadding = 0;

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
     * @param  chart chart to be loaded
     */
    public void loadChart(ChartModel chart) {
        List<CurveModel> curves = chart.getCurves();
        for (CurveModel curveModel : curves) {
            loadCurve(curveModel);
        }
        invalidate();
    }

    /**
     * Clears the data
     */
    public void clear() {
        mCurves.clear();
        invalidate();
    }

    /**
     * Loads single @param curve to be displayed
     * @param curve curve to be loaded
     */
    public void loadCurve(CurveModel curve) {
        mCurves.add(curve);
        mPaintMap.put(curve, getCurvePaintForColor(curve.getColor()));
        mCurvesVisibility.put(curve, true);
        adjustBoundsForCurve(curve);
    }

    /**
     * Sets bound of data range
     *
     * @param bounds bounds that limit display data range
     */
    public void setBounds(BoundsModel bounds) {
        mNormalizedPointsMap.clear();
        BoundsModel adjustedBounds = null;
        for (CurveModel aCurve : mCurves) {
            Boolean visible = mCurvesVisibility.get(aCurve);
            if (visible == null || !visible) continue;
            if (adjustedBounds == null)
                adjustedBounds = aCurve.adjustBoundsHeight(bounds.getMinX(), bounds.getMaxX());
            else
                adjustedBounds = adjustedBounds.megre(aCurve.adjustBoundsHeight(bounds.getMinX(), bounds.getMaxX()));
            List<ViewPointModel> normalized = normalize(aCurve.getPoints(), adjustedBounds);
            mNormalizedPointsMap.put(aCurve, normalized);
        }
        if (adjustedBounds != null) {
            //null case occurs when all curves set invisible
            mBounds = adjustedBounds;
        }
    }

    /**
     * Sets visibility of a specified curve
     *
     * @param curveModel the curve to set visible/invisible
     * @param visible    the curve is visible if true
     */
    public void setCurveVisible(CurveModel curveModel, boolean visible) {
        if (mCurvesVisibility.containsKey(curveModel)) {
            mCurvesVisibility.remove(curveModel);
            mCurvesVisibility.put(curveModel, visible);
            recalculateBounds();
            invalidate();
        }
    }

    //========= Protected methods ==============

    /**
     * Maps given points in data coordinates to view coordinates
     *
     * @param points in data coordinates
     * @return points in view coordinates
     */
    protected List<ViewPointModel> normalize(List<PointModel> points, BoundsModel bounds) {
        List<ViewPointModel> normalized = new ArrayList<>();
        for (PointModel point : points) {
            normalized.add(getViewPointForPoint(point, bounds));
        }
        return normalized;
    }

    /**
     * @param point  in data coordinates
     * @param bounds curve bounds
     * @return Maps given point in data coordinates to view coordinates
     */
    protected ViewPointModel getViewPointForPoint(PointModel point, BoundsModel bounds) {
        int width = getWidth();
        int height = getHeight() - mBaseYPadding;
        long minX = bounds.getMinX();
        long maxX = bounds.getMaxX();
        int minY = bounds.getMinY();
        int maxY = bounds.getMaxY();
        return new ViewPointModel((float) (point.getX() - minX) / (maxX - minX) * width,
                height - (float) (point.getY() - minY) / (maxY - minY) * (height));
    }

    @SuppressWarnings("SameParameterValue")
    protected void initBaseYPadding(int mBaseYPadding) {
        this.mBaseYPadding = mBaseYPadding;
    }

    protected BoundsModel getBounds() {
        return mBounds;
    }

    //================ View ==========================
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (CurveModel curve : mCurves) {
            Paint paint = mPaintMap.get(curve);
            List<ViewPointModel> points = mNormalizedPointsMap.get(curve);
            if (paint == null || points == null) continue;
            for (int i = 0; i < points.size() - 1; i++) {
                ViewPointModel point = points.get(i);
                ViewPointModel nextPoint = points.get(i + 1);
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
            List<ViewPointModel> normalized = normalize(curve.getPoints(), existingBounds);
            mNormalizedPointsMap.put(curve, normalized);
        } else {
            //New bounds should be set
            setBounds(mergedBounds);
        }
    }

    private void recalculateBounds() {
        setBounds(mBounds);
    }

    /**
     * @return paint for given int color
     */
    private Paint getCurvePaintForColor(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(IS_ANTI_ALIAS);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.curve_width));
        paint.setStyle(PAINT_STYLE);
        paint.setStrokeJoin(PAINT_JOIN);
        paint.setStrokeCap(STROKE_CAP);
        return paint;
    }
}
