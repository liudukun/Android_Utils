package com.liudukun.dkchat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ShapeUtil {



    public static Drawable shape(Context context, int corner, int argb){
        return shape(context,corner,argb,false);
    }

    public static Drawable shape(Context context, int corner, int color,boolean resId){
        return shape(context,corner,color,false,0, resId);
    }

    public static Drawable shape(Context context, int corner, int color,boolean border,int borderColor,boolean resId){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setCornerRadius(corner);
        if (border) {
            if (resId) {
                drawable.setStroke(ScreenUtil.getPxByDp(1), context.getResources().getColor(borderColor));
            }else{
                drawable.setStroke(ScreenUtil.getPxByDp(1), borderColor);
            }
        }
        if (resId) {
            drawable.setColor(context.getResources().getColor(color));
        }else{
            drawable.setColor(color);
        }
        return drawable;
    }
}
