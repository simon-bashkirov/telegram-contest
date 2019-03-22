package com.bashkirov.telegram.contest;

import android.app.Application;

import com.bashkirov.telegram.contest.models.State;

public class ThisApplication extends Application {

    private boolean mIsNight = false;
    private State mState;

    private static final ThisApplication ourInstance = new ThisApplication();

    public static ThisApplication getInstance() {
        return ourInstance;
    }

    public ThisApplication() {
    }

    public boolean isNight() {
        return mIsNight;
    }

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
