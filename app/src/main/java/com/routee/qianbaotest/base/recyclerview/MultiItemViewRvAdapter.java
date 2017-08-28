package com.routee.qianbaotest.base.recyclerview;

import android.content.Context;

import com.routee.qianbaotest.base.recyclerview.helper.MultiItemTypeHelper;

import java.util.List;

/**
 * Created by Routee on 2017/8/28.
 * description: ${cusor}
 */

public abstract class MultiItemViewRvAdapter<T> extends BaseRecyclerViewAdapter {
    private MultiItemTypeHelper mMultiItemTypeHelper;

    public MultiItemViewRvAdapter
            (Context context, List<T> datas, MultiItemTypeHelper multiItemTypeHelper) {
        super(context, -1, datas);
        mMultiItemTypeHelper = multiItemTypeHelper;
    }

    public int getItemViewType(int position) {
        return mMultiItemTypeHelper.getItemViewType(position, mDatas.get(position));
    }

    public int getItemCount() {
        return mDatas.size();
    }

    public boolean isEnable() {
        return true;
    }
}
