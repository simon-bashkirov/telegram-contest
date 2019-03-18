package com.bashkirov.telegram.contest.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bashkirov.telegram.contest.R;

public class CustomSeekBar extends View {

    private final static float SENSITIVITY_LENGTH = 0.1f;

    private final static int FILL_COLOR_ID = R.color.black10;
    private final static int FRAME_COLOR_ID = R.color.blue20;

    private final static float MIN_WIDTH = SENSITIVITY_LENGTH * 3.5f;
    private final static float DEFAULT_END_POSITION = 1.0f;
    private final static float DEFAULT_START_POSITION = DEFAULT_END_POSITION - MIN_WIDTH;

    private final Paint mFillPaint = getFillPaint();
    private final Paint mFramePaint = getFramePaint();
    private final float mStrokeWidth = getResources().getDimension(R.dimen.seek_thumb__stroke_width);

    private float mStartPosition = DEFAULT_START_POSITION;
    private float mEndPosition = DEFAULT_END_POSITION;

    private RangeListener mListener;

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private Touch mTouch = null;

    @SuppressLint("ClickableViewAccessibility")
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touch = event.getX() / getWidth();
                if (touch < mStartPosition + SENSITIVITY_LENGTH && touch > mStartPosition - SENSITIVITY_LENGTH) {
                    this.mTouch = Touch.START;
                }

                if (touch < mEndPosition + SENSITIVITY_LENGTH && touch > mEndPosition - SENSITIVITY_LENGTH) {
                    this.mTouch = Touch.END;
                }
                if (touch > mStartPosition + SENSITIVITY_LENGTH && touch < mEndPosition - SENSITIVITY_LENGTH) {
                    this.mTouch = Touch.CENTER;
                }

                invalidate();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                if (mTouch == Touch.START) {
                    float newStartPosition = event.getX() / getWidth();
                    if ((newStartPosition < mStartPosition || mEndPosition - mStartPosition >= MIN_WIDTH) && newStartPosition >= 0f) {
                        mStartPosition = newStartPosition;
                    }

                }
                if (mTouch == Touch.CENTER) {
                    float centerPosition = event.getX() / getWidth();
                    float dif = mEndPosition - mStartPosition;
                    float newEndPosition = centerPosition + dif / 2f;
                    float newStartPosition = centerPosition - dif / 2f;
                    if (newStartPosition >= 0 && newEndPosition <= 1) {
                        mStartPosition = newStartPosition;
                        mEndPosition = newEndPosition;
                    }
                }
                if (mTouch == Touch.END) {
                    float newEndPosition = event.getX() / getWidth();
                    if ((newEndPosition > mEndPosition || mEndPosition - mStartPosition >= MIN_WIDTH) && newEndPosition <= 1f) {
                        mEndPosition = event.getX() / getWidth();
                    }
                }
                mListener.onRangeChange(mStartPosition, mEndPosition);
                invalidate();
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mTouch = null;
                invalidate();
                return true;
            }
            return false;
        });
    }

    public void setListener(RangeListener listener) {
        this.mListener = listener;
        listener.onRangeChange(mStartPosition, mEndPosition);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        float startFrame = mStartPosition * width;
        float endFrame = mEndPosition * width;
        canvas.drawRect(0, 0, startFrame, height, mFillPaint);
        canvas.drawRect(endFrame, 0, width, height, mFillPaint);
        canvas.drawRect(startFrame + mStrokeWidth / 2, mStrokeWidth / 2, endFrame - mStrokeWidth / 2, height - mStrokeWidth / 2, mFramePaint);

    }


    public Paint getFillPaint() {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(FILL_COLOR_ID));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public Paint getFramePaint() {
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(FRAME_COLOR_ID));
        paint.setStrokeWidth(getResources().getDimension(R.dimen.seek_thumb__stroke_width));
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }


    private enum Touch {
        START,
        CENTER,
        END
    }


}

