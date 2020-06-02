package com.liudukun.dkchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSONObject;
import com.liudukun.dkchat.config.Constant;
import com.liudukun.dkchat.DKApplication;
import com.liudukun.dkchat.callBack.DKHttpCallBack;
import com.liudukun.dkchat.model.DKBaseResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DKHttpUtil {
    private static OkHttpClient okHttpClient;
    private volatile static DKHttpUtil instance;//防止多个线程同时访问
    //提交json数据
    private static final MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
    //提交字符串数据
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown;charset=utf-8");
    private static String responseStrGETAsyn;

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    // 使用getCacheDir()来作为缓存文件的存放路径（/data/data/包名/cache） ，
// 如果你想看到缓存文件可以临时使用 getExternalCacheDir()（/sdcard/Android/data/包名/cache）。
    private static File cacheFile;
    private static Cache cache;
    public static DKHttpUtil sharedInstance(){
        synchronized (DKHttpUtil.class) {
            if (instance == null) {
                instance = new DKHttpUtil();
            }
        }
        return instance;
    }



    public DKHttpUtil() {
//        if (APP.getInstance().getApplicationContext().getCacheDir()!=null){
//            cacheFile = new File(APP.getInstance().getCacheDir(), "Test");
//            cache = new Cache(cacheFile, 1024 * 1024 * 10);
//        }

        okHttpClient = new OkHttpClient();
        okHttpClient.newBuilder()
//                .addInterceptor(new HeaderInterceptor())
//                .addNetworkInterceptor(new CacheInterceptor())
//                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
//自动管理Cookie发送Request都不用管Cookie这个参数也不用去response获取新Cookie什么的了。还能通过cookieStore获取当前保存的Cookie。
                    }
                });
    }
    /**
     * post异步请求map传参
     * 通过response.body().string()获取返回的字符串
     * 异步返回值不能更新UI，要开启新线程
     *
     * @param url
     * @return
     */
    public String postAsynchronousRequest(String url, HashMap params, final DKHttpCallBack dataCallBack) {
        RequestBody requestBody;
        if (params == null) {
            params = new HashMap<>();
        }

        FormBody.Builder builder = new FormBody.Builder();
        String version = VersionUtil.getVersionName(DKApplication.sharedInstance().getApplicationContext());
        SharedPreferences sp = DKApplication.sharedInstance().getSharedPreferences("DKChat", Context.MODE_PRIVATE);
        String accessToken =  sp.getString("accessToken","");
        params.put("version",version);
        params.put("is_green","1");
        params.put("device_type","android");
        params.put("accessToken",accessToken);
        /**
         * 在这对添加的参数进行遍历
         */
        addMapParmsToFromBody(params, builder);
        requestBody = builder.build();

        String realURL = urlJoint(url, null);
        //结果返回
        final Request request = new Request.Builder()
                .url(realURL)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            DKLog.d("http:Start POST",url+": Params: "+params.toString());

            // 请求加入调度
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    dataCallBack.requestCompleted(null,request,1,"request fail");
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        responseStrGETAsyn = response.body().string();//此处也可以解析为byte[],Reader,InputStream
                        DKBaseResponse res =  JSONObject.parseObject(responseStrGETAsyn,DKBaseResponse.class);
                        dataCallBack.requestCompleted(res,request,res.getCode(),res.getMsg());
                        DKLog.d(url + " : " ,responseStrGETAsyn);

                    } catch (IOException e) {
                        e.printStackTrace();
                        dataCallBack.requestCompleted(null,request,1,e.getMessage());
                    }

                }
            });

        } catch (Exception e) {
            DKLog.d("request error" + e.toString());
            e.printStackTrace();
            dataCallBack.requestCompleted(null,request,1,"request queue error");

        }
        return responseStrGETAsyn;
    }

    private void addMapParmsToFromBody(HashMap params, FormBody.Builder builder) {
        for (Object key : params.keySet()) {

            Object value = params.get(key);;
            /**
             * 判断值是否是空的
             */
            if (value == null) {
                value = "";
            }

            builder.add(key.toString(), value.toString());
        }
    }
    /**
     * @param url    实际URL的path
     * @param params
     * @return
     */
    private static String urlJoint(String url, Map<String, String> params) {
        StringBuilder realURL = new StringBuilder(Constant.URL);
        realURL = realURL.append(url);
        boolean isFirst = true;
        if (params == null) {
            params = new HashMap<>();
        } else {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                if (isFirst && !url.contains("?")) {
                    isFirst = false;
                    realURL.append("?");
                } else {
                    realURL.append("&");
                }
                realURL.append(entry.getKey());
                realURL.append("=");
                if (entry.getValue() == null) {
                    realURL.append(" ");
                } else {
                    realURL.append(entry.getValue());
                }

            }
        }
        return realURL.toString();
    }

    public void downloadFile(String url,String path,DKDownloadCompletedCallback cb){
        final long startTime = System.currentTimeMillis();
        DKLog.d("TAG","startTime="+startTime);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                DKLog.d("TAG","download failed");
                cb.downloadCompleted("",1,e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = path;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        DKLog.d("TAG","download progress : " + progress);
                        cb.downloadProgress(progress);
                    }
                    fos.flush();
                    DKLog.d("TAG","download success");
                    DKLog.d("TAG","totalTime="+ (System.currentTimeMillis() - startTime));
                    cb.downloadCompleted(savePath,0,"ss");
                } catch (Exception e) {
                    e.printStackTrace();
                    DKLog.d("TAG","download failed : "+e.getMessage());
                    cb.downloadCompleted("",1,e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    public String uploadRequest(String url,HashMap params,List<String> images,List<String> movies,DKHttpCallBack dataCallBack){

        if (params == null) {
            params = new HashMap<>();
        }
        String version = VersionUtil.getVersionName(DKApplication.sharedInstance().getApplicationContext());
        SharedPreferences sp = DKApplication.sharedInstance().getSharedPreferences("DKChat", Context.MODE_PRIVATE);
        String accessToken =  sp.getString("accessToken","");
        params.put("version",version);
        params.put("is_green","1");
        params.put("accessToken",accessToken);

        MultipartBody.Builder multiBuiler = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Object key: params.keySet()){
            String k = (String) key;
            String v = params.get(key).toString();
            multiBuiler.addFormDataPart(k,v);
        }

//        multiBuiler.addPart(paramsBody);
        MediaType mediaType;
        if (images!=null && !images.isEmpty()) {
            mediaType = MediaType.parse("image/png");
            for (String image : images) {
                File file = new File(image);
                RequestBody request = RequestBody.create(file, mediaType);
                multiBuiler.addFormDataPart(file.getName(), file.getName(), request);
                multiBuiler.addPart(request);
            }
        }
        if (movies!=null && !movies.isEmpty()) {
            mediaType = MediaType.parse("audio/mpeg");
            for (String movie : movies) {
                File file = new File(movie);
                RequestBody request = RequestBody.create(file, mediaType);
                multiBuiler.addFormDataPart(file.getName(), file.getName(), request);
                multiBuiler.addPart(request);
            }
        }
        RequestBody body = multiBuiler.build();


        String realURL = urlJoint(url, null);
        //结果返回
        final Request request = new Request.Builder()
                .url(realURL)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            DKLog.d("http:Start POST",realURL+"\nParams:"+params.toString());

            // 请求加入调度
            call.enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    dataCallBack.requestCompleted(null,request,1,"request fail");
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        responseStrGETAsyn = response.body().string();//此处也可以解析为byte[],Reader,InputStream
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    DKLog.d("http:Response ",responseStrGETAsyn);

                    try {
                        DKBaseResponse res =  JSONObject.parseObject(responseStrGETAsyn,DKBaseResponse.class);
                        dataCallBack.requestCompleted(res,request,res.getCode(),res.getMsg());

                    } catch (Exception e) {
                        e.printStackTrace();
//                        DKLog.d("POST异步请求JSON解析失败" + e.toString());
                        dataCallBack.requestCompleted(null,request,1,e.getMessage());
                    }

                }
            });

        } catch (Exception e) {
            DKLog.d("request error" + e.toString());
            e.printStackTrace();
            dataCallBack.requestCompleted(null,request,1,"request queue error");

        }
        return responseStrGETAsyn;
    }

    public interface DKDownloadCompletedCallback{
        void downloadCompleted(String path,int code,String msg);
        void downloadProgress(int progress);

    }

}
