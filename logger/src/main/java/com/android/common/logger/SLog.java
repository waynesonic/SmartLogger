package com.android.common.logger;

import android.util.Log;

/**
 * 简单封装，不依赖于其他类。
 * 日志打印，统一标签，打印进程和线程号
 */

public class SLog {
    public static final String LOG_TAG = "[SLog]";
    private static boolean isDebug = true;

    private static boolean isDebug() {
        return isDebug;
    }

    public static void v(String strTag, String msg) {
        if (isDebug()) {
            Log.v(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + msg);
        }
    }

    public static void d(String strTag, String msg) {
        if (isDebug()) {
            Log.d(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + msg);
        }

    }

    public static void i(String strTag, String msg) {
        if (isDebug()) {
            Log.i(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + msg);
        }
    }

    public static void w(String strTag, String msg) {
        if (isDebug()) {
            Log.w(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + msg);
        }
    }

    public static void e(String strTag, String msg) {
        if (isDebug()) {
            Log.e(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + msg);
        }
    }

    public static void e(String strTag, String msg, Throwable e) {
        if (isDebug()) {
            Log.e(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + msg, e);
        }
    }

    public static void e(String strTag, Throwable e) {
        if (isDebug()) {
            Log.e(LOG_TAG, "[Pid:" + android.os.Process.myPid() + "][Tid:" + android.os.Process.myTid() + "]" + "[" + strTag + "] " + (e != null ? e.getMessage() : " null msg"), e);
        }
    }

    public static void setIsDebug(boolean params) {
        isDebug = params;
    }


}
