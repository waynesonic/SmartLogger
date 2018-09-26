package com.android.common.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.io.IOException;

/**
 * Generic Log class
 */
public class LogImpl {
    
    //---------------------------------------------------------------- Constants
    /**
     * Log level DISABLED: used to speed up applications using logging features
     */
    private  final int DISABLED = -1;
    
    /**
     * Log level ERROR: used to log error messages.
     */
    private  final int ERROR = 0;

    /**
     * Log level WARN: used to log warn messages.
     */
    private  final int WARN = 1;
    
    /**
     * Log level INFO: used to log information messages.
     */
    private  final int INFO = 2;
    
    /**
     * Log level DEBUG: used to log debug messages.
     */
    private  final int DEBUG = 3;
    
    /**
     * Log level TRACE: used to trace the program execution.
     */
    private  final int TRACE = 4;
    
    
    private  final int PROFILING = -2;
    
    //---------------------------------------------------------------- Variables
    /**
     * The default appender is the console
     */
    private  Appender out;
    
    /**
     * The default log level is INFO
     */
    private  int level = INFO;
    
    /**
     * Last time stamp used to dump profiling information
     */
    private  long initialTimeStamp = -1;

    /**
     * Default log cache size
     */
    private  final int CACHE_SIZE = 1024;

    /**
     * This is the log cache size (by default this is CACHE_SIZE)
     */
    private  int cacheSize = CACHE_SIZE;

    /**
     * Log cache
     */
	private  Vector<String> cache = null;

    /**
     * Tail pointer in the log cache
     */
    private  int next = 0;

    /**
     * Head pointer in the log cache
     */
    private  int first = 0;

    /**
     * Controls the context logging feature
     */
    private  boolean contextLogging = false;

    /**
     * The client max supported log level. This is only needed for more accurate context
     * logging behavior and the client filters log statements.
     */
    private  int clientMaxLogLevel = TRACE;

    private  boolean lockedLogLevel;

