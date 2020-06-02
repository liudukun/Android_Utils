package com.liudukun.dkchat.callBack;

import com.liudukun.dkchat.model.DKBaseResponse;

import okhttp3.Request;

public abstract class DKHttpCallBack<T> {

    public abstract void requestCompleted(DKBaseResponse res,Request request,int code,String msg);
}
