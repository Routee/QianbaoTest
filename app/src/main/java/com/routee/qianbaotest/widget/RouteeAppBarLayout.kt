package com.routee.qianbaotest.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView

import com.routee.qianbaotest.R


/**
 * Created by Routee on 2017/8/12.
 * description: ${cusor}
 */

class RouteeAppBarLayout : AppBarLayout {

    private var mIvBg: ImageView? = null
    private var mTb: Toolbar? = null

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_collapsing_toolbar, this)
        mIvBg = view.findViewById(R.id.iv_bg) as ImageView
        mTb = view.findViewById(R.id.toolbar) as Toolbar
        if (attrs != null) {
            val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RouteeAppBarLayout)
            val drawable = typedArray.getDrawable(R.styleable.RouteeAppBarLayout_image_bg)
            val b = typedArray.getBoolean(R.styleable.RouteeAppBarLayout_fitsSystemWindows, true)
            fitsSystemWindows = b
            //            int height = typedArray.getInteger(R.styleable.RouteeAppBarLayout_height, 0);
            //            AppBarLayout.LayoutParams lp = new AppBarLayout.LayoutParams(context, null);
            //            lp.height = height;
            //            lp.width = LayoutParams.MATCH_PARENT;
            //            setLayoutParams(lp);
            mIvBg!!.background = drawable
        }
    }
}
