package com.panxy.campustv.global.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.panxy.campustv.global.main.CampusTvApplication;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesUtil {

    private static final String TAG = "SharedPreferencesUtil";
    private static String spName = "lawyer";
    private static SharedPreferencesUtil spUtil;
    private static SharedPreferences mPreferences;

    private SharedPreferencesUtil(String spName, int modePrivate) {
        mPreferences = CampusTvApplication.getContext().getSharedPreferences(spName, modePrivate);
    }

    public static SharedPreferencesUtil getInstance() {
        if (spUtil == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (spUtil == null) {
                    spUtil = new SharedPreferencesUtil(spName, Context.MODE_PRIVATE);
                }
            }
        }
        return spUtil;
    }

    /**
     * 批量保存
     */
    public void saveInfo(Map<String, String> info) {
        SharedPreferences.Editor editor = mPreferences.edit();
        Set<String> keys = info.keySet();
        for (String str : keys) {
            editor.putString(str, info.get(str));
        }
        editor.commit();
    }

    /**
     * 保存string类型数据
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 根据key读取参数
     */
    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    /**
     * 保存long类型数据
     */
    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 获取long类型数据
     */
    public long getLong(String key) {
        return mPreferences.getLong(key, -1);
    }

    /**
     * 保存int类型数据
     *
     * @param value
     */
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取int类型数据
     *
     * @param key
     * @return
     */
    public int getInt(String key) {
        return mPreferences.getInt(key, -1);
    }

    /**
     * 读取状态
     */
    public boolean getBoolean(String key, boolean def) {
        return mPreferences.getBoolean(key, def);
    }

    /**
     * 读取状态
     */
    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 根据Key删除某个数据
     */
    public void removeByKey(String key) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(key);
        editor.commit();
    }
}
