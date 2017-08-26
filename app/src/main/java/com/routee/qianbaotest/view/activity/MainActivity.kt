package com.routee.qianbaotest.view.activity

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.routee.qianbaotest.R
import com.routee.qianbaotest.base.BaseActivity
import com.routee.qianbaotest.widget.RouteeAppBarLayout


class MainActivity : BaseActivity() {
    internal var mRTb: RouteeAppBarLayout? = null
    internal var mRvMain: RecyclerView? = null
    private var mList: MutableList<String>? = null
    private var mMAdapter: MAdapter? = null
    override fun rootView(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        mRTb = findViewById(R.id.r_Tb) as RouteeAppBarLayout?
        mRvMain = findViewById(R.id.rv_main) as RecyclerView
        mList = ArrayList()
        for (i in 0..49) {
            mList!!.add("条目" + i)
        }
        mRvMain!!.layoutManager = LinearLayoutManager(this)
        mMAdapter = MAdapter(this, mList as ArrayList<String>)
        mRvMain!!.adapter = mMAdapter
    }

    internal inner class MAdapter(var mContext: Context, var mmList: List<String>) : RecyclerView.Adapter<MAdapter.VH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(mContext).inflate(R.layout.simple_list_item, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH?, position: Int) {
            val vH = holder as VH
            vH.mTv.text = mmList[position]
        }

        override fun getItemCount(): Int {
            return mmList.size
        }

        internal inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var mTv: TextView

            init {
                mTv = itemView.findViewById(R.id.tv_text1) as TextView
            }
        }
    }
}


