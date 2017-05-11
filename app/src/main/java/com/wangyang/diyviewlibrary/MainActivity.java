package com.wangyang.diyviewlibrary;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wangyang.divviewlibrary.view.bezierlayout.LiveBezierLayout;
import com.wangyang.divviewlibrary.view.neonlights.NeonLightsTextView;
import com.wangyang.divviewlibrary.view.pathanim.AnimationManager;
import com.wangyang.divviewlibrary.view.pathanim.AnimationPath;
import com.wangyang.divviewlibrary.view.progress.CircleProgressView;

public class MainActivity extends AppCompatActivity {

    private int progress;
    private LiveBezierLayout liveBezierLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liveBezierLayout = (LiveBezierLayout) findViewById(R.id.lbl);
        liveBezierLayout.init(R.drawable.love,R.drawable.love2,R.drawable.love3);
         findViewById(R.id.neno).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NeonLightsTextView) v).setTextColor(Color.BLACK);
            }
        });
        final CircleProgressView circleProgressView = (CircleProgressView) findViewById(R.id.progress);
        circleProgressView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                progress = 0;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true){
                            circleProgressView.setProgress(progress++);
                            try {
                                Thread.sleep(16);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();

            }
        });
    }
    public void button(View view){
        liveBezierLayout.addView();
    }
   public void imageClick(View view){
       AnimationPath build = new AnimationPath.Builder().moveTo(0,0).cubicTo(0, 150,150, 0,300, 300).lineTo(-100,-300).quadTo(400,200,200,500).build();
       AnimationManager animationManager=new AnimationManager(view);
       animationManager.startAnimation(build,3000);

   }
}
