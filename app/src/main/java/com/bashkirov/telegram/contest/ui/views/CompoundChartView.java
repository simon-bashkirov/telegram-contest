package com.bashkirov.telegram.contest.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.CurveModel;

import java.util.List;

public class CompoundChartView extends LinearLayout {

    private ChartModel mChartModel;

    //Components
    private SimpleChartView mSimpleChartView;
    private DetailedChartView mDetailedChartView;
    private SeekBar mSeekBar;
    private LinearLayout mCheckGroup;


    public CompoundChartView(Context context) {
        this(context, null);
    }

    public CompoundChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_chart_compound, this);
        setOrientation(VERTICAL);
        initViews();
    }

    //========= Public methods ===================
    public void loadChart(ChartModel chartModel) {
        mChartModel = chartModel;
        mSimpleChartView.loadChart(chartModel);
        mDetailedChartView.loadChart(chartModel);
        setCheckGroup();
    }

    //////////////////////////////////////////////////
    private void initViews() {
        mSimpleChartView = findViewById(R.id.simple_chart_view);
        mDetailedChartView = findViewById(R.id.rangable_chart_view);
        mSeekBar = findViewById(R.id.seekBar);
        mCheckGroup = findViewById(R.id.check_group);
        initSeekBarListener();

    }

    private float mLastProgress;

    private void initSeekBarListener() {
        mLastProgress = mSeekBar.getProgress();
        final Rangable rangable = mDetailedChartView;
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rangable.shiftRange((progress - mLastProgress) / 100);
                mLastProgress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Do nothing
            }
        });

    }

    private void setCheckGroup() {
        int count = 0;
        List<CurveModel> curves = mChartModel.getCurves();
        for (CurveModel curve : mChartModel.getCurves()) {
            String name = curve.getName();
            int color = curve.getColor();
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(name);
            setCheckBoxTint(checkBox, color);
            checkBox.setChecked(true);
            mCheckGroup.addView(checkBox);
            if (count < curves.size() - 1) {
                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(getResources().getDimension(R.dimen.divider_height))));
                divider.setBackgroundColor(Color.LTGRAY);
                mCheckGroup.addView(divider);
                count++;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setCheckBoxTint(CheckBox checkBox, int color) {
        Drawable buttonDrawable = checkBox.getButtonDrawable();
        if (buttonDrawable != null) {
            buttonDrawable.setTint(color);
            checkBox.setButtonDrawable(buttonDrawable);
        }
    }
}