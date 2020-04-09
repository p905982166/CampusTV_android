package com.panxy.campustv.web.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

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

    /**
     * 上传文件
     * @param jobId 任务id
     */
    @JavascriptInterface
    public void uploadFile(String jobId) {
        LogUtil.e(TAG, "上传文件");
        Message message = mHandler.obtainMessage();
        message.what = Constant.UPLOAD_SELECT_FILES;
        message.obj = jobId;
        mHandler.sendMessage(message);
    }

    /**
     * 查看已上传文档，弹出弹窗
     * @param jobId 任务id
     */
    @JavascriptInterface
    public void popupUploaded(String jobId) {
        LogUtil.e(TAG, "已上传");
        Message message = mHandler.obtainMessage();
        message.what = Constant.POPUP_UPLOADED;
        message.obj = jobId;
        mHandler.sendMessage(message);
    }

    @JavascriptInterface
    public void selectNewsIcon() {
        Message message = mHandler.obtainMessage();
        message.what = Constant.SELECT_ICON;
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
