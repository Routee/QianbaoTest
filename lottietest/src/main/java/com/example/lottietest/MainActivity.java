package com.example.lottietest;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    private SeekBar             mSeekBar;
    private Button              mBt;
    private LottieAnimationView mLav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mSeekBar = findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = i / 100.f;
                mLav.setProgress(f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBt = findViewById(R.id.bt);
        mLav = findViewById(R.id.lav);
        mLav.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float value = (Float) valueAnimator.getAnimatedValue();
                android.util.Log.e("xxxxxx", "value = " + value);
                mSeekBar.setProgress((int) (value * 100));
            }
        });
    }

    public void start(View view) {
        if (mLav.isAnimating()) {
            mLav.pauseAnimation();
        } else {
            mLav.playAnimation();
        }
    }
}
