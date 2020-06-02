package com.liudukun.dkchat.utils;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.text.TextUtilsCompat;

import com.alibaba.fastjson.JSON;

import com.liudukun.dkchat.DKApplication;
import com.liudukun.dkchat.manager.DKThemeManager;
import com.liudukun.dkchat.model.DKUser;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.luck.picture.lib.tools.StringUtils;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

public class StringUtil {
    public static String DKServerDomain = "http://liudukun.com/";

    public static String DKCacheDirectory = "dk_chat_cache";


    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getCacheDirectory() {
        String path = DKApplication.sharedInstance().getCacheDir().getAbsolutePath();
        return path;
    }

    public static String getExternalCacheDirectory() {
        String path = DKApplication.sharedInstance().getExternalCacheDir().getAbsolutePath();
        return path;
    }

   


    public static boolean isNumeric(String string) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(string).matches();
    }

    public static boolean isBlank(String string){
        if (isEmpty(string)){
            return true;
        }
        for (int i = 0;i < string.length();i++){
            char c = string.charAt(i);
            if (c != ' '){
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(String string){
        return TextUtils.isEmpty(string);
    }

    public static String componentsJoinedByString(List<String>strings,String join){
        String string = "";
        for (int i =0 ;i < strings.size();i++){
            string = string + strings.get(i);
            if (i < strings.size() - 1){
                string = string + join;
            }
        }
        return string;
    }


}
