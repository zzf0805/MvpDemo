package com.zzf.mvpdemo;

import java.util.HashMap;

public class DemoPresenter extends BasePresenter<ViewImpl> {

    public void getNews(HashMap<String,String> hashMap){
        if (!isViewAttached()) {
            return;
        }

        DemoModel.getNews(hashMap,new RequestCallback() {
            @Override
            public void onSuccess(Object data, String flag) {
                if (isViewAttached()) {
                    getView().hideLoading();
                    getView().showData(data, flag);
                }
            }

            @Override
            public void onError(String msg, String flag) {
                if (isViewAttached()) {
                    getView().hideLoading();
                    getView().showErr(flag + ": " + msg);
                }
            }
        });
    }
}
