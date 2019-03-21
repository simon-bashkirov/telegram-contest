package com.bashkirov.telegram.contest;

import android.app.Application;

public class ThisApplication extends Application {

    private static final String KEY_MODE = "MODE";

    private boolean mIsNight = false;

    private static final ThisApplication ourInstance = new ThisApplication();

    public static ThisApplication getInstance() {
        return ourInstance;
    }

    public ThisApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public boolean isNight() {
        return mIsNight;
    }

    public void toggleNight() {
        this.mIsNight = !this.mIsNight;
    }
}
