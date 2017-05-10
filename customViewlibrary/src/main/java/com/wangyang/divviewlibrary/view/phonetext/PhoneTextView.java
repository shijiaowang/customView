package com.wangyang.divviewlibrary.view.phonetext;

/**
 * Created by wangyang on 2017/5/4.
 * email:1440214507@qq.com
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


/**
 * Created by wangyang on 2016/8/17 0017.
 * 隐藏中间位数的显示
 */
@SuppressLint("AppCompatCustomView")
public class PhoneTextView extends TextView {

    private String phoneNumber;
    //默认显示前面和后面多少位
    private int defaultStart=3;
    private int defaultEnd=9;
    private int defaultPhoneNumberLength = 11;

    public int getDefaultPhoneNumberLength() {
        return defaultPhoneNumberLength;
    }

    public void setDefaultPhoneNumberLength(int defaultPhoneNumberLength) {
        this.defaultPhoneNumberLength = defaultPhoneNumberLength;
    }

    public PhoneTextView(Context context) {
        this(context, null);
    }

    public PhoneTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhoneTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        phoneNumber = getText().toString().trim();
        hideCenterPhoneNumber();
    }

    /**
     *
     * @param start 从第几个开始变为*
     * @param end 到第几个恢复正常
     * @throws Exception
     */
    private void init(int start,int end) throws Exception {
        if (start<0 || start>defaultPhoneNumberLength){
            throw new Exception("start can,t >"+defaultPhoneNumberLength);
        }
        if (start>end){
            throw new Exception("start can,t > end");
        }
        if (end>defaultPhoneNumberLength){
            throw new Exception("end can,t > "+defaultPhoneNumberLength);
        }
        this.defaultEnd=end;
        this.defaultStart=start;

        init();
    }

    private void hideCenterPhoneNumber() {
        if (phoneNumber.length() == defaultPhoneNumberLength) {
            String start = phoneNumber.substring(0, defaultStart);
            String end = phoneNumber.substring(defaultEnd, phoneNumber.length());
            StringBuilder stringBuilder=new StringBuilder(start);
            for (int i =defaultStart;i<defaultEnd;i++){
                stringBuilder.append("*");
            }
            stringBuilder.append(end);
            phoneNumber = stringBuilder.toString();
            setText(phoneNumber);
        }else {
            Log.e("PhoneTextView","phone number length is error,it must be "+defaultPhoneNumberLength);
        }
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber==null)return;
        this.phoneNumber = phoneNumber;
        hideCenterPhoneNumber();
    }
}