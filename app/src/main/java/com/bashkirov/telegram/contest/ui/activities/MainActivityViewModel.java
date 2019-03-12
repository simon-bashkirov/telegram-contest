package com.bashkirov.telegram.contest.ui.activities;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.bashkirov.telegram.contest.DataParser;
import com.bashkirov.telegram.contest.FileReader;
import com.bashkirov.telegram.contest.models.ChartModel;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

class MainActivityViewModel extends ViewModel {

    private final String TEST_DATA_FILE_NAME = "chart_data.json";

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<ChartModel>> chartsLiveData = new MutableLiveData<>();

    void requestData(final Context context) {
        disposables.add(
                Single.fromCallable(() -> {
                    String data = FileReader.readStringFromAsset(context, TEST_DATA_FILE_NAME);
                    return DataParser.parseCharListJsonString(data);
                })
                .subscribeOn(Schedulers.newThread())
                .subscribeWith(new DisposableSingleObserver<List<ChartModel>>(){
                    @Override
                    public void onSuccess(List<ChartModel> chartModels) {
                        chartsLiveData.postValue(chartModels);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                }));


    }

    public MutableLiveData<List<ChartModel>> getChartsLiveData() {
        return chartsLiveData;
    }

    @Override
    protected void onCleared() {
        disposables.dispose();
        super.onCleared();
    }
}
