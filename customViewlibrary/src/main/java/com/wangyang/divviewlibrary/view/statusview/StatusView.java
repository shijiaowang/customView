package com.wangyang.divviewlibrary.view.statusview;

/**
 * Created by wangyang on 2017/5/4.
 * email:1440214507@qq.com
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.wangyang.divviewlibrary.R;
import com.wangyang.divviewlibrary.utils.NetworkUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by wangyang on 2017/3/9.
 * 状态view 的管理
 */

public class StatusView extends FrameLayout implements View.OnClickListener, IStatusChange {
    private boolean isSuccessfully = false;
    private boolean isShowStatusAfter = false;//成功一次以后，之后是否显示其他状态，除成功之外
    public static final int STATE_LOAD_SUCCESS = 0;//加载成功
    public static final int STATE_LOAD_ERROR = 1;//加载错误
    public static final int STATE_LOAD_LOADING = 2;//加载中
    public static final int STATE_LOAD_EMPTY = 3;//数据为空
    public static final int STATE_LOAD_NO_NETWORK = 4;//没有网络
    NetworkBroadcastReceiver networkBroadcastReceiver;
    private View[] childView = new View[5];
    private int loadingId = R.layout.status_loading_progress_view;
    private int emptyId = R.layout.status_empty_view;
    private int errorId = R.layout.status_error_view;
    private int noNetworkId = R.layout.status_no_network_view;
    private View loadingView;//加载页
    private View errorView;//错误也
    private View successView;//成功页面
    private View emptyView;//空页面
    private View noNetworkView;//没有网络页面
    private LayoutInflater layoutInflater;
    private int currentStatus = -1;

    public StatusView(Context context) {
        this(context, null);
    }

