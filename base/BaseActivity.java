package com.liudukun.dkchat.base;
import com.liudukun.dkchat.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.liudukun.dkchat.manager.DKAppManager;

public class BaseActivity extends Activity {
    public String TAG  ;
    public NavigationBar navigationBar;
    public View rootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TAG = this.getClass().getSimpleName();
//        DKLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        DKAppManager.shareInstance().addActivity(this);
        rootView= findViewById(android.R.id.content);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
            int vis = getWindow().getDecorView().getSystemUiVisibility();
            vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            getWindow().getDecorView().setSystemUiVisibility(vis);
        }



    }

    @Override
    protected void onStart() {
//        DKLog.d(TAG, "onStart");
        super.onStart();

    }

    @Override
    protected void onResume() {
//        DKLog.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
//        DKLog.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
//        DKLog.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        DKLog.d(TAG, "onDestroy");
        super.onDestroy();
        DKAppManager.shareInstance().finishActivity(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
//        DKLog.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
    }

    public void setTitle(String title){
        navigationBar.setTitle(title);
    }

}
