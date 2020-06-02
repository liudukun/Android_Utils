package com.liudukun.dkchat.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.liudukun.dkchat.DKApplication;
import com.liudukun.dkchat.R;
import com.liudukun.dkchat.manager.DKAppManager;


/**
 * UI通用方法类
 */
public class ToastUtil {

    private static Toast mToast;

    public static final void toastLongMessage(final String message) {
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(DKApplication.sharedInstance(), message,
                        Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }


    public static final void toastShortMessage(final String message) {
        BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast != null) {
                    mToast.cancel();
                    mToast = null;
                }
                mToast = Toast.makeText(DKApplication.sharedInstance(), message,
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    static public ConstraintLayout showHud(){
        return ToastUtil.showHud("");
    }

    static public HudView showHud(String message){
        return ToastUtil.showHud("",true);
    }

    static public HudView showHud(boolean autoHide){
        return ToastUtil.showHud("",autoHide);
    }

    static public HudView showHud(String message,boolean autoHide){
        return ToastUtil.showHud("",true, DKAppManager.shareInstance().currentActivity());
    }

    static public HudView showHud(String message, boolean autoHide, Activity context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        HudView layout =  new HudView(context);

        layout.titleView.setText(message);
        ViewGroup group = (ViewGroup) context.findViewById(android.R.id.content);
        group.addView(layout);



        if (autoHide){
            hideHud(layout,true);
        }
        return layout;
    }

    static public void hideHud(ConstraintLayout layout) {
        hideHud(layout,DKAppManager.shareInstance().currentActivity(),false,null);
    }

    static public void hideHud(ConstraintLayout layout,boolean delay) {
        hideHud(layout,DKAppManager.shareInstance().currentActivity(),delay,null);
    }

    static public void hideHud(ConstraintLayout layout, Activity context,boolean delay,DelayCompleted cp) {
        long time = 0;
        if (delay){
            time = 2 * 1000;
        }
        layout.postDelayed(new Runnable() {
            @Override
            public void run() {
                BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layout.clearAnimation();
                        ViewGroup group = (ViewGroup) context.findViewById(android.R.id.content);
                        group.removeView(layout);
                        if (cp!=null) {
                            cp.completed();
                        }
                    }
                });
            }
        },time);


    }

    public interface DelayCompleted{
        void completed();
    }

    public static class HudView extends ConstraintLayout{
        TextView titleView;
        ImageView imageView;

        public HudView(Context context) {
            super(context);
            init();
        }

        public HudView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();

        }

        public HudView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();

        }


        public void init(){
            inflate(getContext(),R.layout.hud,this);
            titleView = findViewById(R.id.titleView);
            imageView = findViewById(R.id.faceView);

            // run
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.8f,1.f);
            valueAnimator.setDuration(1000);
            valueAnimator.setRepeatCount(-1);
            valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float)animation.getAnimatedValue();
                    imageView.setScaleX(value);
                    imageView.setScaleY(value);
                }
            });
            valueAnimator.start();
        }

        public void setTitle(String title) {
            if (title==null){
                title="";
            }
            titleView.setText(title);
        }

        public void hide(Boolean delay){
            ToastUtil.hideHud(this,delay);
        }
        public void hide(){
            ToastUtil.hideHud(this,false);
        }
    }

}
