package com.bashkirov.telegram.contest.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bashkirov.telegram.contest.R;


public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel mMainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewModel();
        initViews();
        initObservers();
        requestData();
    }

    private void initViewModel() {
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
    }

    private void initViews() {
        //TODO
    }

    private void initObservers() {
        mMainActivityViewModel.getChartsLiveData()
                .observe(this, chartModels -> {
                    if (chartModels == null) return;
                    Log.d("TEST_DATA", chartModels.size() + "");
                    //TODO
                });
    }

    private void requestData() {
        mMainActivityViewModel.requestData(this);
    }
}
