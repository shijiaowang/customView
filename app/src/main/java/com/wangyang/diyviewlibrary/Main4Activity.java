package com.wangyang.diyviewlibrary;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wangyang.divviewlibrary.view.waveview.WaveView;

public class Main4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        final WaveView waveView = (WaveView) findViewById(R.id.wave_view);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,100);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                waveView.setProgress(animatedValue);
            }
        });
        valueAnimator.start();
    }
}
