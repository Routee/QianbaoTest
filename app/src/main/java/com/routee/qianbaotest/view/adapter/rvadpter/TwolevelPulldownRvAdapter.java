package com.routee.qianbaotest.view.adapter.rvadpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Routee on 2017/11/21.
 */

public class TwolevelPulldownRvAdapter extends RecyclerView.Adapter {
    private final Context      mContext;
    private       List<String> mList;

    public TwolevelPulldownRvAdapter(Context context, List<String> list) {
        mContext = context;
        mList = new ArrayList();
        mList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, null, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH hold = (VH) holder;
        hold.mTvText.setText(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class VH extends RecyclerView.ViewHolder {

        private final TextView mTvText;

        public VH(View itemView) {
            super(itemView);
            mTvText = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
