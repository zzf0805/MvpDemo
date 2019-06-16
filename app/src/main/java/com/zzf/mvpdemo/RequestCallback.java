package com.zzf.mvpdemo;

public interface RequestCallback {
    /**
     * 数据请求成功
     *
     * @param data 请求到的数据
     */
    void onSuccess(Object data, String flag);

    /**
     * 请求数据失败，指在请求网络API接口请求方式时，出现无法联网、
     * 缺少权限，内存泄露等原因导致无法连接到请求数据源。
     */
    void onError(String msg, String flag);
}
