# SmartLogger
logger for android
本文对Android中日志打印工具常见的封装方案做一个简单介绍。
## 方案一
#### 适用场景：
1，日志文件不需要打印到存储卡。
2，尽可能少的代码实现功能，一个类搞定做好。
3，可控制日志的开关。
4，打印进程和线程号，便于分析。
5，打印应用的TAG和调用接口的TAG
#### 代码片段：
```
import android.util.Log;
/**
 * 日志打印，统一标签，打印进程和线程号
 * Created by waynesonic
 */
public class SLog {
    public static final String LOG_TAG = "[APPTAG]";
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
```
#### 小结：
上面代码就基本可以满足我们日志封装的简单需求了。如果需要打印json可以直接在这个类里面增加接口实现。

## 方案二
#### 适用场景：
1，在满足方案一的条件的同时，可以打印到存储卡，并且是否打印到存储卡可优雅控制。
2，当日志文件超过1M时自动打印到另外的文件中。最多不超过5个文件。大于5个时覆盖掉最早的那个文件。
#### 如何使用：
在Application中初始化日志的配置信息
```
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
```
在需要的打印日志的位置调用：
```
 Log.info("MainActivity", "MainActivity onCreate")
```
#### 源码解析：
主要类的依赖关系如下图所示，主要设计思想可以概况为下面几点：
1，通过实现Appender接口，来实现向文件还是内存还是Android控制台写日志。
2，LogImpl依赖Appender接口，而不是具体类。
3，muilt既是Appender的子类又持有Appender对象的列表，便于我们同时增加多个Appender。
4，通过Log类来统一提供对外的接口。

![smartLog.png](https://upload-images.jianshu.io/upload_images/1050164-4812bdc843a010f9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 其他优秀开关代码：
https://github.com/orhanobut/logger
