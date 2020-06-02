package com.liudukun.dkchat;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import androidx.multidex.MultiDex;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.liudukun.dkchat.config.Constant;
import com.liudukun.dkchat.manager.DKThemeManager;
import com.liudukun.dkchat.manager.DKUserManager;
import com.liudukun.dkchat.utils.BackgroundTasks;
import com.liudukun.dkchat.utils.ImageUtil;
import com.liudukun.dkchat.utils.StringUtil;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.*;
import com.tencent.imsdk.session.SessionWrapper;

import java.io.File;

public class DKApplication extends Application {
    public static Context context;
    private static DKApplication instance;
    private DisplayMetrics displayMetrics = null;
    private String TAG;

    public static DKApplication sharedInstance() {
        if (instance != null && instance instanceof DKApplication) {
            return (DKApplication) instance;
        } else {
            instance = new DKApplication();
            instance.onCreate();
            return (DKApplication) instance;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        instance = this;
        TAG = this.getClass().getSimpleName();

        initIMSDK();
        DKUserManager.sharedInstance();
        initCacheDirectory();
        initCommonImage();
        BackgroundTasks.initInstance();
        DKThemeManager.shareInstance();
        MultiDex.install(this);
        initGoogleAd();
    }

    void initGoogleAd(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
    }

    private void initCommonImage(){
        ImageUtil.initInstance();
    }

    private void initCacheDirectory(){
        String path = StringUtil.getMediaCacheDirectory();
        File file = new File(path);
        if(file.exists()){
            return;
        }
        file.mkdir();
    }

    private void initIMSDK() {
        //判断是否是在主线程
        if (SessionWrapper.isMainProcess(getApplicationContext())) {
            TIMSdkConfig sdkConfig = new TIMSdkConfig(Constant.IM_SDK_APP_ID_Int);
            sdkConfig.setLogLevel(Constant.logLevel);
            sdkConfig.enableLogPrint(Constant.enableLogPrint);

            TIMManager.getInstance().init(this, sdkConfig);

            TIMUserConfig userConfig = new TIMUserConfig();
            userConfig.setReadReceiptEnabled(false);
            TIMManager.getInstance().setUserConfig(userConfig);
            BackgroundTasks.initInstance();

        }

    }



}
