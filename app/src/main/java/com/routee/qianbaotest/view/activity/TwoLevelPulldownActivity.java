package com.routee.qianbaotest.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.routee.qianbaotest.R;
import com.routee.qianbaotest.base.BaseActivity;
import com.routee.qianbaotest.view.adapter.rvadpter.TwolevelPulldownRvAdapter;

import java.util.ArrayList;
import java.util.List;

public class TwoLevelPulldownActivity extends BaseActivity {

    private RecyclerView              mRv;
    private List                      mList;
    private TwolevelPulldownRvAdapter mAdapter;

    @Override
    public int rootView() {
        return R.layout.activity_two_level_pulldown;
    }

    @Override
    public void initView() {
        mRv = (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList();
        for (int i = 0; i < 25; i++) {
            mList.add(" i = " + i);
        }
        mAdapter = new TwolevelPulldownRvAdapter(this, mList);
        mRv.setAdapter(mAdapter);
        mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });
    }
}
