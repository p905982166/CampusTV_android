package com.panxy.campustv.web.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.panxy.campustv.global.common.Config;
import com.panxy.campustv.R;
import com.panxy.campustv.global.activity.BaseActivity;
import com.panxy.campustv.global.common.Constant;
import com.panxy.campustv.global.common.RequestUrl;
import com.panxy.campustv.room.MainRoomActivity;
import com.panxy.campustv.room.video.push.VideoPushActivity;
import com.panxy.campustv.web.entity.NewsListsWithBLOBs;
import com.panxy.campustv.web.utils.HttpUtil;
import com.panxy.campustv.web.utils.JsInteration;
import com.panxy.campustv.web.utils.MyWebViewClient;
import com.panxy.campustv.web.utils.uploadlibrary.listener.ProgressListener;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import barlibarary.BarHide;
import barlibarary.ImmersionBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WebActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WebActivity";

    private String headImagePath = "";
    private String iconRealPath = "";
    private String videoRealPath = "";
    private String imageRealPath = "";

    private int function = 0;
    private final int FUNCTION_PICK_NEWS_ICON = 1;
    private final int FUNCTION_PICK_NEWS_VIDEO = 2;
    private final int FUNCTION_PICK_NEWS_IMAGE = 3;
    private final int FUNCTION_PICK_HEAD_IMAGE = 4;
    private final int FUNCTION_SOCIAL_CAMERA = 5;
    private final int FUNCTION_SOCIAL_ALBUM = 6;
    private final int FUNCTION_PICK_TEAM_LOGO = 7;
    private String cookie = "";

    private WebView mWebview;
    private ImageView iv_net_error;

    private boolean isFullScreen = false;

    private FrameLayout flVideoContainer;

    private Gson gson = new Gson();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //收起软键盘
            InputMethodManager manager = ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE));
            if (manager != null)
                manager.hideSoftInputFromWindow(mWebview.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

            JSONObject jo = null;
            switch (msg.what) {
                case Constant.SELECT_HEAD_IMAGE:
                    jo = (JSONObject) msg.obj;
                    cookie = jo.getString("cookie");
                    selectHeadImage();
                    break;
                case Constant.SOCIAL_CAMERA:
                    selectSocialCamera();
                    break;
                case Constant.SELECT_TEAM_LOGO:
                    selectTeamLogo();
                    break;
                case Constant.SOCIAL_ALBUM:
                    selectSocialAlbum();
                    break;
                case Constant.SELECT_ICON:
                    selectNewsIcon();
                    break;
                case Constant.SELECT_VIDEO:
                    selectNewsVideo();
                    break;
                case Constant.SELECT_IMAGE:
                    selectNewsImg();
                    break;
                case Constant.SUBMIT_SAVE_NEWS:
                    jo = (JSONObject) msg.obj;
                    String newsInfo = jo.getString("newsInfo");
                    String bodyItem = jo.getString("bodyItem");
                    String server = jo.getString("server");
                    cookie = jo.getString("cookie");
                    NewsListsWithBLOBs newsBean = gson.fromJson(newsInfo, new TypeToken<NewsListsWithBLOBs>(){}.getType());
                    submitSaveNews(newsBean, bodyItem,server, cookie);
                    break;
                case Constant.REQUEST_NEWS_SUCCESS:
                    jo = (JSONObject) msg.obj;
                    if(jo.getString("state").equals("200")){
                        Integer newsId = jo.getInteger("news_id");
                        Toast.makeText(WebActivity.this, "成功创建新闻id：" + newsId, Toast.LENGTH_SHORT).show();
                        mWebview.evaluateJavascript("javascript:stopEdit('" + newsId +"')", null);
                        iconRealPath = "";
                        videoRealPath = "";
                    }else {
                        Toast.makeText(WebActivity.this, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.REQUEST_HEAD_IMAGE_SUCCESS:
                    jo = (JSONObject) msg.obj;
                    if(jo.getString("state").equals("200")){
                        Toast.makeText(WebActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        mWebview.evaluateJavascript("javascript:changeHeadImage('"+ headImagePath +"')", null);
                    }else {
                        Toast.makeText(WebActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.SUBMIT_CREATE_SOCIAL:
                    jo = (JSONObject) msg.obj;
                    String socialBody = jo.getString("socialBody");
                    String fileList = jo.getString("fileList");
                    cookie = jo.getString("cookie");
                    createSocial(socialBody, fileList, cookie);
                    break;
                case Constant.REQUEST_SOCIAL_SUCCESS:
                    jo = (JSONObject) msg.obj;
                    if(jo.getString("state").equals("200")){
                        Toast.makeText(WebActivity.this, "动态发布成功", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(WebActivity.this, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.SUBMIT_CREATE_TEAM:
                    jo = (JSONObject) msg.obj;
                    String teamName = jo.getString("teamName");
                    String teamBelongTo = jo.getString("teamBelongTo");
                    String teamLogo = jo.getString("teamLogo");
                    cookie = jo.getString("cookie");
                    createTeam(teamName, teamBelongTo, teamLogo, cookie);
                    break;
                case Constant.REQUEST_CREATE_TEAM_SUCCESS:
                    jo = (JSONObject) msg.obj;
                    if(jo.getString("state").equals("200")){
                        Toast.makeText(WebActivity.this, "队伍创建成功", Toast.LENGTH_SHORT).show();
                        Integer teamId = jo.getInteger("teamId");
                        mWebview.evaluateJavascript("javascript:createTeamSuccess('"+teamId+"')", null);
                    }else {
                        Toast.makeText(WebActivity.this, jo.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.INTO_ROOM:
                    int userType = (int) msg.obj;
                    intoTvRoom(userType);
                    break;
                default:
                    break;
            }
        }
    };

    private void intoTvRoom(final int type){
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if(type == 1){
                            startActivity(new Intent(WebActivity.this, VideoPushActivity.class));
                        }else {
                            startActivity(new Intent(WebActivity.this, MainRoomActivity.class));
                        }
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

    private void createTeam(String teamName, String teamBelongTo, String teamLogo, String cookie) {
        String logoPath = teamLogo.substring("http://androidimg".length());
        List<File> files = new ArrayList<>();
        File file = new File(logoPath);
        Bitmap.CompressFormat pic = null;
        //裁剪图片
        try{
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
            File temp = new File(getExternalCacheDir(), timeStamp + "" + random + ".jpg");
            FileInputStream fis = new FileInputStream(logoPath);
            Bitmap bitmap  = BitmapFactory.decodeStream(fis);
            Bitmap compressBitmap = ImageCompressL(bitmap);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
            compressBitmap.compress(pic, 100, bos);
            bos.flush();
            bos.close();

            files.add(temp);
            Map<String, Object> map = new HashMap<>();
            map.put("teamName", teamName);
            map.put("teamBelongTo", teamBelongTo);
            HttpUtil.doAsynchFileHttpPost(RequestUrl.CREATE_TEAM, files, map,
                    new ProgressListener() {
                        @Override
                        public void onProgress(long currentBytes, long contentLength, boolean done) {
                            int progress = (int) ((100 * currentBytes) / contentLength);
                            Log.i("uploading", (100 * currentBytes) / contentLength + " % done ");
                        }
                    }, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            JSONObject resObj = JSONObject.parseObject(res);
                            Message message = mHandler.obtainMessage();
                            message.what = Constant.REQUEST_CREATE_TEAM_SUCCESS;
                            message.obj = resObj;
                            mHandler.sendMessage(message);

                        }
                    }, cookie);
        }catch (IOException e){
            e.printStackTrace();
        }



    }

    private void createSocial(String socialBody, String fileList, String cookie) {

        JSONArray fileArray = JSONArray.parseArray(fileList);

        List<File> files = new ArrayList<>();
        for (Object o : fileArray) {
            String oldPath = (String) o;
            if(oldPath.contains("http://androidimg")){
                String path = oldPath.substring("http://androidimg".length());
                File file = new File(path);
                files.add(file);
            }
        }

        HttpUtil.doAsynchFileHttpPost(RequestUrl.CREATE_SOCIAL, files, socialBody,
                new ProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                        int progress = (int) ((100 * currentBytes) / contentLength);
                        Log.i("uploading", (100 * currentBytes) / contentLength + " % done ");
                    }
                }, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        JSONObject resObj = JSONObject.parseObject(res);
                        Message message = mHandler.obtainMessage();
                        message.what = Constant.REQUEST_SOCIAL_SUCCESS;
                        message.obj = resObj;
                        mHandler.sendMessage(message);


                    }
                }, cookie);

    }

    private File copyFile(File file){
        long timeStamp = System.currentTimeMillis();
        int random = (int)(Math.random() * 1000);
        String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
        File temp = new File(getExternalCacheDir(), timeStamp + "" + random + suffix);
        if(FileUtils.copyFile(file, temp)){
            return temp;
        }
        return null;
    }

    private void submitSaveNews(NewsListsWithBLOBs newsInfo, String bodyItem, String server, String cookie) {
        List<File> files = new ArrayList<>();

        /* icon */

        if(!TextUtils.isEmpty(iconRealPath) && iconRealPath.length() > 4){
            File iconFile = new File(iconRealPath);
            File iconAfterCopy = copyFile(iconFile);

            if(iconAfterCopy != null){
                files.add(iconAfterCopy);
                newsInfo.setNewsIcon(iconAfterCopy.getName());
            }

        }else{
            newsInfo.setNewsIcon(null);
        }

        /* video */
        if(!TextUtils.isEmpty(videoRealPath)){
            File videoFile = new File(videoRealPath);
            File videoAfterCopy = copyFile(videoFile);
            if(videoAfterCopy != null){
                files.add(videoAfterCopy);
                newsInfo.setVideos(videoAfterCopy.getName());
            }
        }

        /* image and body */
        JSONArray newsBodyArray = JSONArray.parseArray(bodyItem);

        JSONArray tempBodyArray = new JSONArray();

        for (Object o : newsBodyArray) {
            JSONObject bodyObj = new JSONObject();
            JSONObject newsBodyObj = (JSONObject) o;
            if(newsBodyObj.getInteger("type").equals(0)){
                //段落  不需要转换
                bodyObj.put("type", 0);
                bodyObj.put("content", newsBodyObj.getString("content"));
            }else if(newsBodyObj.getInteger("type").equals(1)){
                //图片
                String path = newsBodyObj.getString("content");
                if(path.contains("http://androidimg")){
                    //新增的图片
                    path = path.substring("http://androidimg".length());
                    File img = new File(path);
                    if(!path.contains("com.panxy.campustv")){
                        File imgAfterCopy = copyFile(img);
                        if(imgAfterCopy != null){
                            files.add(imgAfterCopy);
                            bodyObj.put("type", 1);
                            bodyObj.put("content", imgAfterCopy.getName());
                        }
                    }else {
                        //QQ浏览器的文件，不需要再复制一次
                        files.add(img);
                        bodyObj.put("type", 1);
                        bodyObj.put("content", img.getName());
                    }


                }else{
                    //已上传过的图片，无需重复上传
                    bodyObj.put("type", 1);
                    if(path.contains(server)){
                        path = path.substring(server.length());
                    }
                    bodyObj.put("content", path);
                }



            }
            tempBodyArray.add(bodyObj);

        }

        newsInfo.setNewsBody(tempBodyArray.toJSONString());

        //newsInfo.setPhotos(imgObj.toJSONString());

        //已经拿到了所有的信息

        HttpUtil.doAsynchFileHttpPost(RequestUrl.CREATE_NEWS, files, newsInfo,
                new ProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                        int progress = (int) ((100 * currentBytes) / contentLength);
                        Log.i("uploading", (100 * currentBytes) / contentLength + " % done ");
                    }
                }, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        JSONObject resObj = JSONObject.parseObject(res);
                        Message message = mHandler.obtainMessage();
                        message.what = Constant.REQUEST_NEWS_SUCCESS;
                        message.obj = resObj;
                        mHandler.sendMessage(message);


                    }
                }, cookie);


    }

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        //statusBarDarkFont 设置状态栏字体颜色
        ImmersionBar.with(this)
                .hideBar(BarHide.FLAG_SHOW_BAR)

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
        flVideoContainer = findViewById(R.id.flVideoContainer);
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

    private class MyWebChromeClient extends WebChromeClient {
        WebChromeClient.CustomViewCallback mCallback;
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.i("ToVmp","onShowCustomView");
            fullScreen();

            mWebview.setVisibility(View.GONE);
            flVideoContainer.setVisibility(View.VISIBLE);
            flVideoContainer.addView(view);
            mCallback = callback;

            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            Log.i("ToVmp","onHideCustomView");
            fullScreen();

            mWebview.setVisibility(View.VISIBLE);
            flVideoContainer.setVisibility(View.GONE);
            flVideoContainer.removeAllViews();

            super.onHideCustomView();

        }
    }

    private void showHide(){
        if(isFullScreen){

            ImmersionBar.with(this)
                    .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
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

        }else {


            ImmersionBar.with(this)
                    .hideBar(BarHide.FLAG_SHOW_BAR)

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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            Log.i("ToVmp","横屏");
            isFullScreen = true;

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.i("ToVmp","竖屏");
            isFullScreen = false;
        }
        showHide();
    }

    private void selectTeamLogo() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        function = FUNCTION_PICK_TEAM_LOGO;  //表示现在做选择图标的动作
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

    //目前只实现了选择一个图标
    private void selectHeadImage() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        function = FUNCTION_PICK_HEAD_IMAGE;  //表示现在做选择图标的动作
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

    //目前只实现了选择一个图标
    private void selectNewsIcon() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        function = FUNCTION_PICK_NEWS_ICON;  //表示现在做选择图标的动作
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

    private void selectNewsVideo(){
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        function = FUNCTION_PICK_NEWS_VIDEO;  //表示现在做选择视频的动作
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("video/*");
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

    private void selectNewsImg() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        function = FUNCTION_PICK_NEWS_IMAGE;  //表示现在做选择图片的动作
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

    private void selectSocialCamera() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(Permission.Group.CAMERA)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相机
                        function = FUNCTION_SOCIAL_CAMERA;  //表示现在做拍照的动作
                        openSysCamera();

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

    private void selectSocialAlbum() {
        AndPermission.with(WebActivity.this)
                .runtime()
                .permission(com.yanzhenjie.permission.Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //打开相册
                        function = FUNCTION_SOCIAL_ALBUM;  //表示现在做选择图片的动作
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
            case FUNCTION_PICK_NEWS_ICON:
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
            case 0:
                //相机返回
                if(resultCode == Activity.RESULT_OK){
                    //合同审核-拍照
                    try {
                        //压缩照片
                        FileInputStream fis = new FileInputStream(outputImage.getPath());
                        Bitmap bitmap  = BitmapFactory.decodeStream(fis);
                        Bitmap compressBitmap = ImageCompressL(bitmap);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputImage));
                        compressBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        mWebview.evaluateJavascript("javascript:addFile('"+ outputImage.getPath() +"')", null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }

    }

    private File outputImage;
    private Uri imageUri;

    private void openSysCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        long timeStamp = System.currentTimeMillis();

        int random = (int)(Math.random() * 1000);

        outputImage = new File(getExternalCacheDir(), timeStamp + "" + random + ".jpg");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //大于等于版本24（7.0）的场合
            imageUri = FileProvider.getUriForFile(WebActivity.this, "com.panxy.campustv.fileprovider", outputImage);
        } else {
            //小于android 版本7.0（24）的场合
            imageUri = Uri.fromFile(outputImage);
        }

        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //MediaStore.ACTION_IMAGE_CAPTURE = android.media.action.IMAGE_CAPTURE
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, 0);

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
                if(function == FUNCTION_PICK_NEWS_ICON
                        || function == FUNCTION_SOCIAL_ALBUM
                        || function == FUNCTION_PICK_TEAM_LOGO
                        || function == FUNCTION_PICK_HEAD_IMAGE
                        || function == FUNCTION_PICK_NEWS_IMAGE){
                    //Images
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                }else if(function == FUNCTION_PICK_NEWS_VIDEO){
                    //video
                    imagePath = getImagePath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, selection);
                }

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

    private void displayImage(String filePath) {
        if (filePath != null) {

            File file = new File(filePath);
            //需要加单引号
            String tempPath = "'"+ filePath +"'";
            String tempName = "'"+ file.getName() +"'";


            if(file.getPath().contains("/QQBrowser/")){
                //解决QQ浏览器文件显示不出来的方法，转存
                long timeStamp = System.currentTimeMillis();
                int random = (int)(Math.random() * 1000);
                String suffix = file.getName().substring(file.getName().lastIndexOf('.'));
                File temp = new File(getExternalCacheDir(), timeStamp + "" + random + suffix);
                if(FileUtils.copyFile(file, temp)){
                    tempPath = "'"+ temp.getPath() +"'";
                }else {
                    Toast.makeText(WebActivity.this,"文件读取失败 ",Toast.LENGTH_SHORT).show();
                    return;
                }

            }

            Bitmap.CompressFormat pic = null;
            switch (function){
                case FUNCTION_PICK_TEAM_LOGO:
                    if(file.getName().endsWith("jpg") || file.getName().endsWith("jpeg") ){
                        pic = Bitmap.CompressFormat.JPEG;
                    }else if(file.getName().endsWith("png")){
                        pic = Bitmap.CompressFormat.PNG;
                    }else {
                        Toast.makeText(WebActivity.this, "不支持该格式的图片，请重新选择", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mWebview.evaluateJavascript("javascript:changeTeamLogo("+ tempPath +")", null);

                    break;
                case FUNCTION_PICK_HEAD_IMAGE:

                    if(file.getName().endsWith("jpg") || file.getName().endsWith("jpeg") ){
                        pic = Bitmap.CompressFormat.JPEG;
                    }else if(file.getName().endsWith("png")){
                        pic = Bitmap.CompressFormat.PNG;
                    }else {
                        Toast.makeText(WebActivity.this, "不支持该格式的图片，请重新选择", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //todo 裁剪图片
                    //压缩图片
                    try{
                        long timeStamp = System.currentTimeMillis();
                        int random = (int)(Math.random() * 1000);
                        File temp = new File(getExternalCacheDir(), timeStamp + "" + random + ".jpg");
                        FileInputStream fis = new FileInputStream(filePath);
                        Bitmap bitmap  = BitmapFactory.decodeStream(fis);
                        Bitmap compressBitmap = ImageCompressL(bitmap);
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp));
                        compressBitmap.compress(pic, 100, bos);
                        bos.flush();
                        bos.close();
                        headImagePath = temp.getPath();
                        List<File> files = new ArrayList<>();
                        files.add(temp);
                        HttpUtil.doAsynchFileHttpPost(RequestUrl.UPLOAD_HEAD_IMAGE, files, null,
                                new ProgressListener() {
                                    @Override
                                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                                        int progress = (int) ((100 * currentBytes) / contentLength);
                                        Log.i("uploading", (100 * currentBytes) / contentLength + " % done ");
                                    }
                                }, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String res = response.body().string();
                                        JSONObject resObj = JSONObject.parseObject(res);
                                        Message message = mHandler.obtainMessage();
                                        message.what = Constant.REQUEST_HEAD_IMAGE_SUCCESS;
                                        message.obj = resObj;
                                        mHandler.sendMessage(message);

                                    }
                                }, cookie);
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    break;
                case FUNCTION_PICK_NEWS_ICON:
                    //调用获取本地相册，显示在h5上，支持jpg、png

                    if(file.getName().endsWith("jpg") || file.getName().endsWith("jpeg") ){
                        pic = Bitmap.CompressFormat.JPEG;
                    }else if(file.getName().endsWith("png")){
                        pic = Bitmap.CompressFormat.PNG;
                    }else {
                        Toast.makeText(WebActivity.this, "不支持该格式的图片，请重新选择", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    iconRealPath = filePath;
                    mWebview.evaluateJavascript("javascript:changeIcon("+ tempPath +")", null);
                    break;
                case FUNCTION_PICK_NEWS_VIDEO:
                    videoRealPath = filePath;
                    mWebview.evaluateJavascript(
                            "javascript:changeVideo("+ tempName +")", null);
                    break;
                case FUNCTION_PICK_NEWS_IMAGE:
                    imageRealPath = filePath;
                    mWebview.evaluateJavascript(
                            "javascript:addImg("+ tempPath +")", null);
                    break;
                case FUNCTION_SOCIAL_ALBUM:
                    imageRealPath = filePath;
                    mWebview.evaluateJavascript(
                            "javascript:addFile("+ tempPath +")", null);
                    break;
            }

        } else {
            Toast.makeText(WebActivity.this, "获取系统资源失败", Toast.LENGTH_SHORT).show();
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
            //首先判断是否是全屏，调用取消全屏
            if(isFullScreen){
                mWebview.evaluateJavascript("javascript:exitFullscreen()", null);

                return true;
            }
            //判断是否是在新闻编辑页,调用编辑页的返回逻辑
            if(mWebview.getUrl().contains("uCreateNews/uCreateNews")){
                mWebview.evaluateJavascript("javascript:appback()", null);
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
