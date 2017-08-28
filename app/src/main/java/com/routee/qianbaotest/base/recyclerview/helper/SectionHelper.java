package com.routee.qianbaotest.base.recyclerview.helper;

/**
 * Created by Routee on 2017/8/28.
 * description: ${cusor}
 */

public interface SectionHelper<T> {
    int sectionHeaderLayoutId();

    int sectionTitleTextViewId();

    String getTitle(T t);
}
