package com.bashkirov.telegram.contest.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.bashkirov.telegram.contest.DataParser;
import com.bashkirov.telegram.contest.FileReader;
import com.bashkirov.telegram.contest.models.ChartModel;

import org.json.JSONException;

import java.util.List;

class MainActivityViewModel extends ViewModel {

    private final String TEST_DATA_FILE_NAME = "chart_data.json";

    private final MutableLiveData<List<ChartModel>> chartsLiveData = new MutableLiveData<>();

    private Thread mLoader;

    void requestData(final Context context) {
        //This simple approach is applied for the sake of low app size and low min API support.
        //Otherwise CompletableFuture (API level 24+) or RxJava (increasing app size by ~700KB) should be used
        mLoader = new Thread(() -> {
            try {
                String data = FileReader.readStringFromAsset(context, TEST_DATA_FILE_NAME);
                List<ChartModel> charts = DataParser.parseCharListJsonString(data);
                chartsLiveData.postValue(charts);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        });
        mLoader.start();
    }

    MutableLiveData<List<ChartModel>> getChartsLiveData() {
        return chartsLiveData;
    }

    @Override
    protected void onCleared() {
        mLoader.interrupt();
        super.onCleared();
    }
}
