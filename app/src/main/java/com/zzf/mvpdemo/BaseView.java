package com.zzf.mvpdemo;

public interface BaseView {
    /**
     * 显示正在加载view
     */
    void showLoading();
    /**
     * 关闭正在加载view
     */
    void hideLoading();

    /**
     * 显示请求错误提示
     */
    void showErr(String msg);
}
