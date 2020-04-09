package com.panxy.campustv.web.utils;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.panxy.campustv.global.common.Constant;
import com.panxy.campustv.global.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class MyWebViewClient extends WebViewClient {
    private static final String TAG = "MyWebViewClient";
    private Handler mHandler;

    public MyWebViewClient(Handler handler) {
        mHandler = handler;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return true;
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        FileInputStream input;

        String url = request.getUrl().toString();

        String key = "http://androidimg";
        /*如果请求包含约定的字段 说明是要拿本地的图片*/
        if (url.contains(key)) {
            Log.d(TAG, "拦截了请求");
            String imgPath = url.replace(key, "");

            try {
                /*重新构造WebResourceResponse  将数据已流的方式传入*/
                input = new FileInputStream(new File(imgPath.trim()));
                WebResourceResponse response = new WebResourceResponse("image/jpg", "UTF-8", input);

                /*返回WebResourceResponse*/
                return response;
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            }

        }
        return super.shouldInterceptRequest(view, request);
    }

    /**
     * 通知主机应用程序页面已开始加载
     *
     * @param view    视图启动回调的WebView
     * @param url     要加载的网址
     * @param favicon 此页面的favicon如果已存在于此页面中数据库
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    /**
     * 通知主机应用程序页面已完成加载
     *
     * @param view 视图启动回调的WebView
     * @param url  页面的网址
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LogUtil.e(TAG, "onPageFinished 完成加载url:  " + url);
        Integer integer = (Integer) view.getTag();
        if (integer != null && integer < 0) {

        } else {
            mHandler.sendEmptyMessage(Constant.ON_PAGE_FINISHED);
        }
    }

    /**
     * 向主机应用程序报告错误。 这些错误是不可恢复的（即主要资源不可用）
     *
     * @param view        视图启动回调的WebView
     * @param errorCode   errorCode与ERROR_ *值对应的错误代码
     * @param description 描述错误的字符串
     * @param failingUrl  无法加载的网址
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        view.setTag(errorCode);
        mHandler.sendEmptyMessage(errorCode);
        LogUtil.e(TAG, "onReceivedError1 Web资源加载错误:  " + errorCode);
        LogUtil.e(TAG, "onReceivedError1 Web资源加载错误描述:  " + description);
        LogUtil.e(TAG, "onReceivedError1 Web资源加载错误请求路径:  " + failingUrl);
    }

    /**
     *   向主机应用程序报告Web资源加载错误
     *
     * @param view    视图启动回调的WebView
     * @param request 原始请求
     * @param error   有关错误的信息
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        LogUtil.e(TAG, "onReceivedError2 Web资源加载错误:  " + error.getErrorCode());
    }

    /**
     * 通知主机应用程序已从服务器收到HTTP错误加载资源
     *
     * @param view          视图启动回调的WebView
     * @param request       原始请求
     * @param errorResponse 有关发生错误的信息
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        LogUtil.e(TAG, "onReceivedHttpError 从服务器收到HTTP错误:  " + errorResponse.getStatusCode());
    }

    /**
     * 通知主机应用程序加载时发生SSL错误资源
     *
     * @param view    视图启动回调的WebView
     * @param handler 一个将处理用户的SslErrorHandler对象回应
     * @param error   SSL错误对象
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        LogUtil.e(TAG, "onReceivedSslError发生SSL错误资源:  " + error.getUrl());
    }
}
