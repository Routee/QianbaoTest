package com.routee.qianbaotest.base.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.routee.qianbaotest.base.recyclerview.helper.MultiItemTypeHelper;
import com.routee.qianbaotest.base.recyclerview.helper.SectionHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Routee on 2017/8/28.
 * description: ${cusor}
 */

public abstract class SectionRvAdapter<T> extends MultiItemViewRvAdapter<T> {
    private static final int TYPE_SECTION = 0;
    private MultiItemTypeHelper<T>         mMultiItemTypeHelper;
    private SectionHelper                  mSectionHelper;
    private LinkedHashMap<String, Integer> mSections;
    MultiItemTypeHelper<T>           headerItemTypeHelper = new MultiItemTypeHelper<T>() {
        @Override
        public int getLayoutId(int viewType) {
            if (viewType == TYPE_SECTION)
                return mSectionHelper.sectionHeaderLayoutId();
            else
                return mMultiItemTypeHelper.getLayoutId(viewType);
        }

        @Override
        public int getItemViewType(int position, T o) {
            int positionVal = getIndexForPosition(position);
            return mSections.values().contains(position)
                    ? TYPE_SECTION : mMultiItemTypeHelper.getItemViewType(positionVal, o);
        }
    };
    RecyclerView.AdapterDataObserver observer             = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            findSections();
        }
    };

    public SectionRvAdapter(Context context, int layoutId, List datas, SectionHelper sectionHelper) {
        super(context, datas, null);
        mLayoutId = layoutId;
        mMultiItemTypeHelper = headerItemTypeHelper;
        mSectionHelper = sectionHelper;
        mSections = new LinkedHashMap<>();
        findSections();
        registerAdapterDataObserver(observer);
    }

    public void findSections() {
        int n = mDatas.size();
        int nSections = 0;
        mSections.clear();
        for (int i = 0; i < n; i++) {
            String sectionName = mSectionHelper.getTitle(mDatas.get(i));
            if (!mSections.containsKey(sectionName)) {
                mSections.put(sectionName, i + nSections);
                nSections++;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMultiItemTypeHelper.getItemViewType(position, null);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + mSections.size();
    }

    @Override
    public void onBindViewHolder(BaseRvViewHolder holder, int position) {
        position = getIndexForPosition(position);
        if (holder.getItemViewType() == TYPE_SECTION) {
            holder.setText(mSectionHelper.sectionTitleTextViewId()
                    , mSectionHelper.getTitle(mDatas.get(position)));
            return;
        }
        super.onBindViewHolder(holder, position);
    }

    public int getIndexForPosition(int position) {
        int nSections = 0;
        Set<Map.Entry<String, Integer>> entrySet = mSections.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet) {
            if (entry.getValue() < position) {
                nSections++;
            }
        }
        return position - nSections;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        unregisterAdapterDataObserver(observer);
    }
}