    /**
     *  Application TAG
     */
    public static String mAppTag = "smartLogger";
    
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
    		Locale.getDefault());
    
    //------------------------------------------------------------- Constructors
    /**
     * This class is  and cannot be intantiated
     */
    public LogImpl(){
    }

    
    //----------------------------------------------------------- Public methods
    /**
     * Initialize log file with a specific appender and log level. Contextual
     * errors handling is disabled after this call.
     * @param appTAG the application tag
     * @param object the appender object that write log file
     * @param level the log level
     */
	public synchronized  void initLog(String appTAG, Appender object, int level){
        mAppTag = appTAG;
        out = object;
        out.initLogFile();
        // Init the caching part
        cache = new Vector<String>(cacheSize);
        first = 0;
        next  = 0;
        contextLogging = false;
        lockedLogLevel = false;
        setLogLevel(level);
        if (level > Log.DISABLED) {
            writeLogMessage(getCaller(new Throwable()), level, getLevelMsg(level), "init logger >>>>>>>>>");
        }
    }
    
    private  String getLevelMsg(int level) {
    	String levelMsg = "";
    	
    	if (level == Log.DEBUG) {
			levelMsg = "DEBUG";
		} else if (level == Log.INFO) {
			levelMsg = "INFO";
		} else if (level == Log.ERROR) {
			levelMsg = "ERROR";
		} else if (level == Log.TRACE) {
			levelMsg = "TRACE";
		} else if (level == Log.WARN) {
			levelMsg = "WARN";
		} else {
			levelMsg = "UNKNOWN";
		}
    	
    	return levelMsg;
    }
    
    private  String getCaller(Throwable e) {
    	StackTraceElement[] element = e.getStackTrace();
    	
    	if (element.length > 1) {
    		return e.getStackTrace()[1].getClassName();
    	} else {
    		return "Unknown Caller";
    	}
    }

    /**
     * Return a reference to the current appender
     */
    public  Appender getAppender() {
        return out;
    }

    /**
     * Enabled/disable the context logging feature. When this feature is on, any
     * call to Log.error will trigger the dump of the error context.
     */
    public void enableContextLogging(boolean contextLogging) {
        this.contextLogging = contextLogging;
    }

    /**
     * Allow clients to specify their maximum log level. By default this value
     * is set to TRACE.
     */
    public  void setClientMaxLogLevel(int clientMaxLogLevel) {
        this.clientMaxLogLevel = clientMaxLogLevel;
    }
    
    /**
     * Delete log file
     *
     */
    public  void deleteLog() {
        out.deleteLogFile();
    }
    
    /**
     * Accessor method to define log level
     * the method will be ignorated in Log level is locked
     * @param newlevel log level to be set
     */
    public  void setLogLevel(int newlevel) {
        if(!lockedLogLevel){
        	System.out.println("[level:" + level + "] >> [newlevel:" + newlevel + "] set log level.");
            level = newlevel;
            if (out != null) {
                out.setLogLevel(level);
            }
        }
    }

    /**
     * Accessor method to lock defined log level
     * @param levelToLock log level to be lock
     */
    public  void lockLogLevel(int levelToLock) {
        level = levelToLock;
        lockedLogLevel = true;
        if (out != null) {
            out.setLogLevel(level);
        }
    }
    
    /**
     * Accessor method to lock defined log level
     * 
     */
    public  void unlockLogLevel() {
        lockedLogLevel = false;
    }

    
    /**
     * Accessor method to retrieve log level:
     * @return actual log level
     */
    public  int getLogLevel() {
        return level;
    }
    
    /**
     * ERROR: Error message
     * @param msg the message to be logged
     */
    public  void error(String msg) {
        writeLogMessage("", ERROR, "ERROR", msg);
    }
    
    /**
     * ERROR: Error message
     * @param msg the message to be logged
     * @param obj the object that send error message
     */
    public  void error(Object obj, String msg) {
        writeLogMessage(obj.getClass().getName(), ERROR, "ERROR", msg);
    }

    /**
     * ERROR: Error message
     * @param msg the message to be logged
     * @param tag the tag characterizing the log message initiator
     */
    public  void error(String tag, String msg) {
        writeLogMessage(tag, ERROR, "ERROR", msg);
    }

    /**
     * ERROR: Error message
     * @param msg the message to be logged
     * @param tag the tag characterizing the log message initiator
     * @param e the exception that caused the error
     */
    public  void error(String tag, String msg, Throwable e) {
    	StringBuffer sb = new StringBuffer();
    	sb.append(msg).append("\n").append(StackTracePrinter.getStackTrace(e));
        writeLogMessage(tag, ERROR, "ERROR", sb.toString());
    }
    
    // modified by zhangheng 20140318
    /**
     * WARN: Information message
     * @param msg the message to be logged
     */
    public  void warn(String msg) {
        writeLogMessage("", WARN, "WARN", msg);
    }
    
    /**
     * WARN: Information message
     * @param msg the message to be logged
     * @param obj the object that send log message
     */
    public  void warn(Object obj, String msg) {
        writeLogMessage(obj.getClass().getName(), WARN, "WARN", msg.toString());
    }

    /**
     * WARN: Information message
     * @param msg the message to be logged
     * @param tag the tag characterizing the log message initiator
     */
    public  void warn(String tag, String msg) {
        writeLogMessage(tag, WARN, "WARN", msg.toString());
    }

    
    /**
     * INFO: Information message
     * @param msg the message to be logged
     */
    public  void info(String msg) {
        writeLogMessage("", INFO, "INFO", msg);
    }
    
    /**
     * INFO: Information message
     * @param msg the message to be logged
     * @param obj the object that send log message
     */
    public  void info(Object obj, String msg) {
        writeLogMessage(obj.getClass().getName(), INFO, "INFO", msg.toString());
    }

    /**
     * INFO: Information message
     * @param msg the message to be logged
     * @param tag the tag characterizing the log message initiator
     */
    public  void info(String tag, String msg) {
        writeLogMessage(tag, INFO, "INFO", msg.toString());
    }

    /**
     * DEBUG: Debug message
     * @param msg the message to be logged
     */
    public  void debug(String msg) {
        writeLogMessage("", DEBUG, "DEBUG", msg);
    }
    
    /**
     * DEBUG: Information message
     * @param msg the message to be logged
     * @param tag the tag characterizing the log message initiator
     */
    public  void debug(String tag, String msg) {
        writeLogMessage(tag, DEBUG, "DEBUG", msg.toString());
    }

    /**
     * DEBUG: Information message
     * @param msg the message to be logged
     * @param obj the object that send log message
     */
    public  void debug(Object obj, String msg) {
        writeLogMessage(obj.getClass().getName(), DEBUG, "DEBUG", msg.toString());
    }

    /**
     * TRACE: Debugger mode
     */
    public  void trace(String msg) {
        writeLogMessage(msg, TRACE, "TRACE", msg);
    }
    
    /**
     * TRACE: Information message
     * @param msg the message to be logged
     * @param obj the object that send log message
     */
    public  void trace(Object obj, String msg) {
        writeLogMessage(obj.getClass().getName(), TRACE, "TRACE", msg);
    }

    /**
     * TRACE: Information message
     * @param msg the message to be logged
     * @param tag the tag characterizing the log message initiator
     */
    public  void trace(String tag, String msg) {
        writeLogMessage(tag, TRACE, "TRACE", msg);
    }

    
    /**
     * Dump memory statistics at this point. Dump if level >= DEBUG.
     *
     * @param msg message to be logged
     */
    public  void memoryStats(String msg) {
        // Try to force a garbage collection, so we get the real amount of
        // available memory
        long available = Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().gc();
        writeLogMessage("", PROFILING, "PROFILING-MEMORY", msg + ":" + available
                + " [bytes]");
    }
    
    /**
     * Dump memory statistics at this point.
     *
     * @param obj caller object
     * @param msg message to be logged
     */
    public  void memoryStats(Object obj, String msg) {
        // Try to force a garbage collection, so we get the real amount of
        // available memory
        Runtime.getRuntime().gc();
        long available = Runtime.getRuntime().freeMemory();
        writeLogMessage(obj.getClass().getName(), PROFILING, "PROFILING-MEMORY", obj.getClass().getName()
        + "::" + msg + ":" + available + " [bytes]");
    }
    
    /**
     * Dump time statistics at this point.
     *
     * @param msg message to be logged
     */
    public  void timeStats(String msg) {
        long time = System.currentTimeMillis();
        if (initialTimeStamp == -1) {
            writeLogMessage("", PROFILING, "PROFILING-TIME", msg + ": 0 [msec]");
            initialTimeStamp = time;
        } else {
            long currentTime = time - initialTimeStamp;
            writeLogMessage("", PROFILING, "PROFILING-TIME", msg + ": "
                    + currentTime + "[msec]");
        }
    }
    
    /**
     * Dump time statistics at this point.
     *
     * @param obj caller object
     * @param msg message to be logged
     */
    public  void timeStats(Object obj, String msg) {
        // Try to force a garbage collection, so we get the real amount of
        // available memory
        long time = System.currentTimeMillis();
        if (initialTimeStamp == -1) {
            writeLogMessage(obj.getClass().getName(), PROFILING, "PROFILING-TIME", obj.getClass().getName()
            + "::" + msg + ": 0 [msec]");
            initialTimeStamp = time;
        } else {
            long currentTime = time - initialTimeStamp;
            writeLogMessage(obj.getClass().getName(), PROFILING, "PROFILING-TIME", obj.getClass().getName()
            + "::" + msg + ":" + currentTime + " [msec]");
        }
    }
    
    /**
     * Dump time statistics at this point.
     *
     * @param msg message to be logged
     */
    public  void stats(String msg) {
        memoryStats(msg);
        timeStats(msg);
    }
    
    /**
     * Dump time statistics at this point.
     *
     * @param obj caller object
     * @param msg message to be logged
     */
    public  void stats(Object obj, String msg) {
        memoryStats(obj, msg);
        timeStats(obj, msg);
    }

    /**
     * Return the current log appender LogContent container object
     */         
    public  LogContent getCurrentLogContent() throws IOException {
        return out.getLogContent();
    }

    /**
     * Return true if a message is currently loggable at the given log level
     *
     * @param msgLevel the log level the msg shall be logged at.
     */
    public  boolean isLoggable(int msgLevel) {
        return msgLevel <= level;
    }
    
    private  synchronized void writeLogMessage(String tag, int msgLevel, String levelMsg, String msg) {
        if (contextLogging) {
            try {
                cacheMessage(msgLevel, levelMsg, msg);
            } catch (Exception e) {
                // Cannot cache log message, just ignore the error
            }
        }

        try {
            writeLogMessageNoCache(tag, msgLevel, levelMsg, msg);
        } catch (Exception e) {
            // Cannot write log message, just ignore the error
        }
    }

    private  void writeLogMessageNoCache(String tag, int msgLevel, String levelMsg, String msg) {
        if (level >= msgLevel) {
            try {
                if (out != null) {
                    out.writeLogMessage("[" + mAppTag + "][" + tag + "]", levelMsg,  "[Pid:" + android.os.Process.myPid() + "][Tid:" + Thread.currentThread().getId() + "] " + msg);
                } else {
                    System.out.print(getNow());
                    System.out.print("["+ mAppTag + "] [" + tag + "][" + levelMsg + "]");
                    System.out.print("[Pid:" + android.os.Process.myPid() + "]");
                    System.out.print("[Tid:" + Thread.currentThread().getId() + "]");
                    System.out.println(msg);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private  String getNow() {
    	try {
            return formatter.format(new Date());
    	} catch (Exception e) {
    		return "unknown";
    	}
    }

    private  void cacheMessage(int msgLevel, String levelMsg, String msg) throws IOException {

        // If we are already dumping at DEBUG, then the context is already
        // available
        if (cache == null || level >= clientMaxLogLevel) {
            return;
        }

        if (msgLevel == ERROR) {
            dumpAndFlushCache();
        } else {

            // Store at next
            if (next >= cache.size()) {
                cache.addElement(msg);
            } else {
                cache.setElementAt(msg, next);
            }
            // Move next
            next++;
            if (next == cacheSize) {
                next = 0;
            }

            if (next == first) {
                // Make room for the next entry
                first++;
            }
            if (first == cacheSize) {
                first = 0;
            }
        }
    }

    private  void dumpAndFlushCache() throws IOException {
        int i = first;
        if (first != next) {
            writeLogMessageNoCache("", ERROR, "[Error Context]", "==================================================");
        }
        while (i != next) {
            if (i == cacheSize) {
                i = 0;
            }
            writeLogMessageNoCache("", ERROR, "[Error Context]", (String) cache.elementAt(i));
            ++i;
        }

        if (first != next) {
            writeLogMessageNoCache("", ERROR, "[Error Context]", "==================================================");
        }
        first = 0;
        next = 0;
    }
}
