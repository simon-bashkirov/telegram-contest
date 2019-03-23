package com.bashkirov.telegram.contest;

import android.app.Application;

import com.bashkirov.telegram.contest.models.State;

/**
 * Provides access to global state
 */
public class ThisApplication extends Application {

    private boolean mIsNight = false;
    private State mState;

    private static final ThisApplication ourInstance = new ThisApplication();

    public static ThisApplication getInstance() {
        return ourInstance;
    }

    @SuppressWarnings("WeakerAccess")
    public ThisApplication() {
    }

    public boolean isNight() {
        return mIsNight;
    }

    /**
     * Switches day and night nodes
     */
    public void toggleNight() {
        this.mIsNight = !this.mIsNight;
    }

    public State getState() {
        return mState;
    }

    public void setState(State mState) {
        this.mState = mState;
    }
}