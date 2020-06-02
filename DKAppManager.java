package com.liudukun.dkchat.manager;

import android.app.Activity;

import java.util.Stack;
import android.content.Context;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.liudukun.dkchat.DKApplication;
import com.liudukun.dkchat.base.BaseFragment;
import com.liudukun.dkchat.utils.DKLog;


public class DKAppManager {
    private static Stack<Activity> activityStack;
    private static DKAppManager instance;

    private DKAppManager(){

    }

    public static DKAppManager shareInstance(){
        if (instance == null){
            instance = new DKAppManager();
        }
        return instance;
    }

    public void showActivity(Class cl,int resultCode) {
        showActivity(cl,resultCode, null, false);
    }

    public void showActivity(Class cl){
        showActivity(cl,0,null,false);
    }

    public void showActivity(Class cl,int resultCode, Bundle bundle){
        showActivity(cl,resultCode,bundle,false);
    }

    public void showActivity(Class cl,Bundle bundle, boolean onlyOne){
        showActivity(cl,0,bundle,false);
    }

    public void showActivity(Class cl, int resultCode, Bundle bundle, boolean onlyOne){
        showActivity(cl,resultCode,bundle,onlyOne,null);

    }

    public void showActivity(Class cl, int resultCode, Bundle bundle, boolean onlyOne, BaseFragment fragment){
        if (onlyOne){
            finishActivity(cl);
        }
        Intent intent = new Intent(currentActivity(),cl);
        if (bundle ==  null){
            bundle = new Bundle();
        }
        if (fragment!= null){
            fragment.startActivityForResult(intent,resultCode,bundle);
        }else {
            currentActivity().startActivityForResult(intent, resultCode, bundle);
        }
    }


    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity){
        if (activityStack == null){
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity(){
        for (int i = activityStack.size() - 1, size = activityStack.size(); i >=0; i--){
            if (null != activityStack.get(i) && !activityStack.get(i).isFinishing()) {
                return activityStack.get(i);
            }
        }
        return activityStack.lastElement();
    }

    public void finishActivity(){
        Activity activity=activityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(int result){
        Intent intent = new Intent();
        Activity activity=activityStack.lastElement();
        finishActivity(intent,result,activity);
    }

    public void finishActivity(Intent intent,int result){
        Activity activity=activityStack.lastElement();
        finishActivity(intent,result,activity);
    }

    public void finishActivity(Activity activity){
        finishActivity(null,0,activity);
    }

    public void finishActivity(Intent intent,int result,Activity activity){
        if(activity!=null){
            if (intent!=null) {
                activity.setResult(result, intent);
            }
            activity.finish();
            activityStack.remove(activity);
        }else{
            DKLog.d("activity is null");
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls){
        for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)) {
                if (activityStack.get(i).getClass().equals(cls)) {
                    Activity activity = activityStack.get(i);
                    finishActivity(activity);
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){
        for (int i = 0, size = activityStack.size(); i < size; i++){
            if (null != activityStack.get(i)){
                finishActivity(activityStack.get(i));
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     * 这里关闭的是所有的Activity，没有关闭Activity之外的其他组件;
     * android.os.Process.killProcess(android.os.Process.myPid())
     * 杀死进程关闭了整个应用的所有资源，有时候是不合理的，通常是用
     * 堆栈管理Activity;System.exit(0)杀死了整个进程，这时候活动所占的
     * 资源也会被释放,它会执行所有通过Runtime.addShutdownHook注册的shutdown hooks.
     * 它能有效的释放JVM之外的资源,执行清除任务，运行相关的finalizer方法终结对象，
     * 而finish只是退出了Activity。
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            //DalvikVM的本地方法
            // 杀死该应用进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
            //这些方法如果是放到主Activity就可以退出应用，如果不是主Activity
            //就是退出当前的Activity
        } catch (Exception e) {
        }
    }



}
