package com.panxy.campustv.global.utils;

import android.util.Log;

import com.panxy.campustv.global.common.Config;


/**
 * 使用该类方便统一关闭日志输出
 */
public class LogUtil {

    /**
     * 日志输出级别NONE
     */
    public static final int LEVEL_NONE = 0;
    /**
     * 日志输出级别E
     */
    public static final int LEVEL_ERROR = 1;
    /**
     * 日志输出级别W
     */
    public static final int LEVEL_WARN = 2;
    /**
     * 日志输出级别I
     */
    public static final int LEVEL_INFO = 3;
    /**
     * 日志输出级别D
     */
    public static final int LEVEL_DEBUG = 4;
    /**
     * 日志输出级别V
     */
    public static final int LEVEL_VERBOSE = 5;

    /**
     * 是否允许输出log
     */
    private static int mDebuggable = Config.mDebuggable;

    /**
     * 以级别为 verbose 的形式输出LOG
     */
    public static void v(String tag, String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    /**
     * 以级别为 debug 的形式输出LOG
     */
    public static void d(String tag, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * 以级别为 info 的形式输出LOG
     */
    public static void i(String tag, String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    /**
     * 以级别为 warn 的形式输出LOG
     */
    public static void w(String tag, String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }

    /**
     * 以级别为 warn 的形式输出Throwable
     */
    public static void w(String tag, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, "", tr);
        }
    }

    /**
     * 以级别为 warn 的形式输出LOG信息和Throwable
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN && null != msg) {
            Log.w(tag, msg, tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String tag, String msg) {
        //将错误日志进行记录
        //        ErrorLog error = ErrorLogUtils.createErrorLog();
        //        error.setErrorInfo(tag + ">>>详细信息--->>" + msg);
        //        ErrorLogUtils.insert(error);
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    /**
     * 以级别为 e 的形式输出Throwable
     */
    public static void e(String tag, Throwable tr) {
        StackTraceElement[] stack = tr.getStackTrace();
        StringBuffer stringBuffer = new StringBuffer();
        if (stack != null) {
            for (int i = 0; i < stack.length - 1; i++) {
                stringBuffer.append("\n" + stack[i].toString());
            }
        }

        //将错误日志进行记录
        //        ErrorLog error = ErrorLogUtils.createErrorLog();
        //        error.setErrorInfo(tag + tr.getClass().getCanonicalName() + ">>>详细信息--->>" + stringBuffer.toString());
        //        ErrorLogUtils.insert(error);
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, "", tr);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG信息和Throwable
     */
    public static void e(String tag, String msg, Throwable tr) {
        StackTraceElement[] stack = tr.getStackTrace();
        StringBuffer stringBuffer = new StringBuffer();
        if (stack != null) {
            for (int i = 0; i < stack.length - 1; i++) {
                stringBuffer.append("\n" + stack[i].toString());
            }
        }

        //将错误日志进行记录
        //        ErrorLog error = ErrorLogUtils.createErrorLog();
        //        error.setErrorInfo(tag + tr.getClass().getCanonicalName() + ":" + msg + tr.getMessage() + ">>>详细信息--->>" + stringBuffer.toString());
        //        ErrorLogUtils.insert(error);
        if (mDebuggable >= LEVEL_ERROR && null != msg) {
            Log.e(tag, msg, tr);
        }
    }

}
