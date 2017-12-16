package com.routee.qianbaotest.base.recyclerview.manager;

import com.routee.qianbaotest.base.recyclerview.BaseRvViewHolder;

/**
 * Created by Routee on 2017/8/29.
 * description: ${cusor}
 */

public interface ItemViewDelegate<T> {
    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(BaseRvViewHolder holder, T t, int position);
}
