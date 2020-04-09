package com.panxy.campustv.global.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        //初始化沉浸式
        if (isImmersionBarEnabled()) {
            initImmersionBar();
        }
        initUI();
        initData();
        initControl();
    }

    /**
     * 初始化视图
     */
    protected void initUI() {
    }

    /**
     * 初始化数据
     */
    protected void initData() {
    }

    /**
     * 视图添加监听和视图与数据进行绑定
     */
    protected void initControl() {
    }

    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract int getLayoutId();

    /**
     * 是否可以使用沉浸式 默认使用
     *
     * @return the boolean
     */
    protected boolean isImmersionBarEnabled() {
        return true;
    }

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
    }
}
