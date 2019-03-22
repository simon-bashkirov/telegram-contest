package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.PointModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.bashkirov.telegram.contest.utils.ThemeUtils.getColorForAttrId;

/**
 * Draws selected point details on canvas.
 * Note from the author: in most cases it is not a good way get such views by calculating and drawing on canvas.
 * Generally (and much less efficiently in terms of performance) XML layouts are inflated and used. But not this time.
 * <p>
 * This class may look somehow ugly, but it does for it is made for and works fast.
 */
class SelectedPointDraw {
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.US);

    private final Context mContext;
    private float topLeftX;
    private float topLeftY;
    private float bottomRightX;
    private float bottomRightY;
    private final float strokeWidth;
    private final float mRadius;

    private final Paint mFramePaint;
    private final Paint mFillPaint;
    private final Paint mDateTextPaint;

    private final List<SelectedPointDataModel> mPoints = new LinkedList<>();
    private String mDate;

    private final float mPadding;
    private final float mPaddingBetweenPoints;

    SelectedPointDraw(Context context) {

        this.mContext = context;

        mPadding = mContext.getResources().getDimension(R.dimen.default_margin);
        mPaddingBetweenPoints = 2 * mPadding;

        strokeWidth = mContext.getResources().getDimension(R.dimen.divider_thickness);

        mFramePaint = getFramePaint();
        mFillPaint = getFillPaint();
        mDateTextPaint = getDateTextPaint();

        mRadius = mContext.getResources().getDimension(R.dimen.selected_point_corner_radius);
    }

    private float mTitleLength;
    private float mAllContentLength;

    private float mFirstLineHeight;
    private float mSecondLineHeight;
    private float mThirdLineHeight;

    void addData(CurveModel curve, int selectedPointIndex) {
        PointModel point = curve.getPoints().get(selectedPointIndex);
        int intValue = point.getY();
        String value = intValue < 10000 ? String.valueOf(intValue) : String.valueOf(Math.round((float) intValue / 1000) + "K");
        String caption = curve.getName();
        Paint valueTextPaint = getValueTextPaint(curve.getColor());
        Paint captionTextPaint = getValueCaptionTextPaint(curve.getColor());

        mDate = mSimpleDateFormat.format(new Date(point.getX()));
        Rect firstLineBounds = new Rect();
        mDateTextPaint.getTextBounds(mDate, 0, mDate.length(), firstLineBounds);
        mTitleLength = firstLineBounds.width();
        mFirstLineHeight = firstLineBounds.height();

        Rect secondLineBounds = new Rect();
        valueTextPaint.getTextBounds(value, 0, value.length(), secondLineBounds);
        mSecondLineHeight = secondLineBounds.height();

        Rect thirdLineBounds = new Rect();
        captionTextPaint.getTextBounds(caption, 0, caption.length(), thirdLineBounds);
        mThirdLineHeight = thirdLineBounds.height();

        float currentContentLength = Math.max(secondLineBounds.width(), thirdLineBounds.width());
        mAllContentLength += currentContentLength;
        mPoints.add(
                new SelectedPointDataModel(
                        value,
                        caption,
                        valueTextPaint,
                        captionTextPaint,
                        currentContentLength
                ));
    }

    void setPosition(float x, boolean isRight) {
        float width = Math.max(
                mPadding * 2 + mTitleLength,
                mPadding * 2 + mAllContentLength + (mPoints.size() - 1) * mPaddingBetweenPoints
        );

        float height = 2.5f * mPadding + mFirstLineHeight + mSecondLineHeight + mThirdLineHeight;
        float offset = mContext.getResources().getDimension(R.dimen.selected_point_positioning_x_offset);
        topLeftX = isRight ? x - offset : x - width + offset;
        topLeftY = mContext.getResources().getDimension(R.dimen.divider_thickness);
        bottomRightX = topLeftX + width;
        bottomRightY = topLeftY + height;
    }

    void draw(Canvas canvas) {
        if (mPoints.isEmpty()) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(topLeftX, topLeftY, bottomRightX,
                    bottomRightY, mRadius, mRadius, mFramePaint);
            canvas.drawRoundRect(topLeftX + strokeWidth / 2,
                    topLeftY + strokeWidth / 2,
                    bottomRightX - strokeWidth / 2,
                    bottomRightY - strokeWidth / 2,
                    mRadius,
                    mRadius,
                    mFillPaint);

        } else {
            canvas.drawRect(topLeftX, topLeftY, bottomRightX, bottomRightY, mFramePaint);
            canvas.drawRect(topLeftX + strokeWidth / 2,
                    topLeftY + strokeWidth / 2,
                    bottomRightX - strokeWidth / 2,
                    bottomRightY - strokeWidth / 2,
                    mFillPaint);
        }
        canvas.drawText(mDate,
                topLeftX + mPadding,
                topLeftY + 0.5f * mPadding + mFirstLineHeight, mDateTextPaint);
        float padding = mPadding;
        for (SelectedPointDataModel point : mPoints) {
            canvas.drawText(String.valueOf(point.value),
                    topLeftX + padding,
                    topLeftY + 1.5f * mPadding + mFirstLineHeight + mSecondLineHeight,
                    point.valueTextPaint);
            canvas.drawText(String.valueOf(point.caption),
                    topLeftX + padding,
                    topLeftY + 2.0f * mPadding + mFirstLineHeight +
                            mSecondLineHeight + mThirdLineHeight,
                    point.valueCaptionTextPaint);
            padding += point.length + mPaddingBetweenPoints;
        }
    }

    private Paint getFramePaint() {
        float px = mContext.getResources().getDimension(R.dimen.divider_thickness);
        Paint paint = new Paint();
        paint.setColor(getColorForAttrId(mContext, android.R.attr.divider));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShadowLayer(px, px, px, getColorForAttrId(mContext, R.attr.attrShadow));
        return paint;
    }

    private Paint getFillPaint() {
        Paint paint = new Paint();
        paint.setColor(getColorForAttrId(mContext, R.attr.attrSelectedPointFill));
        paint.setStrokeWidth(mContext.getResources().getDimension(R.dimen.divider_thickness));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getDateTextPaint() {
        Paint paint = new Paint();
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.selected_point_date_text_size));
        paint.setColor(getColorForAttrId(mContext, android.R.attr.textColorPrimary));
        return paint;
    }

    private Paint getValueTextPaint(int color) {
        Paint paint = new Paint();
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.selected_point_value_text_size));
        paint.setColor(color);
        return paint;
    }

    private Paint getValueCaptionTextPaint(int color) {
        Paint paint = new Paint();
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.selected_point_caption_text_size));
        paint.setColor(color);
        return paint;
    }

    private class SelectedPointDataModel {
        final String value;
        final String caption;
        final Paint valueTextPaint;
        final Paint valueCaptionTextPaint;
        final float length;

        SelectedPointDataModel(String value,
                               String caption,
                               Paint valueTextPaint,
                               Paint valueCaptionTextPaint,
                               float length) {
            this.value = value;
            this.caption = caption;
            this.valueTextPaint = valueTextPaint;
            this.valueCaptionTextPaint = valueCaptionTextPaint;
            this.length = length;
        }
    }

}

