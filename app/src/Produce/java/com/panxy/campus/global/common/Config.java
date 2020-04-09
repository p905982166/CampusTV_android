package com.panxy.campustv.global.common;

import com.panxy.campustv.global.utils.LogUtil;

public interface Config {
    //首页  生产
    String domain = "http://www.complicated.top/h5/";

    //知律请求接口url  生产
    String campus_accept_web = "http://www.complicated.top:8088/";


    /**
     * 是否是调式模式
     */
    boolean IS_DEBUG = false;

    /**
     * 允许输出日志的级别
     */
    int mDebuggable = LogUtil.LEVEL_WARN;

    String IMAGE = "https://www.zhihuizhengan.com/image/";
}
