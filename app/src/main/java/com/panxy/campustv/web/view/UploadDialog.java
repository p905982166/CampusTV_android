package com.panxy.campustv.web.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.panxy.campustv.R;

public class UploadDialog extends Dialog{

    //在构造方法里提前加载了样式
    private Context context;//上下文
    private int layoutResID;//布局文件id

    public TextView tv_progress;    //当前文件上传进度
    public ProgressBar progressBar; //进度条
    public TextView tv_upload_info; //文件占比进度
    public TextView tv_count_down;  //倒计时



    public UploadDialog(Context context,int layoutResID){
        super(context, R.style.MyUploadDialog);//加载dialog的样式
        this.context = context;
        this.layoutResID = layoutResID;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        }
        //dialogWindow.setWindowAnimations();设置动画效果
        setContentView(layoutResID);


        WindowManager windowManager = ((Activity)context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);//不能省略,必须有
        int screenWidth = outSize.x;//得到屏幕的宽度
        int screenHeight = outSize.y;//得到屏幕的高度

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = screenWidth * 4 / 5;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失

        tv_progress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progressBar);
        tv_upload_info = (TextView)findViewById(R.id.tv_upload_info);
        tv_count_down = (TextView)findViewById(R.id.tv_count_down);

    }





//    private OnCenterItemClickListener listener;
//
//    public interface OnCenterItemClickListener {
//        void OnCenterItemClick(UploadDialog dialog, View view);
//    }
//
//    //很明显我们要在这里面写个接口，然后添加一个方法
//    public void setOnCenterItemClickListener(OnCenterItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    @Override
//    public void onClick(View v) {
//        dismiss();//注意：我在这里加了这句话，表示只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
//        listener.OnCenterItemClick(this,v);
//    }

}
