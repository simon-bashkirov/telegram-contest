package com.bashkirov.telegram.contest.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.ChartModel;

public class CompoundChartView extends LinearLayout {

    //Components
    private SimpleChartView mSimpleChartView;
    private DetailedChartView mDetailedChartView;
    private SeekBar mSeekBar;


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
        mSimpleChartView.loadChart(chartModel);
        mDetailedChartView.loadChart(chartModel);
    }

    //////////////////////////////////////////////////
    private void initViews() {
        mSimpleChartView = findViewById(R.id.simple_chart_view);
        mDetailedChartView = findViewById(R.id.rangable_chart_view);
        mSeekBar = findViewById(R.id.seekBar);
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
}
