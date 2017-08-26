package com.routee.qianbaotest.base


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

abstract class BaseFragment : Fragment() {
    lateinit var mContext: Context

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContext = activity
        val view = inflater!!.inflate(layoutId, null)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
    }

    abstract val layoutId: Int
}
