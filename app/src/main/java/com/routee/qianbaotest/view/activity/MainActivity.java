package com.routee.qianbaotest.view.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routee.qianbaotest.R;
import com.routee.qianbaotest.base.BaseActivity;
import com.routee.qianbaotest.widget.RouteeAppBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    @BindView(R.id.r_Tb)
    RouteeAppBarLayout mRTb;
    @BindView(R.id.rv_main)
    RecyclerView       mRvMain;
    private List<String> mList;
    private MAdapter     mMAdapter;
    private RecyclerView mRv;

    @Override
    public int getRootView() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mList.add("条目" + i);
        }
//        mRv = (RecyclerView) findViewById(R.id.rv_main);
        mRvMain.setLayoutManager(new LinearLayoutManager(this));
//        mRv.setLayoutManager(new LinearLayoutManager(this));
        mMAdapter = new MAdapter(this, mList);
        mRvMain.setAdapter(mMAdapter);
//        mRv.setAdapter(mMAdapter);
//        mMAdapter.notifyDataSetChanged();
    }

    class MAdapter extends RecyclerView.Adapter {
        List<String> mmList;
        Context      mContext;

        public MAdapter(Context context, List<String> list) {
            mContext = context;
            mmList = list;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vH = (VH) holder;
            vH.mTv.setText(mmList.get(position));
        }

        @Override
        public int getItemCount() {
            return mmList.size();
        }

        class VH extends RecyclerView.ViewHolder {

            public TextView mTv;

            public VH(View itemView) {
                super(itemView);
                mTv = (TextView) itemView.findViewById(R.id.tv_text1);
            }
        }
    }
}


