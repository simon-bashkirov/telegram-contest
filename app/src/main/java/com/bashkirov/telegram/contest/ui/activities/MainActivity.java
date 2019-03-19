package com.bashkirov.telegram.contest.ui.activities;

import android.app.Activity;
import android.os.Bundle;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.ui.views.CompoundChartView;
import com.bashkirov.telegram.contest.utils.DataParser;
import com.bashkirov.telegram.contest.utils.FileReader;

import org.json.JSONException;

import java.util.List;


public class MainActivity extends Activity {

    private Thread mLoader;
    private final String TEST_DATA_FILE_NAME = "chart_data.json";

    private CompoundChartView mCompoundChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadData();
    }

    @Override
    protected void onDestroy() {
        mLoader.interrupt();
        super.onDestroy();
    }


    private void initViews() {
        mCompoundChartView = findViewById(R.id.compound_chart_view);
    }

    private void loadData() {
        mLoader = new Thread(() -> {
            try {
                String data = FileReader.readStringFromAsset(this, TEST_DATA_FILE_NAME);
                List<ChartModel> charts = DataParser.parseChartListJsonString(data);
                postDataInUIThread(charts);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        });
        mLoader.start();
    }

    private void postDataInUIThread(List<ChartModel> charts) {
        mCompoundChartView.post(() -> {
            if (charts.isEmpty()) return;
            mCompoundChartView.loadChart(charts.get(0));
        });
    }


}

//TODO
/*
1. Pointer
2. Switcher
3. Night theme
 */


