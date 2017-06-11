package com.wangyang.divviewlibrary.view.table;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    TableView tableView;
    Handler handler=new Handler();
    int row=10;
    int comlun=20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tableView= (TableView) findViewById(R.id.table);
        tableView.setAdapter(new MyAdapter(MainActivity.this));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tableView.setAdapter(new MyAdapter(MainActivity.this));
            }
        },1500);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                row=10000;
                comlun=10000;
                Log.i("tuch","加载10000");
                tableView.setAdapter(new MyAdapter(MainActivity.this));
            }
        },8000);

    }


    class MyAdapter implements  BaseTableAdapter{
        LayoutInflater inflater;
        private int width;
        private  int height;
        public MyAdapter(Context context) {
            Resources resources = context.getResources();
            width = resources.getDimensionPixelSize(R.dimen.table_width);
            height = resources.getDimensionPixelSize(R.dimen.table_height);
            inflater=LayoutInflater.from(context);
        }
        @Override
        public int getRowCount() {
            //13
            return  row;
        }

        @Override
        public int getColmunCount() {
            return comlun;
        }
        private int getLayout(int row, int column) {
            final int layoutResource;
            switch (getItemViewType(row, column)) {
                case 0:
                    layoutResource = R.layout.item_table1_header;
                    break;
                case 1:
                    layoutResource = R.layout.item_table1;
                    break;
                default:
                    throw new RuntimeException("wtf?");
            }
            return layoutResource;
        }
        @Override
        public View getView(int row, int column, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView= inflater.inflate(getLayout(row,column),parent,false);
            }
            //
            TextView textView= (TextView) convertView.findViewById(R.id.text1);
            textView.setText("第 "+row+"行"+"  列 "+column);
            return convertView;
        }

        @Override
        public int getWidth(int column) {


            return width;
        }

        @Override
        public int getHeight(int row) {
            return height;
        }

        @Override
        public int getItemViewType(int row, int column) {
            if (row < 0) {
                return 0;
            } else {
                return 1;
            }
        }
        @Override
        public int getViewTypeCount() {
            return 2;
        }
    }
    class obj implements BaseTableAdapter{

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @NotNull
        @Override
        public View getView(int row, int comun, @NotNull View convertView, @NotNull ViewGroup parent) {
            return null;
        }

        @Override
        public int getWidth(int column) {
            return 0;
        }

        @Override
        public int getHeight(int row) {
            return 0;
        }

        @Override
        public int getItemViewType(int row, int column) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }
    }

}
