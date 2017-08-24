package com.routee.qianbaotest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.routee.qianbaotest.R;


/**
 * Created by Routee on 2017/8/12.
 * description: ${cusor}
 */

public class RouteeAppBarLayout extends AppBarLayout {

    private ImageView mIvBg;
    private Toolbar mTb;

    public RouteeAppBarLayout(Context context) {
        super(context);
        initView(context, null);
    }

    public RouteeAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_collapsing_toolbar, this);
        mIvBg = (ImageView) view.findViewById(R.id.iv_bg);
        mTb = (Toolbar) view.findViewById(R.id.toolbar);
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RouteeAppBarLayout);
            Drawable drawable = typedArray.getDrawable(R.styleable.RouteeAppBarLayout_image_bg);
            Boolean b = typedArray.getBoolean(R.styleable.RouteeAppBarLayout_fitsSystemWindows, true);
            setFitsSystemWindows(b);
//            int height = typedArray.getInteger(R.styleable.RouteeAppBarLayout_height, 0);
//            AppBarLayout.LayoutParams lp = new AppBarLayout.LayoutParams(context, null);
//            lp.height = height;
//            lp.width = LayoutParams.MATCH_PARENT;
//            setLayoutParams(lp);
            mIvBg.setBackground(drawable);
        }
    }
}
