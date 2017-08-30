package com.routee.qianbaotest.base.recyclerview;

import android.content.Context;
import android.view.ViewGroup;

import com.routee.qianbaotest.base.recyclerview.helper.MultiItemTypeHelper;
import com.routee.qianbaotest.base.recyclerview.manager.ItemViewDelegateManager;

import java.util.List;

/**
 * Created by Routee on 2017/8/28.
 * description: ${cusor}
 */

public class MultiItemViewRvAdapter<T> extends BaseRvAdapter<T> {
    private MultiItemTypeHelper<T>     mMultiItemTypeHelper;
    private ItemViewDelegateManager mItemViewDelegateManager;

    public MultiItemViewRvAdapter
            (Context context, List<T> datas, MultiItemTypeHelper<T> multiItemTypeHelper) {
        super(context, -1, datas);
        mMultiItemTypeHelper = multiItemTypeHelper;
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeHelper.getItemViewType(position, mDatas.get(position));
    }

    @Override
    public BaseRvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mMultiItemTypeHelper.getLayoutId(viewType);
        BaseRvViewHolder holder = BaseRvViewHolder.createViewHolder(mContext, parent, layoutId);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public boolean isEnable() {
        return true;
    }

    @Override
    public void convert(BaseRvViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }
}
