package com.routee.qianbaotest.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.routee.qianbaotest.R;
import com.routee.qianbaotest.widget.RouteePieChart;

import java.util.ArrayList;
import java.util.List;

public class RouteePieChartActivity extends AppCompatActivity {

    private RouteePieChart mRpc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routee_pie_chart);
        mRpc = (RouteePieChart) findViewById(R.id.rpc);
        List<RouteePieChart.Unit> list = new ArrayList<>();
        list.add(new RouteePieChart.Unit(10, Color.YELLOW));
        list.add(new RouteePieChart.Unit(20, Color.GRAY));
        list.add(new RouteePieChart.Unit(30, Color.RED));
        mRpc.setData(list);
    }

    public void addUnit(View view) {

    }
}