package com.wangyang.diyviewlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.wangyang.divviewlibrary.view.magnifier.MagnifierView;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final MagnifierView magnifierView = (MagnifierView) findViewById(R.id.MagnifierView);
        final ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                magnifierView.setImageRes(R.drawable.xyjy);
               // magnifierView.setView(v);
               // magnifierView.setBitmap();
            }
        });
    }
}
