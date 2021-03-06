package com.bashkirov.telegram.contest.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.BoundsModel;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.PointModel;
import com.bashkirov.telegram.contest.models.ViewPointModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.bashkirov.telegram.contest.utils.ThemeUtils.getColorForAttrId;

/**
 * Provides rangable and detailed chart visualisation
 */
public class DetailedChartView extends BaseChartView implements RangeListener {

    private static final int X_TICS_COUNT = 5;
    private static final int Y_TICS_COUNT = 5;
    private static final float LIMIT_TO_SWITCH_SIDE = 0.7f;

    private final float mSelectedPointStrokeRadius = getResources()
            .getDimension(R.dimen.selected_point_radius);

    private final float mSelectedPointFillRadius =
            mSelectedPointStrokeRadius - getResources().getDimension(R.dimen.curve_width) / 2;

    private final float mPointSelectionSensibleArea = getResources()
            .getDimension(R.dimen.point_selection_sensible_area);

    private float mYTicksTextPadding;
    private float mXTicksTextPadding;

    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MMM dd", Locale.US);

    private int mYtickStep = 10; //Default value
    private long mXtickStep = 10L * 24L * 60L * 60L * 1000L;//10 Days in millis, default value

    private final List<Tick> mTicksX = new LinkedList<>();
    private final List<Tick> mTicksY = new LinkedList<>();
    private final Paint mScaleLinePaint = getScaleLinePaint();
    private final Paint mTextPaint = getTickTextPaint();

    private BoundsModel mInitialBounds;
    private Float mStartPosition;
    private Float mEndPosition;

    private final List<SelectedPoint> mSelectedPoints = new ArrayList<>();
    private SelectedPointDraw mSelectedPointDraw = null;
    private Integer mSelectedPointIndex;

    //================== Constructors ========================

    public DetailedChartView(Context context) {
        this(context, null);
    }

    public DetailedChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailedChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTouchListener();
        setPadding();

