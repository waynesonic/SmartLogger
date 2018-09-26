package com.android.common.test;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.android.common.logger.AndroidLogAppender;
import com.android.common.logger.FileAppender;
import com.android.common.logger.Log;
import com.android.common.logger.MultipleAppender;

/**
 * @author weidong1-os
 * @name SmartLogger
 * @class name：com.android.common.logger
 * @class describe
 * @time 2018/9/26 14:42
 * @class describe
 */
public class SmartLoggerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        intLogAppender(getApplicationContext());
    }

    private static void intLogAppender(Context context) {

        MultipleAppender ma = new MultipleAppender();

        //初始化打印日志到文件的相关配置
        String fileName = "smartLogger";//文件名称
        String userDir = Environment.getExternalStorageDirectory().getAbsolutePath(); //文件存储路径
        int maxSize = 1012 * 1024;//单个文件最大容量
        FileAppender fileAppender = new FileAppender(userDir, fileName);
        fileAppender.setLogContentType(true);
        fileAppender.setMaxFileSize(maxSize);
        ma.addAppender(fileAppender);

        //初始化android自带的日志打印
        AndroidLogAppender androidLogAppender = new AndroidLogAppender();
        ma.addAppender(androidLogAppender);

        //加载配置，并设置日志级别为INFO
        Log.initLog("smartLoggerTest", ma, Log.INFO);
    }
}
