package com.bashkirov.telegram.contest.ui.views;

/**
 * Implements abstraction for listening range changes
 */
interface RangeListener {

    /**
     * Notifies range changes
     *
     * @param start starting percent of data raw
     * @param end   ending percent of data raw
     */
    void onRangeChange(float start, float end);

}