       /*Uncomment this to enable shadows in paintings.
        This will cost disabling hardware acceleration that may affect the performance.
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //Enable shadows
            setLayerType(LAYER_TYPE_SOFTWARE, new Paint());
        }
        */
    }

    /////////////////////////////////////////////////////////////

    @Override
    public void loadChart(ChartModel chart) {
        super.loadChart(chart);
        mInitialBounds = getBounds();
        mXtickStep = Math.round(
                (float) (mInitialBounds.getMaxX() - mInitialBounds.getMinX()) / X_TICS_COUNT);
        mYtickStep = Math.round(
                (float) (mInitialBounds.getMaxY() - mInitialBounds.getMinY()) / Y_TICS_COUNT);
        roundYstep();
        if (mStartPosition != null && mEndPosition != null) {
            onRangeChange(mStartPosition, mEndPosition);
        }
    }

    public Integer getSelectedPointIndex() {
        return mSelectedPointIndex;
    }

    public void setSelectedPointIndex(Integer selectedPointIndex) {
        processSelectedPointIndex(selectedPointIndex);
    }

    // ============= Override base behaviour ==========

    @Override
    void setBounds(BoundsModel bounds) {
        super.setBounds(bounds);
        setTicksX();
        setTicksY();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Draw tick lines
        for (Tick tick : mTicksY) {
            canvas.drawLine(tick.floatPoint.getX(), tick.floatPoint.getY(), getWidth(),
                    tick.floatPoint.getY(), mScaleLinePaint);
            canvas.drawText(String.valueOf(tick.text), tick.floatPoint.getX(),
                    tick.floatPoint.getY() - mYTicksTextPadding, mTextPaint);
        }
        //Draw curves
        super.onDraw(canvas);

        //Draw tick labels
        for (Tick tick : mTicksX) {
            canvas.drawText(String.valueOf(tick.text), tick.floatPoint.getX(),
                    tick.floatPoint.getY() + mXTicksTextPadding, mTextPaint);
        }
        Float verticalLineX = null;

        //Draw selected point
        if (!mSelectedPoints.isEmpty()) {
            verticalLineX = mSelectedPoints.get(0).getX();
            canvas.drawLine(verticalLineX, 0, verticalLineX,
                    mTicksY.get(0).floatPoint.getY(), mScaleLinePaint);
        }

        for (SelectedPoint selectedPoint : mSelectedPoints) {
            canvas.drawCircle(selectedPoint.getX(), selectedPoint.getY(),
                    mSelectedPointStrokeRadius, selectedPoint.strokePaint);
            canvas.drawCircle(selectedPoint.getX(), selectedPoint.getY(),
                    mSelectedPointFillRadius, selectedPoint.fillPaint);
        }
        if (mSelectedPointDraw != null && verticalLineX != null) {
            mSelectedPointDraw.draw(canvas);
        }
    }

    @Override
    public void setCurveVisible(CurveModel curveModel, boolean visible) {
        super.setCurveVisible(curveModel, visible);
        clearSelection();
    }

    @Override
    public void clear() {
        super.clear();
        clearSelection();
    }
    //////////////////////////////////////////////

    private void setPadding() {
        mYTicksTextPadding = getResources().getDimension(R.dimen.y_tick_label_padding);
        Rect rect = new Rect();
        mTextPaint.getTextBounds("0", 0, 1, rect);
        float height = (float) rect.height();
        mXTicksTextPadding = height + getResources().getDimension(R.dimen.x_tick_label_padding);
        mCurvePaddingBottom = getResources().getDimension(R.dimen.curve_padding) + mXTicksTextPadding;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initTouchListener() {
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                Integer selectedPointIndex = null;
                for (Map.Entry<CurveModel, List<ViewPointModel>> entry
                        : mNormalizedPointsMap.entrySet()) {
                    List<ViewPointModel> points = entry.getValue();
                    for (ViewPointModel point : points) {
                        float pointX = point.getX();
                        float pointY = point.getY();
                        if (Math.abs(x - pointX) <= mPointSelectionSensibleArea &&
                                Math.abs(y - pointY) <= mPointSelectionSensibleArea) {
                            selectedPointIndex = points.indexOf(point);
                        }
                    }
                }
                clearSelection();
                processSelectedPointIndex(selectedPointIndex);
            }
            return true;
        });
    }

    private void clearSelection() {
        mSelectedPoints.clear();
        mSelectedPointDraw = null;
    }

    private void setTicksY() {
        BoundsModel processedBounds = getBounds();
        int dif = processedBounds.getMaxY() - processedBounds.getMinY();
        if (dif / mYtickStep < Y_TICS_COUNT - 1) mYtickStep = mYtickStep / 2;
        if (dif / mYtickStep > Y_TICS_COUNT) mYtickStep = mYtickStep * 2;
        mTicksY.clear();
        roundYstep();
        int minY = (processedBounds.getMinY() / Y_TICS_COUNT) * Y_TICS_COUNT;
        int maxY = processedBounds.getMaxY();
        for (int i = minY; i < maxY; i += mYtickStep) {
            PointModel point = new PointModel(processedBounds.getMinX(), i);
            ViewPointModel floatPoint = getViewPointForPoint(point, processedBounds);
            mTicksY.add(new Tick(String.valueOf(point.getY()), point, floatPoint));
        }
    }

    private void setTicksX() {
        BoundsModel processedBounds = getBounds();
        long dif = processedBounds.getMaxX() - processedBounds.getMinX();
        long oldStep = mXtickStep;
        if (dif / mXtickStep < X_TICS_COUNT - 1) mXtickStep = mXtickStep / 2;
        if (dif / mXtickStep > X_TICS_COUNT) mXtickStep = mXtickStep * 2;
        if (oldStep == mXtickStep) {
            List<Tick> oldTicks = new LinkedList<>(mTicksX);
            mTicksX.clear();
            for (Tick tick : oldTicks) {
                PointModel point = new PointModel(tick.point.getX(), processedBounds.getMinY());
                mTicksX.add(new Tick(tick.text, point, getViewPointForPoint(point, processedBounds)));
            }
            Tick first = mTicksX.get(0);
            if (first.point.getX() - processedBounds.getMinX() > mXtickStep) {
                PointModel point = new PointModel(first.point.getX() - mXtickStep,
                        processedBounds.getMinY());
                ViewPointModel floatPoint = getViewPointForPoint(point, processedBounds);
                mTicksX.add(0, new Tick(
                        mSimpleDateFormat.format(new Date(point.getX())), point, floatPoint));
                return;
            }
            Tick last = mTicksX.get(mTicksX.size() - 1);
            if (processedBounds.getMaxX() - last.point.getX() > mXtickStep) {
                PointModel point = new PointModel(last.point.getX() + mXtickStep,
                        processedBounds.getMinY());
                ViewPointModel floatPoint = getViewPointForPoint(point, processedBounds);
                mTicksX.add(new Tick(mSimpleDateFormat.format(
                        new Date(point.getX())), point, floatPoint));
            }
        } else {
            mTicksX.clear();
            long minX = ((processedBounds.getMinX()) / Y_TICS_COUNT) * Y_TICS_COUNT;
            long maxX = processedBounds.getMaxX();
            for (long i = minX; i < maxX; i += mXtickStep) {
                PointModel point = new PointModel(i, processedBounds.getMinY());
                ViewPointModel floatPoint = getViewPointForPoint(point, processedBounds);
                mTicksX.add(new Tick(
                        mSimpleDateFormat.format(new Date(point.getX())), point, floatPoint));
            }
        }
    }

    private void processSelectedPointIndex(Integer selectedPointIndex) {
        mSelectedPointIndex = selectedPointIndex;
        if (selectedPointIndex == null) {
            invalidate();
            return;
        }
        mSelectedPointDraw = new SelectedPointDraw(getContext());
        for (Map.Entry<CurveModel, List<ViewPointModel>> entry : mNormalizedPointsMap.entrySet()) {
            CurveModel curve = entry.getKey();
            List<ViewPointModel> points = entry.getValue();
            mSelectedPoints.add(
                    new SelectedPoint(points.get(selectedPointIndex),
                            getStrokePaintForSelectedPoint(curve.getColor()),
                            getFillPaintForSelectedPoint()));

            mSelectedPointDraw.addData(curve, selectedPointIndex);
        }
        if (!mSelectedPoints.isEmpty()) {
            float pointX = mSelectedPoints.get(0).point.getX();
            mSelectedPointDraw.setPosition(pointX, pointX / getWidth() < LIMIT_TO_SWITCH_SIDE);
        }
        invalidate();
    }

    private Paint getScaleLinePaint() {
        Paint paint = new Paint();
        paint.setColor(getColorForAttrId(getContext(), android.R.attr.divider));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.divider_thickness));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private Paint getTickTextPaint() {
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.tick_text_size));
        paint.setColor(getColorForAttrId(getContext(), R.attr.attrTickLabel));
        return paint;
    }

    private Paint getFillPaintForSelectedPoint() {
        Paint paint = new Paint();
        paint.setColor(getColorForAttrId(getContext(), android.R.attr.windowBackground));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getStrokePaintForSelectedPoint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.curve_width));
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private void roundYstep() {
        mYtickStep = (mYtickStep / Y_TICS_COUNT) * Y_TICS_COUNT;
    }

    //============ RangeListener =====================
    @Override
    public void onRangeChange(float start, float end) {
        clearSelection();
        mStartPosition = start;
        mEndPosition = end;
        if (end < start) return;
        BoundsModel initialBounds = mInitialBounds;
        if (initialBounds == null) return;
        Float xStartShift = start * (initialBounds.getMaxX() - initialBounds.getMinX());
        Float xEndShift = end * (initialBounds.getMaxX() - initialBounds.getMinX());
        BoundsModel newBounds = new BoundsModel(
                initialBounds.getMinX() + xStartShift.longValue(),
                initialBounds.getMinX() + xEndShift.longValue(),
                initialBounds.getMinY(), initialBounds.getMaxY());
        setBounds(newBounds);
        invalidate();
    }
    //////////////////////////////////////////////////////

    /**
     * Provides model for plot tick label (text and position)
     */
    private class Tick {
        private final String text;
        private final PointModel point;
        private final ViewPointModel floatPoint;

        Tick(String text, PointModel point, ViewPointModel floatPoint) {
            this.text = text;
            this.point = point;
            this.floatPoint = floatPoint;
        }
    }

    private class SelectedPoint {
        private final ViewPointModel point;
        private final Paint strokePaint;
        private final Paint fillPaint;

        SelectedPoint(ViewPointModel point, Paint strokePaint, Paint fillPaint) {
            this.point = point;
            this.strokePaint = strokePaint;
            this.fillPaint = fillPaint;
        }

        float getX() {
            return point.getX();
        }

        float getY() {
            return point.getY();
        }
    }
}