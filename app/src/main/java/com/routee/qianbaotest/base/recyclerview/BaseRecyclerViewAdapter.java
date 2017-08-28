package com.routee.qianbaotest.base.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Routee on 2017/8/28.
 * description: ${cusor}
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRvViewHolder> {
    public Context mContext;
    public List<T> mDatas;
    public int     mLayoutId;

    public BaseRecyclerViewAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public BaseRvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRvViewHolder holder = BaseRvViewHolder.createViewHolder(mContext, parent, mLayoutId);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRvViewHolder holder, int position) {
//        holder.updatePosition(position);
        convert(holder, mDatas.get(position));
    }

    public abstract void convert(BaseRvViewHolder holder, T t);
}
