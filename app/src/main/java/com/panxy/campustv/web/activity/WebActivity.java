package com.panxy.campustv.web.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.panxy.campustv.global.common.Config;
import com.panxy.campustv.R;
import com.panxy.campustv.global.activity.BaseActivity;
import com.panxy.campustv.global.common.Constant;
import com.panxy.campustv.global.common.RequestUrl;
import com.panxy.campustv.web.utils.JsInteration;
import com.panxy.campustv.web.utils.MyWebChromeClient;
import com.panxy.campustv.web.utils.MyWebViewClient;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.io.File;
import java.util.List;

import barlibarary.ImmersionBar;


public class WebActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WebActivity";
    private WebView mWebview;
    private ImageView iv_net_error;
    private PopupWindow popupWindow;
    private View popupView;  // 弹窗view
    private RecyclerView rl_uploaded;
    private boolean isPopup = false;
    private boolean delAble = false;  //等待加载完已上传文件

    private String jobId;

    private long lastClickTime = 0L; // 防止点击两次上传文件

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constant.SELECT_ICON:
                    selectIcon();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //statusBarDarkFont 设置状态栏字体颜色
        ImmersionBar.with(this).statusBarView(R.id.top_view)
                .statusBarDarkFont(true, 0.2f)
                .navigationBarColor(R.color.colorPrimary)
                //解决软键盘与底部输入框冲突问题
                .keyboardEnable(true)
                .addTag("PicAndColor")
                .init();
        // 当含有导航栏时，修改导航栏颜色
        if (ImmersionBar.hasNavigationBar(this)) {
            ImmersionBar.with(this).navigationBarColor(R.color.navigation_bar).init();
        }
    }

    protected void initUI() {
        mWebview = findViewById(R.id.webview);
        iv_net_error = findViewById(R.id.iv_net_error);
    }

    protected void initData() {
        mWebview.loadUrl(RequestUrl.LOAD_URL_TEST);
    }

    protected void initControl() {
        iv_net_error.setOnClickListener(this);
        initWebview();
    }

    private void initWebview() {
        //是否支持调试
        mWebview.setWebContentsDebuggingEnabled(Config.IS_DEBUG);
        // 设置加载文件的格式
        mWebview.getSettings().setDefaultTextEncodingName(Constant.UTF_8);
        // 设置支持javascript
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setBlockNetworkImage(false);//解决图片不显示
        //手机字体放大，app不受影响
        mWebview.getSettings().setTextZoom(100);
        mWebview.addJavascriptInterface(new JsInteration(mHandler , WebActivity.this), "android");
        // 屏蔽复制事件
        mWebview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        // 启用硬件加速
        mWebview.getSettings().setPluginState(WebSettings.PluginState.ON);
        // 设置支持缩放
        mWebview.getSettings().setSupportZoom(false); // 设置是否支持缩放
        mWebview.getSettings().setBuiltInZoomControls(true); // 设置是否显示内建缩放工具
        //设置本地缓存
        mWebview.getSettings().setDomStorageEnabled(true);
        mWebview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存
        // webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        // 设置允许访问文件
        mWebview.getSettings().setAllowFileAccess(false);
        //允许webview对文件的操作
        mWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        // 设置Web视图
        mWebview.setWebViewClient(new MyWebViewClient(mHandler));
        // 设置下载监听
        // 设置缩放
        mWebview.setBackgroundColor(0);
        mWebview.setWebChromeClient(new MyWebChromeClient());
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

    }

    private void selectIcon() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1); // 打开相册

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        Toast.makeText(WebActivity.this,"读写sdk权限被拒绝",Toast.LENGTH_LONG).show();
                    }
                })
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    //从相册选中照片
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
        }

    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(WebActivity.this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        displayImage(imagePath);
    }

    /**
     * android 4.4以前的处理方式
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {

            //try{
                File file = new File(imagePath);
                Bitmap.CompressFormat pic = null;
                if(file.getName().endsWith("jpg") || file.getName().endsWith("jpeg") ){
                    pic = Bitmap.CompressFormat.JPEG;
                }else if(file.getName().endsWith("png")){
                    pic = Bitmap.CompressFormat.PNG;
                }else {
                    Toast.makeText(WebActivity.this, "不支持该格式的图片，请重新选择", Toast.LENGTH_SHORT).show();
                    return;
                }
                long timeStamp = System.currentTimeMillis();
                int random = (int)(Math.random() * 1000);

                /*
                压缩图片
                File temp = new File(getExternalCacheDir(), timeStamp + "" + random + ".jpg");
                FileInputStream fis = new FileInputStream(imagePath);
                Bitmap bitmap  = BitmapFactory.decodeStream(fis);
                Bitmap compressBitmap = ImageCompressL(bitmap);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
                compressBitmap.compress(pic, 100, bos);
                bos.flush();
                bos.close();*/
               // String tempPath = "'"+ temp.getPath() +"'";
                String tempPath = "'"+ imagePath +"'";

                mWebview.evaluateJavascript("javascript:changeIcon("+ tempPath +")", null);

//            }
//            catch (IOException e){
//                e.printStackTrace();
//            }


        } else {
            Toast.makeText(WebActivity.this, "获取相册图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 压缩图片方法
     *
     * @param bitmap
     * @return
     */
    private Bitmap ImageCompressL(Bitmap bitmap) {
        double targetwidth = Math.sqrt(100.00 * 1000);//约等于100多KB，可自行进行调节
        if (bitmap.getWidth() > targetwidth || bitmap.getHeight() > targetwidth) {
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            double x = Math.max(targetwidth / bitmap.getWidth(), targetwidth
                    / bitmap.getHeight());
            // 缩放图片动作
            matrix.postScale((float) x, (float) x);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebview != null) {
            mWebview.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mWebview != null) {
            mWebview.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.destroy();
        }
        super.onDestroy();
    }



    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isPopup){
                //首先判断是否处于弹窗
                popupWindow.dismiss();
                return true;
            }
            if(mWebview.canGoBack()){
                mWebview.goBack();
                return true;
            }else {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    Toast.makeText(getApplicationContext(), "再按一次返回退出校园竞技赛事app", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                    return true;
                }
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_net_error:
                if (mWebview != null) {
                    mWebview.setVisibility(View.VISIBLE);
                    mWebview.loadUrl(RequestUrl.LOAD_URL);
                }
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

}
