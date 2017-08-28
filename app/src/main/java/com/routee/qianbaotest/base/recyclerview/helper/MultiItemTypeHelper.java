package com.routee.qianbaotest.base.recyclerview.helper;

/**
 * Created by Routee on 2017/8/28.
 * description: ${cusor}
 */

public interface MultiItemTypeHelper<T> {
    int getLayoutId(int viewType);

    int getItemViewType(int position, T t);
}
