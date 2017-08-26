package com.routee.qianbaotest.base

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

abstract class BaseActivity : AppCompatActivity() {


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView())
        initView()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    abstract fun rootView(): Int

    abstract fun initView()

    override fun onDestroy() {
        super.onDestroy()
    }
}
