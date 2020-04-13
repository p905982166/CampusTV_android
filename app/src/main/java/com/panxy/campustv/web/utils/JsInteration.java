package com.panxy.campustv.web.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.panxy.campustv.global.common.Constant;
import com.panxy.campustv.global.main.CampusTvApplication;
import com.panxy.campustv.global.utils.LogUtil;
import com.panxy.campustv.global.utils.NotchScreenUtil;


import static com.panxy.campustv.global.utils.NotchScreenUtil.DEVICE_BRAND_HUAWEI;
import static com.panxy.campustv.global.utils.NotchScreenUtil.DEVICE_BRAND_OPPO;
import static com.panxy.campustv.global.utils.NotchScreenUtil.DEVICE_BRAND_VIVO;

public class JsInteration {
    private static final String TAG = "JsInteration";
    private Handler mHandler;
    private Context mContext;

    public JsInteration(Handler handler, Context context) {
        mHandler = handler;
        mContext = context;
    }

    @JavascriptInterface
    public void uploadHeadImage(String cookie){
        Message message = mHandler.obtainMessage();
        message.what = Constant.SELECT_HEAD_IMAGE;
        JSONObject jo = new JSONObject();
        jo.put("cookie", cookie);
        message.obj = jo;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public void socialCamera(){
        Message message = mHandler.obtainMessage();
        message.what = Constant.SOCIAL_CAMERA;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public void socialAlbum(){
        Message message = mHandler.obtainMessage();
        message.what = Constant.SOCIAL_ALBUM;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public void createSocial(String socialBody, String fileList, String cookie){
        Message message = mHandler.obtainMessage();
        message.what = Constant.SUBMIT_CREATE_SOCIAL;
        JSONObject jo = new JSONObject();
        jo.put("socialBody", socialBody);
        jo.put("fileList", fileList);
        jo.put("cookie", cookie);
        message.obj = jo;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public void selectNewsIcon() {
        Message message = mHandler.obtainMessage();
        message.what = Constant.SELECT_ICON;
        mHandler.sendMessage(message);
    }
    @JavascriptInterface
    public void selectNewsVideo() {
        Message message = mHandler.obtainMessage();
        message.what = Constant.SELECT_VIDEO;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public void selectNewsImg() {
        Message message = mHandler.obtainMessage();
        message.what = Constant.SELECT_IMAGE;
        mHandler.sendMessage(message);
    }
    @JavascriptInterface
    public void submitSaveNews(String newsInfo, String bodyItem,String server, String cookie){
        Message message = mHandler.obtainMessage();
        message.what = Constant.SUBMIT_SAVE_NEWS;
        JSONObject jo = new JSONObject();
        jo.put("newsInfo", newsInfo);
        jo.put("bodyItem", bodyItem);
        jo.put("server", server);
        jo.put("cookie", cookie);
        message.obj = jo;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public String getDevice() {
        String device = "";
        switch (CampusTvApplication.getInstance().getDeviceBrand()){
            case 0:
                device = "unknown";
                break;
            case DEVICE_BRAND_OPPO:
                device = "OPPO";
                break;
            case DEVICE_BRAND_HUAWEI:
                device = "HUAWEI";
                break;
            case DEVICE_BRAND_VIVO:
                device = "VIVO";
                break;
        }
        return device;
    }

    @JavascriptInterface
    public int getNotchSize() {

        int device = 0;
        switch (CampusTvApplication.getInstance().getDeviceBrand()){
            case 0:
                break;
            case DEVICE_BRAND_OPPO:
                if(NotchScreenUtil.hasNotchInScreenAtOppo(mContext)){
                    device = NotchScreenUtil.getNotchSizeAtOppo();
                }
                break;
            case DEVICE_BRAND_HUAWEI:
                if(NotchScreenUtil.hasNotchInScreenAtHuawei(mContext)){
                    device = NotchScreenUtil.getNotchSizeAtHuawei(mContext);
                }
                break;
            case DEVICE_BRAND_VIVO:
                if(NotchScreenUtil.hasNotchInScreenAtVivo(mContext)){
                    device = NotchScreenUtil.getNotchSizeAtVivo(mContext);
                }
                break;
        }
        return device;
    }

    @JavascriptInterface
    public int getStatusBarHeight(){
        Log.d(TAG, "getStatusBarHeight: " + CampusTvApplication.getStatusBarHeight());
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int statusBarHeight = CampusTvApplication.getStatusBarHeight();
        return (int) (statusBarHeight / scale + 0.5f);
    }

}
