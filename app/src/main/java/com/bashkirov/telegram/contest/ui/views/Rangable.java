package com.bashkirov.telegram.contest.ui.views;

public interface Rangable {

    /**
     * Sets range limits for data
     *
     * @param start starting percent of data raw
     * @param end   ending percent of data raw
     */
    void setRange(float start, float end);

    /**
     * Shifts range to given percent
     *
     * @param shift - shift in percents from data amount
     */
    void shiftRange(float shift);

    /**
     * Extension range to given percent
     *
     * @param percent   - extension in percents from data amount
     * @param direction - direction of extension
     */
    void extendRange(float percent, Direction direction);

    enum Direction {
        LEFT,
        RIGHT
    }
}
