package com.routee.qianbaotest.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.exocr.exocr.MainActivity
import com.routee.qianbaotest.R

class ActivityA : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_a)
    }

    fun finish(view: View) {
        startActivity(Intent(this, MainActivity::class.java))
    }
}