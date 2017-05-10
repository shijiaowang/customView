package com.wangyang.divviewlibrary.view.statusview;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 页面状态转换
 */
public interface IStatusChange {
    void showSuccessView();
    void showEmptyView();
    void showErrorView();
    void showNoNetworkView();
    void showLoadingView(boolean isHideSuccessView);
    boolean errorBack(Throwable throwable);//是否需要错误返回，需要就不会自动处理错误页面
    boolean isSuccessfully();//是否成功过，如果页面从未成功过 返回0就可以判断为空页面
    void noMoreData();//没有更多数据回调 成功过 之后状态码为0
    void cnaShow(boolean isShowStatus);//之后是否展示


}