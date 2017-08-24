package com.routee.qianbaotest.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

public abstract class BaseFragment extends Fragment {
    public Context  mContext;
    private Unbinder mUnbinder;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(getLayoutId(), null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    public abstract void initView();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    public abstract int getLayoutId();
}
