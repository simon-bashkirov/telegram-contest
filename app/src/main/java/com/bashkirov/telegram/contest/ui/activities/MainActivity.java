package com.bashkirov.telegram.contest.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.ui.views.CompoundChartView;
import com.bashkirov.telegram.contest.utils.DataParser;
import com.bashkirov.telegram.contest.utils.FileReader;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    //Constants
    private final String TEST_DATA_FILE_NAME = "chart_data.json";

    //Data
    private Thread mLoader;
    private List<ChartModel> mCharts = new ArrayList<>();

    //Views
    private CompoundChartView mCompoundChartView;
    private Spinner mSpinner;

    //Adapter
    private ArrayAdapter<String> mSpinnerAdapter;
    private List<String> mChartNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initAdapter();
        loadData();
    }

    @Override
    protected void onDestroy() {
        mLoader.interrupt();
        super.onDestroy();
    }

    private void initViews() {
        mCompoundChartView = findViewById(R.id.compound_chart_view);
        mSpinner = findViewById(R.id.spinner);
    }

    private void initAdapter() {
        mSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mChartNames);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
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
            mCharts.clear();
            mCharts.addAll(charts);
            mChartNames.clear();
            for (int i = 0; i < charts.size(); i++) {
                mChartNames.add("Chart " + i);
            }
            mSpinnerAdapter.notifyDataSetChanged();
        });
    }

    //===================== OnItemSelectedListener ==========================
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mCharts.size() > position) {
            mCompoundChartView.clear();
            mCompoundChartView.loadChart(mCharts.get(position));
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        //Do nothing
    }
}

//TODO
/*
1. Dynamic width for selected view
2. Night theme
 */


