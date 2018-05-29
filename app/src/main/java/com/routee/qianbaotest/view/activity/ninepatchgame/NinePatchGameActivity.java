package com.routee.qianbaotest.view.activity.ninepatchgame;

import android.view.View;
import android.widget.Button;

import com.routee.qianbaotest.R;
import com.routee.qianbaotest.base.BaseActivity;

public class NinePatchGameActivity extends BaseActivity implements View.OnClickListener {

    private Button mBtSelectPic;
    private Button mBtTakePic;

    @Override
    public int rootView() {
        return R.layout.activity_nine_patch_game;
    }

    @Override
    public void initView() {
        mBtSelectPic = (Button) findViewById(R.id.bt_select_pic);
        mBtSelectPic.setOnClickListener(this);
        mBtTakePic = (Button) findViewById(R.id.bt_take_pic);
        mBtTakePic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_select_pic:
                preSelectPic();
                break;
            case R.id.bt_take_pic:
                break;
            default:
                break;
        }
    }

    private void preSelectPic() {
    }
}