    public StatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.setId(R.id.status_view);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();//获取自定义属性个数
        for (int i = 0; i < indexCount; i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.StatusView_empty_view) {
                emptyId = typedArray.getInteger(index, R.layout.status_empty_view);
            } else if (index == R.styleable.StatusView_loading_view) {
                loadingId = typedArray.getInteger(index, R.layout.status_loading_progress_view);
            } else if (index == R.styleable.StatusView_error_view) {
                errorId = typedArray.getInteger(index, R.layout.status_error_view);
            } else if (index == R.styleable.StatusView_no_network_view) {
                noNetworkId = typedArray.getInteger(index, R.layout.status_no_network_view);
            }
        }
        typedArray.recycle();
    }

    public void setStatus(int status) {
        setStatus(status, true);
    }


    public void setEmptyId(int emptyId) {
        this.emptyId = emptyId;
    }

    public void setLoadingId(int loadingId) {
        this.loadingId = loadingId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public void setNoNetworkId(int noNetworkId) {
        this.noNetworkId = noNetworkId;
    }

    /**
     * @param status        当前的状态
     * @param isHideContent 是否隐藏内容页
     */
    public void setStatus(int status, boolean isHideContent) {
        currentStatus = status;
        if (!isShowStatusAfter && isSuccessfully) return;
        if (status == STATE_LOAD_SUCCESS) isSuccessfully = true;
        if (status < 0 || status > childView.length - 1) {
            return;
        }
        if (successView == null) {
            successView = this.findViewById(R.id.status_content_view);
            if (successView == null) {
                Log.e(TAG, "you must set id is content_view in success view");
                return;
            }
            childView[STATE_LOAD_SUCCESS] = successView;
        }
        if (childView[status] == null) {
            View view = null;
            switch (status) {
                case STATE_LOAD_LOADING:
                    view = loadingView = inflateView(loadingId);
                    break;
                case STATE_LOAD_EMPTY:
                    view = emptyView = inflateView(emptyId);
                    break;
                case STATE_LOAD_ERROR:
                    view = errorView = inflateView(errorId);
                    setClick(view);
                    break;
                case STATE_LOAD_NO_NETWORK:
                    view = noNetworkView = inflateView(noNetworkId);
                    noNetworkView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetworkUtils.isNetworkConnected()) {
                                unregisterNetworkBroadcastReceiver();
                                onClick(noNetworkView);
                            } else {
                                registerNetworkBroadcastReceiver();
                                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                noNetworkView.getContext().startActivity(intent);
                            }
                        }
                    });
                    break;
            }
            if (view == null) {
                Log.e(TAG, "you net set currentStatus View,status=" + status);
                return;
            }
            childView[status] = view;
            addViewInRootView(view);

        }
        for (View view : childView) {
            if (view == null) continue;
            if (view == childView[status]) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(isHideContent ? View.GONE : view == successView ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void setClick(View view) {
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    public void showEmptyView() {
        setStatus(STATE_LOAD_EMPTY);
    }

    public void showSuccessView() {

        setStatus(STATE_LOAD_SUCCESS, false);
    }

    public void resetSuccess() {
        isSuccessfully = false;//有些页面需要重置成功过没有的状态
    }

    /**
     * @param isHideSuccessView 是否展示内容页
     */
    public void showLoadingView(boolean isHideSuccessView) {
        setStatus(STATE_LOAD_LOADING, isHideSuccessView);
    }

    @Override
    public boolean errorBack(Throwable throwable) {
        if (onErrorBackListener != null) {
            return onErrorBackListener.onErrorBack(throwable);
        }
        return false;
    }

    @Override
    public boolean isSuccessfully() {
        return isSuccessfully;
    }

    /**
     * 恢复
     *
     * @param state
     */
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.isSuccessfully = bundle.getBoolean(DIV_SAVE);
            super.onRestoreInstanceState(bundle.getParcelable(SYSTEM_SAVE));
            return;

        }
        super.onRestoreInstanceState(state);
    }

    private static final String SYSTEM_SAVE = "system_save";//系统存储的
    private static final String DIV_SAVE = "div_save";//自定义存储

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SYSTEM_SAVE, super.onSaveInstanceState());
        bundle.putBoolean(DIV_SAVE, isSuccessfully);
        return bundle;
    }

    @Override
    public void noMoreData() {
        if (onNoMoreDataListener != null) {//没有更多数据回调
            onNoMoreDataListener.onNoMore();
        }
    }

    @Override
    public void cnaShow(boolean isShowStatus) {
        this.isShowStatusAfter = isShowStatus;
    }


    /**
     * 如果之前有数据展示 就取消显示错误页面
     */
    public void showErrorView() {
        if (isSuccessfully) {
            showSuccessView();
            return;
        }
        setStatus(STATE_LOAD_ERROR);
    }

    /**
     * 如果之前有数据展示 就取消显示没有网络页面
     */
    public void showNoNetworkView() {
        if (isSuccessfully) {
            showSuccessView();
            return;
        }
        setStatus(STATE_LOAD_NO_NETWORK);
    }

    /**
     * 添加View进入rootView
     */
    private void addViewInRootView(View view) {

        if (view != null) {
            this.addView(view);
        }

    }

    /**
     * 填充
     *
     * @param id
     * @return
     */
    private View inflateView(int id) {
        if (id != 0) {
            try {
                return layoutInflater.inflate(id, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        if (onTryGetDataListener != null) {
            setStatus(STATE_LOAD_LOADING);
            onTryGetDataListener.onTryGet();
        }
    }

    private OnTryGetDataListener onTryGetDataListener;

    public interface OnTryGetDataListener {
        void onTryGet();
    }

    public void setOnTryGetDataListener(OnTryGetDataListener onTryGetDataListener) {
        this.onTryGetDataListener = onTryGetDataListener;
    }

    private OnNoMoreDataListener onNoMoreDataListener;


    public void setOnNoMoreDataListener(OnNoMoreDataListener onNoMoreDataListener) {
        this.onNoMoreDataListener = onNoMoreDataListener;
    }

    public interface OnNoMoreDataListener {
        void onNoMore();
    }

    public void setOnErrorBackListener(OnErrorBackListener onErrorBackListener) {
        this.onErrorBackListener = onErrorBackListener;
    }

    private OnErrorBackListener onErrorBackListener;

    public interface OnErrorBackListener {
        boolean onErrorBack(Throwable throwable);
    }

    /**
     * 注册广播
     */
    public void registerNetworkBroadcastReceiver() {
        IntentFilter mFilter = new IntentFilter();
        if (networkBroadcastReceiver == null) {
            networkBroadcastReceiver = new NetworkBroadcastReceiver();
        }
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkBroadcastReceiver, mFilter);

    }

    /**
     * 注销广播
     */
    public void unregisterNetworkBroadcastReceiver() {
        if (networkBroadcastReceiver != null) {
            getContext().unregisterReceiver(networkBroadcastReceiver);
        }
    }

    public class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (NetworkUtils.isNetworkConnected() && onTryGetDataListener != null) {
                    unregisterNetworkBroadcastReceiver();
                    onClick(null);
                }
            }
        }
    }
}