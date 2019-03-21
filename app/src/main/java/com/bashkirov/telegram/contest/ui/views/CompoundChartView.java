package com.bashkirov.telegram.contest.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.CurveModel;

import java.util.List;

public class CompoundChartView extends LinearLayout {

    private ChartModel mChartModel;

    //Views
    private BaseChartView mBaseChartView;
    private DetailedChartView mDetailedChartView;
    private CustomSeekBar mSeekBar;
    private LinearLayout mCheckGroup;

    //============= Constructors ================
    public CompoundChartView(Context context) {
        this(context, null);
    }

    public CompoundChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.view_chart_compound, this);
        setOrientation(VERTICAL);
        initViews();
        initSeekBarListener();
    }

    //========= Public methods ===================
    public void loadChart(ChartModel chartModel) {
        mChartModel = chartModel;
        mBaseChartView.loadChart(chartModel);
        mDetailedChartView.loadChart(chartModel);
        setCheckButtonsGroup();
    }

    public void clear() {
        mBaseChartView.clear();
        mDetailedChartView.clear();

    }

    //////////////////////////////////////////////////

    private void initViews() {
        mBaseChartView = findViewById(R.id.base_chart_view);
        mDetailedChartView = findViewById(R.id.detailed_chart_view);
        mSeekBar = findViewById(R.id.seekBar);
        mCheckGroup = findViewById(R.id.check_group);
    }

    private void initSeekBarListener() {
        mSeekBar.setListener(mDetailedChartView);
    }

    /**
     * Creates required number of check buttons and fills the mCheckGroup
     */
    private void setCheckButtonsGroup() {
        mCheckGroup.removeAllViews();
        int count = 0;
        List<CurveModel> curves = mChartModel.getCurves();
        for (CurveModel curve : mChartModel.getCurves()) {
            String name = curve.getName();
            int color = curve.getColor();
            final CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(name);
            setCheckBoxTint(checkBox, color);
            checkBox.setChecked(true);
            mCheckGroup.addView(checkBox);
            if (count < curves.size() - 1) {
                View divider = new View(getContext());
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Math.round(getResources().getDimension(R.dimen.divider_thickness))));
                divider.setBackgroundColor(getResources().getColor(R.color.colorDividerGrayDay));
                mCheckGroup.addView(divider);
                count++;
            }
            checkBox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> {
                        if (buttonView == checkBox) {
                            mDetailedChartView.setCurveVisible(curve, isChecked);
                            mBaseChartView.setCurveVisible(curve, isChecked);
                        }
                    }
            );
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
