package com.routee.qianbaotest.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.routee.qianbaotest.R;
import com.routee.qianbaotest.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

public class HomeFragement extends BaseFragment {

    @BindView(R.id.rv_home)
    RecyclerView mRvHome;
    Unbinder unbinder;
    private List<String> mList;

    @Override
    public int getLayoutId() {
        return R.layout.layout_fragment_home;
    }

    @Override
    public void initView() {
        mList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mList.add("条目" + i);
        }
        mRvHome.setLayoutManager(new LinearLayoutManager(mContext));
        mRvHome.setAdapter(new RecyclerView.Adapter() {
            @Override
            public VH onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, parent);
                return new VH(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                VH vH = (VH) holder;
                vH.mTv.setText(mList.get(position));
            }

            @Override
            public int getItemCount() {
                return mList.size();
            }

            class VH extends RecyclerView.ViewHolder {

                public TextView mTv;

                public VH(View itemView) {
                    super(itemView);
                    mTv = (TextView) itemView.findViewById(R.id.tv_text1);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
