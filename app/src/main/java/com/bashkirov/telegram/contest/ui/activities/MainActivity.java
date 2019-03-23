package com.bashkirov.telegram.contest.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bashkirov.telegram.contest.R;
import com.bashkirov.telegram.contest.ThisApplication;
import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.State;
import com.bashkirov.telegram.contest.ui.views.CompoundChartView;
import com.bashkirov.telegram.contest.utils.DataParser;
import com.bashkirov.telegram.contest.utils.FileReader;
import com.bashkirov.telegram.contest.utils.ThemeUtils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    //Constants
    private final String TEST_DATA_FILE_NAME = "chart_data.json";

    //Data
    private Thread mLoader;
    private final List<ChartModel> mCharts = new ArrayList<>();

    //Views
    private CompoundChartView mCompoundChartView;
    private Spinner mSpinner;

    //Adapter
    private ArrayAdapter<String> mSpinnerAdapter;
    private final List<String> mChartNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ThisApplication.getInstance().isNight()) {
            setTheme(R.style.ActivityThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        State state = ThisApplication.getInstance().getState();
        if (state == null) {
            loadData();
        } else {
            setState(state);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ThisApplication.getInstance().setState(getState());
    }

    @Override
    protected void onDestroy() {
        if (mLoader != null) {
            mLoader.interrupt();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.switch_night_mode) {
            ThisApplication.getInstance().toggleNight();
            restartActivityWithoutAnimation();
            return true;
        }
        return false;
    }

    private void initViews() {
        mCompoundChartView = findViewById(R.id.compound_chart_view);
        mSpinner = findViewById(R.id.spinner);
    }

    private void initAdapter() {
        mSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mChartNames);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(this);
    }

    private void loadData() {
        mLoader = new Thread(() -> {
            try {
                String data = FileReader.readStringFromAsset(this, TEST_DATA_FILE_NAME);
                List<ChartModel> charts = DataParser.parseChartListJsonString(data);
                setDefaultState(charts);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        });
        mLoader.start();
    }

    private void setDefaultState(List<ChartModel> charts) {
        State defaultState = new State(
                charts,
                new HashSet<>(),
                0,
                //Not specified. Will be set by defaults
                null,
                null,
                //No selected point
                null
        );
        ThisApplication.getInstance().setState(defaultState);
        setState(defaultState);
    }

    private void setCharts(List<ChartModel> charts) {
        if (charts.isEmpty()) return;
        mCharts.clear();
        mCharts.addAll(charts);
        mChartNames.clear();
        for (int i = 0; i < charts.size(); i++) {
            mChartNames.add(getString(R.string.chart, i));
        }
        mSpinnerAdapter.notifyDataSetChanged();
    }

    private void restartActivityWithoutAnimation() {
        Intent intent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            overridePendingTransition(0, 0);
        }
        startActivity(intent);
    }

    private void setState(State state) {
        mCompoundChartView.post(() -> {
            setCharts(state.getCharts());
            int selectedChartIndex = state.getSelectedChartIndex();
            mCompoundChartView.loadChart(mCharts.get(selectedChartIndex));
            mCompoundChartView.setHiddenCurvesIndexes(state.getHiddenCurvesIndexes());
            mCompoundChartView.setPositions(state.getStartPosition(), state.getEndPosition());
            mCompoundChartView.setSelectedPointIndex(state.getSelectedPointIndex());
            mSpinner.setSelection(selectedChartIndex);
        });
    }

    private State getState() {
        return new State(mCharts,
                mCompoundChartView.getHiddenCurvesIndexes(),
                mSpinner.getSelectedItemPosition(),
                mCompoundChartView.getStartPosition(),
                mCompoundChartView.getEndPosition(),
                mCompoundChartView.getSelectedPointIndex());
    }

    //===================== OnItemSelectedListener ==========================
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view instanceof TextView) {
            TextView tw = (TextView) view;
            tw.setTextColor(ThemeUtils.getColorForAttrId(this, android.R.attr.textColor));
        }

        if (mCharts.size() > position) {
            ChartModel chart = mCharts.get(position);
            if (mCompoundChartView.getLoadedChart() != chart) {
                mCompoundChartView.loadChart(chart);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        //Do nothing
    }
}