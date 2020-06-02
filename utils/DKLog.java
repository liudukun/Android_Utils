package com.liudukun.dkchat.utils;

import android.content.Context;
import android.util.Log;

import com.liudukun.dkchat.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Author： zhangjianlong
 * Date： 2017/6/20
 * E-Mail：m13478943650@163.com
 * Desc：
 */

public class DKLog {
    static String className;//类名
    static String content;
    static String methodName;//方法名
    static int lineNumber;//行数
    private String mPID;

    private LogDumper mLogDumper;
    private String PATH_LOGCAT;
    private static DKLog instance;

    public static DKLog getInstance(){
        if (instance==null){
            instance = new DKLog();
        }
        return instance;
    }

    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("  \n."+methodName);
        buffer.append("(").append(content).append(":").append(lineNumber).append(")");
        buffer.append(log);
        return buffer.toString();
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        content = sElements[1].getFileName();
        className = sElements[1].getClassName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (!isDebuggable())
            return;
        // Throwable instance must be created before any methods
        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message));
    }

    public static void i(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(message));
    }

    public static void d(String message,String...values) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < values.length; i++) {
            value.append(values[i]);
            if (i == values.length - 1) {
                break;
            }
            value.append(", ");
        }
        Log.d(className, createLog(message+ value.toString()));
    }

    public static void v(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(message));
    }

    public static void w(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.w(className, createLog(message));
    }

    public static void wtf(String message) {
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(className, createLog(message));
    }


    public void initSaveLogs(Context context) {
        File cacheFile = context.getExternalCacheDir();
        if (cacheFile.getAbsolutePath()==null) return;
        PATH_LOGCAT = cacheFile.getAbsolutePath();
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
            DKLog.d("创建文件夹");
        }
        DKLog.d(PATH_LOGCAT);
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(android.os.Process.myPid()), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;
        private String file = "debug" + ".log";

        public LogDumper(String pid, String dir) {
            mPID = pid;

            try {
                out = new FileOutputStream(new File(dir, file));

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            /**
             *
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
             *
             * 显示当前mPID程序的 E和W等级的日志.
             *
             * */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(
                        logcatProc.getInputStream()), 1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        out.write((line + "\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }






}
