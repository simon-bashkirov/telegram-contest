package com.bashkirov.telegram.contest.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides model for saving activity state
 */
public class State {

    private final List<ChartModel> mCharts = new ArrayList<>();
    private final int mSelectedChartIndex;
    private final Set<Integer> mHiddenCurvesIndexes = new HashSet<>();
    private final Float mStartPosition;
    private final Float mEndPosition;
    private final Integer mSelectedPointIndex;

    public State(List<ChartModel> charts, Set<Integer> hiddenCurvesIndexes, int selectedChartIndex, Float startPosition, Float endPosition, Integer selectedPointIndex) {
        this.mCharts.addAll(charts);
        this.mHiddenCurvesIndexes.addAll(hiddenCurvesIndexes);
        this.mSelectedChartIndex = selectedChartIndex;
        this.mStartPosition = startPosition;
        this.mEndPosition = endPosition;
        this.mSelectedPointIndex = selectedPointIndex;
    }

    public List<ChartModel> getCharts() {
        return mCharts;
    }

    public int getSelectedChartIndex() {
        return mSelectedChartIndex;
    }

    public Set<Integer> getHiddenCurvesIndexes() {
        return mHiddenCurvesIndexes;
    }

    public Float getStartPosition() {
        return mStartPosition;
    }

    public Float getEndPosition() {
        return mEndPosition;
    }

    public Integer getSelectedPointIndex() {
        return mSelectedPointIndex;
    }
}