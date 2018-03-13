package com.routee.qianbaotest.view.activity;

import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.routee.qianbaotest.R;

/**
 * @author: Routee
 * @date 2018/3/9
 * @mail wangc4@qianbaocard.com
 * ------------1.本类由Routee开发,阅读、修改时请勿随意修改代码排版格式后提交到git。
 * ------------2.阅读本类时，发现不合理请及时指正.
 * ------------3.如需在本类内部进行修改,请先联系Routee,若未经同意修改此类后造成损失本人概不负责。
 */
public class TestActivity extends AppCompatActivity {

    private ImageView mIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mIv = (ImageView) findViewById(R.id.iv);
    }

    public void start(View view) {
        TransitionDrawable d = (TransitionDrawable) mIv.getBackground();
        d.startTransition(2000);
    }
}
