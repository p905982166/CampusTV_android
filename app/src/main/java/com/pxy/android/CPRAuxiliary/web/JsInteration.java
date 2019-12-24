package com.pxy.android.CPRAuxiliary.web;

import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;

public class JsInteration {
    private static final String TAG = "JsInteration";
    private Handler mHandler;

    public JsInteration(Handler handler) {
        mHandler = handler;
    }

    @JavascriptInterface
    public void showToast(String content){
        Message message = mHandler.obtainMessage();
        message.what = 1;
        message.obj = content;
        mHandler.sendMessage(message);
    }
}
