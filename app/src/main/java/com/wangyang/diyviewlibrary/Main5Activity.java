package com.wangyang.diyviewlibrary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wangyang.divviewlibrary.view.table.TableSlideView;

public class Main5Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main5);
        setContentView(new TableSlideView(this));
    }
}
