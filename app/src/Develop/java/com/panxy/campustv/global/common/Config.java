package com.panxy.campustv.global.common;


import com.panxy.campustv.global.utils.LogUtil;

public interface Config {
    //首页   测试
    String domain = "http://www.complicated.top/h5/";
    String test_domain = "http://192.168.43.249:8080/h5/";

    //知律请求接口url  测试
    String campus_accept_web = "http://www.complicated.top:8088/";


    /**
     * 是否是调式模式
     */
    boolean IS_DEBUG = true;

    /**
     * 允许输出日志的级别
     */
    int mDebuggable = LogUtil.LEVEL_VERBOSE;

}
