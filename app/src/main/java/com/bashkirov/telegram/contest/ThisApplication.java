package com.bashkirov.telegram.contest;

import android.app.Application;

public class ThisApplication extends Application {
    
    private boolean mIsNight = false;

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
}
