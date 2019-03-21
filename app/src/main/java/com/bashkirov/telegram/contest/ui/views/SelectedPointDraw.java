package com.bashkirov.telegram.contest.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.PointModel;
import com.bashkirov.telegram.contest.models.ViewPointModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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

    SelectedPointDraw(Context context) {

        this.mContext = context;


        mPadding = mContext.getResources().getDimension(R.dimen.default_margin);
        strokeWidth = mContext.getResources().getDimension(R.dimen.divider_thickness);

        mFramePaint = getFramePaint();
        mFillPaint = getFillPaint();
        mDateTextPaint = getDateTextPaint();

        mRadius = mContext.getResources().getDimension(R.dimen.selected_point_corner_radius);

    }


    void addData(CurveModel curve, int selectedPointIndex) {
        PointModel point = curve.getPoints().get(selectedPointIndex);
        int intValue = point.getY();
        String value = intValue < 10000 ? String.valueOf(intValue) : String.valueOf(Math.round((float) intValue / 1000) + "K");
        mPoints.add(
                new SelectedPointDataModel(
                        value,
                        curve.getName(),
                        getValueTextPaint(curve.getColor()),
                        getValueCaptionTextPaint(curve.getColor())
                )
        );
        mDate = mSimpleDateFormat.format(new Date(point.getX()));

    }

    void setPosition(float x, boolean isRight) {

        float width = mContext.getResources().getDimension(R.dimen.selected_point_view_width);
        float height = mContext.getResources().getDimension(R.dimen.selected_point_view_height);
        float offset = mContext.getResources().getDimension(R.dimen.selected_point_positioning_x_offset);
        float positionX = isRight ? x - offset : x - width + offset;
        ViewPointModel position = new ViewPointModel(
                positionX, mContext.getResources().getDimension(R.dimen.divider_thickness));

        topLeftX = position.getX();
        topLeftY = position.getY();

        bottomRightX = topLeftX + width;
        bottomRightY = topLeftY + height;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void draw(Canvas canvas) {
        canvas.drawRoundRect(topLeftX, topLeftY, bottomRightX, bottomRightY, mRadius, mRadius, mFramePaint);
        canvas.drawRoundRect(topLeftX + strokeWidth / 2,
                topLeftY + strokeWidth / 2,
                bottomRightX - strokeWidth / 2,
                bottomRightY - strokeWidth / 2,
                mRadius,
                mRadius,
                mFillPaint);
        canvas.drawText(mDate, topLeftX + mPadding, topLeftY + 1.5f * mPadding, mDateTextPaint);

        int count = 0;
        for (SelectedPointDataModel point : mPoints) {
            canvas.drawText(String.valueOf(point.value), topLeftX + mPadding + 200f * count, topLeftY + 3.5f * mPadding, point.valueTextPaint);
            canvas.drawText(String.valueOf(point.caption), topLeftX + mPadding + 200f * count, topLeftY + 5f * mPadding, point.valueCaptionTextPaint);
            count++;
        }
    }

    private Paint getFramePaint() {
        float px = mContext.getResources().getDimension(R.dimen.divider_thickness);
        Paint paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.colorDividerGrayDay));
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setShadowLayer(px, px, px, mContext.getResources().getColor(R.color.colorShadowDay));
        return paint;
    }

    private Paint getFillPaint() {
        Paint paint = new Paint();
        paint.setColor(mContext.getResources().getColor(R.color.colorWindowBackgroundDay));
        paint.setStrokeWidth(mContext.getResources().getDimension(R.dimen.divider_thickness));
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getDateTextPaint() {
        Paint paint = new Paint();
        paint.setTextSize(mContext.getResources().getDimension(R.dimen.selected_point_date_text_size));
        paint.setColor(mContext.getResources().getColor(R.color.colorPrimaryTextDay));
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

    class SelectedPointDataModel {
        private final String value;
        private final String caption;
        private final Paint valueTextPaint;
        private final Paint valueCaptionTextPaint;

        SelectedPointDataModel(String value,
                               String caption,
                               Paint valueTextPaint,
                               Paint valueCaptionTextPaint) {
            this.value = value;
            this.caption = caption;
            this.valueTextPaint = valueTextPaint;
            this.valueCaptionTextPaint = valueCaptionTextPaint;
        }
    }

}

