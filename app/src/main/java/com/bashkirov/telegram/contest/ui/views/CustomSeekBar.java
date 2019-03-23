package com.bashkirov.telegram.contest.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.utils.ThemeUtils;

public class CustomSeekBar extends View {

    //Constants
    private final static float SENSITIVITY_LENGTH = 0.1f;
    private final static float MIN_WIDTH = SENSITIVITY_LENGTH * 3.5f;
    private final Paint mFillPaint = getFillPaint();
    private final Paint mFramePaint = getFramePaint();
    private final float mStrokeWidth = getResources().getDimension(R.dimen.seek_thumb_stroke_width);
    private final static float DEFAULT_END_POSITION = 1.0f;
    private final static float DEFAULT_START_POSITION = DEFAULT_END_POSITION - MIN_WIDTH;

    //Private fields
    private float mStartPosition = DEFAULT_START_POSITION;
    private float mEndPosition = DEFAULT_END_POSITION;
    private RangeListener mListener;
    private Touch mTouch = null;

    //=================== Constructors ==========================
    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initOnTouchListener();
    }

    //================== Public methods ===========================
    public void setListener(RangeListener listener) {
        this.mListener = listener;
        listener.onRangeChange(mStartPosition, mEndPosition);
    }

    public float getStartPosition() {
        return mStartPosition;
    }

    public float getEndPosition() {
        return mEndPosition;
    }

    public void setPositions(Float startPosition, Float endPosition) {
        this.mStartPosition = startPosition != null ? startPosition : DEFAULT_START_POSITION;
        this.mEndPosition = endPosition != null ? endPosition : DEFAULT_END_POSITION;
        mListener.onRangeChange(mStartPosition, mEndPosition);
        invalidate();
    }

    //////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float startFrame = mStartPosition * width;
        float endFrame = mEndPosition * width;
        canvas.drawRect(0, 0, startFrame, height, mFillPaint);
        canvas.drawRect(endFrame, 0, width, height, mFillPaint);
        canvas.drawRect(startFrame + mStrokeWidth / 2, 0,
                endFrame - mStrokeWidth / 2, height, mFramePaint);
    }

    private Paint getFillPaint() {
        Paint paint = new Paint();
        paint.setColor(ThemeUtils.getColorForAttrId(getContext(), R.attr.attrSeekBarFill));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getFramePaint() {
        Paint paint = new Paint();
        paint.setColor(ThemeUtils.getColorForAttrId(getContext(), R.attr.attrSeekBarStroke));
        paint.setStrokeWidth(getResources().getDimension(R.dimen.seek_thumb_stroke_width));
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOnTouchListener() {
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                handleActionDown(event.getX());
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                handleActionMove(event.getX());
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handleActionUp();
            }
            return true;
        });
    }

    /**
     * Handles action down.
     * Sets mTouch to START, CENTER or END depending on @param x and SENSITIVITY_LENGTH
     * *
     * @param x event coordinate
     */
    private void handleActionDown(float x) {
        float touch = x / getWidth();
        if (touch < mStartPosition + SENSITIVITY_LENGTH &&
                touch > mStartPosition - SENSITIVITY_LENGTH) {
            this.mTouch = Touch.START;
        }

        if (touch < mEndPosition + SENSITIVITY_LENGTH &&
                touch > mEndPosition - SENSITIVITY_LENGTH) {
            this.mTouch = Touch.END;
        }
        if (touch > mStartPosition + SENSITIVITY_LENGTH &&
                touch < mEndPosition - SENSITIVITY_LENGTH) {
            this.mTouch = Touch.CENTER;
        }

        invalidate();
    }

    /**
     * Handles action move. Change mStartPosition or/and mEndPosition according to @param x.
     * Notifies listener that range has been changed.
     *
     * @param x event coordinate
     */
    private void handleActionMove(float x) {
        if (mTouch == Touch.START) {
            float newStartPosition = x / getWidth();
            if ((newStartPosition < mStartPosition ||
                    mEndPosition - mStartPosition >= MIN_WIDTH) && newStartPosition >= 0f) {
                mStartPosition = newStartPosition;
            }

        }
        if (mTouch == Touch.CENTER) {
            float centerPosition = x / getWidth();
            float dif = mEndPosition - mStartPosition;
            float newEndPosition = centerPosition + dif / 2f;
            float newStartPosition = centerPosition - dif / 2f;
            if (newStartPosition >= 0 && newEndPosition <= 1) {
                mStartPosition = newStartPosition;
                mEndPosition = newEndPosition;
            }
        }
        if (mTouch == Touch.END) {
            float newEndPosition = x / getWidth();
            if ((newEndPosition > mEndPosition ||
                    mEndPosition - mStartPosition >= MIN_WIDTH) &&
                    newEndPosition <= 1f) {
                mEndPosition = x / getWidth();
            }
        }
        mListener.onRangeChange(mStartPosition, mEndPosition);
        invalidate();
    }

    /**
     * Handles action up. Clears mTouch
     */
    private void handleActionUp() {
        mTouch = null;
        invalidate();
    }

    /**
     * Provides positions where user touch this control.
     */
    private enum Touch {
        START,
        CENTER,
        END
    }
}

