package com.routee.qianbaotest.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder mButterKnife;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRootView());
        mButterKnife = ButterKnife.bind(this);
        initView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public abstract int getRootView();

    public abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mButterKnife.unbind();
    }
}
