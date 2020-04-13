package com.panxy.campustv.web.utils;

import com.alibaba.fastjson.JSON;
import com.panxy.campustv.web.entity.NewsListsWithBLOBs;
import com.panxy.campustv.web.utils.uploadlibrary.helper.ProgressHelper;
import com.panxy.campustv.web.utils.uploadlibrary.listener.ProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    /**
     * 单例 okHttpClient
     */
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
//    public static void doAsynchFileHttpPost(String url, List<File> files, ProgressListener uiProgressRequestListener, Callback callback, String cookie) {
//        OkHttpClient currentOkHttpClient = okHttpClient.newBuilder().connectTimeout(20000, TimeUnit.SECONDS).build();
//
//        currentOkHttpClient.newCall(getRequest(url, files,null, uiProgressRequestListener, cookie)).enqueue(callback);
//
//    }

    public static void doAsynchFileHttpPost(String url, List<File> files, Object object, ProgressListener uiProgressRequestListener, Callback callback, String cookie) {
        OkHttpClient currentOkHttpClient = okHttpClient.newBuilder().connectTimeout(20000, TimeUnit.SECONDS).build();

        currentOkHttpClient.newCall(getRequest(url, files,object, uiProgressRequestListener, cookie)).enqueue(callback);

    }
    private static Request getRequest(String url, List<File> files, Object object, ProgressListener uiProgressRequestListener, String cookie) {
        Request.Builder builder = new Request.Builder();
        builder.url(url).addHeader("user_cookie", cookie)
                .post(ProgressHelper.addProgressRequestListener(
                        getRequestBody(files,object,cookie),
                        uiProgressRequestListener));
        return builder.build();
    }


    private static RequestBody getRequestBody(List<File> files, Object object, String cookie) {
        //创建MultipartBody.Builder，用于添加请求的数据
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //builder.addPart(RequestBody.create(null, object));
        for (int i = 0; i < files.size(); i++) { //对文件进行遍历
            File file = files.get(i);
            //根据文件的后缀名，获得文件类型
            String fileType = getMimeType(file.getName());
            String filename = null;
            try {
                filename = URLEncoder.encode(file.getName(),"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            builder.addFormDataPart( //给Builder添加上传的文件
                    "files",  //请求的名字
                    filename, //文件的文字，服务器端用来解析的
                    RequestBody.create(MediaType.parse(fileType), file) //创建RequestBody，把上传的文件放入
            );
        }
        if(object != null){
            if(object instanceof NewsListsWithBLOBs){
                builder.addFormDataPart("news_info", JSON.toJSONString((NewsListsWithBLOBs)object));
            }else if(object instanceof String){
                builder.addFormDataPart("body", (String) object);
            }

        }

        builder.addFormDataPart("cookie", cookie);

        return builder.build(); //根据Builder创建请求
    }
    /**
     * 获取文件MimeType
     *
     * @param filename 文件名
     * @return
     */
    private static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            contentType = "application/octet-stream"; //* exe,所有的可执行程序
        }
        return contentType;
    }
}
