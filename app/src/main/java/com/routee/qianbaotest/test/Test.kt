package com.routee.qianbaotest.test

import com.routee.qianbaotest.base.recyclerview.helper.MultiItemTypeHelper

/**
 * Created by Routee on 2017/8/29.
 * description: ${cusor}
 */

class Test<T> {
    internal var mMultiItemTypeHelper: MultiItemTypeHelper<T> = object : MultiItemTypeHelper<T> {
        override fun getLayoutId(viewType: Int): Int {
            return 0
        }

        override fun getItemViewType(position: Int, t: T): Int {
            return 0
        }
    }
}
