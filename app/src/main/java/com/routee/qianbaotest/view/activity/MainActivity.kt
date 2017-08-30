package com.routee.qianbaotest.view.activity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.routee.qianbaotest.R
import com.routee.qianbaotest.base.BaseActivity
import com.routee.qianbaotest.base.recyclerview.MultiItemViewRvAdapter
import com.routee.qianbaotest.base.recyclerview.helper.MultiItemTypeHelper
import com.routee.qianbaotest.model.bean.MerchantListItemBean
import com.routee.qianbaotest.widget.RouteeAppBarLayout

class MainActivity : BaseActivity() {
    internal var mRTb: RouteeAppBarLayout? = null
    internal var mRvMain: RecyclerView? = null
    private var mList: MutableList<MerchantListItemBean.MerchantListBean>? = null
    private var mMAdapter: MultiItemViewRvAdapter<MerchantListItemBean.MerchantListBean>? = null
    override fun rootView(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        mRTb = findViewById(R.id.r_Tb) as RouteeAppBarLayout?
        mRvMain = findViewById(R.id.rv_main) as RecyclerView
        mList = ArrayList()
        mRvMain!!.layoutManager = LinearLayoutManager(this)
        var mMultiItemTypeHelper: MultiItemTypeHelper<MerchantListItemBean.MerchantListBean>
                = object : MultiItemTypeHelper<MerchantListItemBean.MerchantListBean> {
            override fun getLayoutId(viewType: Int): Int {
                return 0
            }

            override fun getItemViewType(position: Int, t: MerchantListItemBean.MerchantListBean): Int {
                return 0
            }
        }
        mMAdapter = MultiItemViewRvAdapter<MerchantListItemBean.MerchantListBean>(this, mList, mMultiItemTypeHelper)
        mRvMain!!.adapter = mMAdapter
    }
}


