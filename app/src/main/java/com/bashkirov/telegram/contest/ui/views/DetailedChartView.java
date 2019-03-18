package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.BoundsModel;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.FloatPointModel;
import com.bashkirov.telegram.contest.models.PointModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Provides rangable and decorated chart visualisation
 */
public class DetailedChartView extends BaseChartView implements RangeListener {

    private static final int NUMBER_OF_X_TICS = 5;
    private static final int DEFAULT_Y_TICS_STEP = 50;

    private static final int DEFAULT_Y_TICS_TEXT_PADDING_PX = -16;
    private static final int DEFAULT_X_TICS_TEXT_PADDING_PX = 60;
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MMM dd", Locale.US);

    private List<Tick> mTicksX = new LinkedList<>();
    private List<Tick> mTicksY = new LinkedList<>();
    private Paint mScaleLinePaint = getScaleLinePaint();
    private Paint mTextPaint = getTickTextPaint();

    private BoundsModel mInitialBounds;
    private Float mStartPosition;
    private Float mEndPosition;

    public DetailedChartView(Context context) {
        this(context, null);
    }

    public DetailedChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DetailedChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBaseYPadding(DEFAULT_X_TICS_TEXT_PADDING_PX + 20);
    }

    @Override
    public void loadChart(ChartModel chart) {
        super.loadChart(chart);
        mInitialBounds = getBounds();
        if (mStartPosition != null && mEndPosition != null) {
            onRangeChange(mStartPosition, mEndPosition);
        }
    }

    // ============= Override default behaviour ==========

    @Override
    public void setBounds(BoundsModel bounds) {
        super.setBounds(bounds);
        setTicksX();
        setTicksY();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Tick tick : mTicksY) {
            canvas.drawLine(tick.floatPoint.getX(), tick.floatPoint.getY(), getWidth(), tick.floatPoint.getY(), mScaleLinePaint);
            canvas.drawText(String.valueOf(tick.text), tick.floatPoint.getX(), tick.floatPoint.getY() + DEFAULT_Y_TICS_TEXT_PADDING_PX, mTextPaint);
        }
        for (Tick tick : mTicksX) {
            canvas.drawText(String.valueOf(tick.text), tick.floatPoint.getX(), tick.floatPoint.getY() + DEFAULT_X_TICS_TEXT_PADDING_PX, mTextPaint);
        }
        super.onDraw(canvas);
    }

    //////////////////////////////////////////////

    private void setTicksY() {
        BoundsModel processedBounds = getBounds();
        mTicksY.clear();
        int minY = processedBounds.getMinY();
        int maxY = processedBounds.getMaxY();
        for (int i = minY; i < maxY; i += DEFAULT_Y_TICS_STEP) {
            PointModel point = new PointModel(processedBounds.getMinX(), i);
            FloatPointModel floatPoint = getFloatPointForPoint(point, processedBounds);
            mTicksY.add(new Tick(String.valueOf(point.getY()), floatPoint));
        }
    }

    private void setTicksX() {
        BoundsModel processedBounds = getBounds();
        mTicksX.clear();
        long minX = processedBounds.getMinX();
        long maxX = processedBounds.getMaxX();
        for (long i = minX; i < maxX; i += (maxX - minX) / NUMBER_OF_X_TICS) {
            PointModel point = new PointModel(i, 0);
            FloatPointModel floatPoint = getFloatPointForPoint(point, processedBounds);
            mTicksX.add(new Tick(mSimpleDateFormat.format(new Date(point.getX())), floatPoint));
        }
    }

    private Paint getScaleLinePaint() {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.black10));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.divider_height));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    private Paint getTickTextPaint() {
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.tick_text_size));
        paint.setColor(getResources().getColor(R.color.black60));
        return paint;
    }


    //============ RangeListener =====================
    @Override
    public void onRangeChange(float start, float end) {
        mStartPosition = start;
        mEndPosition = end;
        if (end < start) return;
        BoundsModel initialBounds = mInitialBounds;
        if (initialBounds == null) return;
        Float xStartShift = start * (initialBounds.getMaxX() - initialBounds.getMinX());
        Float xEndShift = end * (initialBounds.getMaxX() - initialBounds.getMinX());
        BoundsModel newBounds = new BoundsModel(initialBounds.getMinX() + xStartShift.longValue(),
                initialBounds.getMinX() + xEndShift.longValue(), initialBounds.getMinY(), initialBounds.getMaxY());
        setBounds(newBounds);
        invalidate();
    }
    //////////////////////////////////////////////////////

    private class Tick {
        private final String text;
        private final FloatPointModel floatPoint;

        Tick(String text, FloatPointModel floatPoint) {
            this.text = text;
            this.floatPoint = floatPoint;
        }
    }

}