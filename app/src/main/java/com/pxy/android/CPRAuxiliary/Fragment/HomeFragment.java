package com.pxy.android.CPRAuxiliary.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.pxy.android.CPRAuxiliary.R;
import com.pxy.android.CPRAuxiliary.web.JsInteration;
import com.pxy.android.CPRAuxiliary.web.MyWebChromeClient;
import com.pxy.android.CPRAuxiliary.web.MyWebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeFragment extends Fragment {

    private final String TAG = "HomeFragment";

    @BindView(R.id.web_home)
    WebView web_home;

    @OnClick(R.id.bt_js)
    public void bt_js(){
        web_home.evaluateJavascript("javascript:androidCallReturn()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Log.e(TAG, "onReceiveValue: " + value );
            }
        });
        web_home.evaluateJavascript("javascript:androidCallJs('你好')",null);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_fragment,container,false);
        ButterKnife.bind(this,view);
        initWebview();
        return view;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String content = (String) msg.obj;
                    if(content!=null){
                        Log.e(TAG, content);
                        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }
    };

    private void initWebview() {
        //是否支持调试
        web_home.setWebContentsDebuggingEnabled(true);
        // 设置加载文件的格式
        web_home.getSettings().setDefaultTextEncodingName("UTF-8");
        // 设置支持javascript
        web_home.getSettings().setJavaScriptEnabled(true);

        web_home.getSettings().setSavePassword(false);
        //手机字体放大，app不受影响
        web_home.getSettings().setTextZoom(100);
        web_home.addJavascriptInterface(new JsInteration(mHandler), "androidJs");
        // 屏蔽复制事件
        web_home.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        // 启用硬件加速
        web_home.getSettings().setPluginState(WebSettings.PluginState.ON);
        // 设置支持缩放
        web_home.getSettings().setSupportZoom(false); // 设置是否支持缩放
        web_home.getSettings().setBuiltInZoomControls(true); // 设置是否显示内建缩放工具
        //设置本地缓存
        web_home.getSettings().setDomStorageEnabled(true);
        // webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        // 设置允许访问文件
        web_home.getSettings().setAllowFileAccess(false);
        //允许webview对文件的操作
        web_home.getSettings().setAllowUniversalAccessFromFileURLs(true);
        web_home.getSettings().setAllowFileAccessFromFileURLs(true);
        // 设置Web视图
        web_home.setWebViewClient(new MyWebViewClient(mHandler));
        // 设置下载监听
        // 设置缩放
        web_home.setBackgroundColor(0);
        web_home.setWebChromeClient(new MyWebChromeClient());
        web_home.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        web_home.loadUrl("http://www.complicated.top/web/dist/m_index.html#/");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
