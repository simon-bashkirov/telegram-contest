package com.bashkirov.telegram.contest;

import android.graphics.Color;

import com.bashkirov.telegram.contest.models.ChartModel;
import com.bashkirov.telegram.contest.models.CurveModel;
import com.bashkirov.telegram.contest.models.PointModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class DataParser {

    public static List<ChartModel> parseCharListJsonString(String string) throws JSONException {
        JSONArray data = new JSONArray(string);
        return parseChartList(data);
    }

    public static List<ChartModel> parseChartList(JSONArray chartJsonArray) throws JSONException {
        List<ChartModel> charts = new ArrayList<>();
        for (int i = 0; i < chartJsonArray.length(); i++) {
            ChartModel chart = parseChart(chartJsonArray.getJSONObject(i));
            charts.add(chart);
        }
        return charts;
    }

    public static ChartModel parseChart(JSONObject chartJson) throws JSONException {
        JSONArray columns = chartJson.getJSONArray("columns");
        //Parse x column
        List<Long> xList = new LinkedList<>();
        JSONArray xColumn = columns.getJSONArray(0);
        for (int j = 1; j < xColumn.length(); j++) {
            xList.add(xColumn.getLong(j));
        }
        //Parse y columns
        Map<String, List<PointModel>> yColumnMap = new HashMap<>();
        for (int j = 1; j < columns.length(); j++) {
            JSONArray yColumn = columns.getJSONArray(j);
            List<Integer> yList = new LinkedList<>();

            for (int k = 1; k < xColumn.length(); k++) {
                yList.add(yColumn.getInt(k));
            }
            if (yList.size() == xList.size()) {
                //Create points
                List<PointModel> points = new LinkedList<>();
                for (int k = 0; k < xList.size(); k++) {
                    points.add(new PointModel(xList.get(k), yList.get(k)));
                }
                yColumnMap.put(yColumn.getString(0), points);
            }
        }
        //Parse properties
        JSONObject names = chartJson.getJSONObject("names");
        JSONObject colors = chartJson.getJSONObject("colors");
        JSONObject types = chartJson.getJSONObject("types");

        //Create curves
        List<CurveModel> curves = new LinkedList<>();
        for (Map.Entry<String, List<PointModel>> entry : yColumnMap.entrySet()) {
            String key = entry.getKey();
            List<PointModel> points = entry.getValue();
            String name = names.getString(key);
            int color = -1;
            try {
                color = Color.parseColor(colors.getString(key));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String type = types.getString(key);
            if (name != null && type != null && color != -1) {
                CurveModel curveModel = new CurveModel(points, color, name, type);
                curves.add(curveModel);
            }
        }
        return new ChartModel(curves);
    }
}
