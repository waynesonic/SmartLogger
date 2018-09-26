package com.android.common.logger;

import java.io.IOException;

/**
 * Generic Log class
 */
public class Log {

	private static LogImpl mLogImpl = new LogImpl();

	/**
	 * Log level DISABLED: used to speed up applications using logging features
	 */
	public static final int DISABLED = -1;

	/**
	 * Log level ERROR: used to log error messages.
	 */
	public static final int ERROR = 0;

	/**
	 * Log level WARN: used to log warn messages.
	 */
	public static final int WARN = 1;

	/**
	 * Log level INFO: used to log information messages.
	 */
	public static final int INFO = 2;

	/**
	 * Log level DEBUG: used to log debug messages.
	 */
	public static final int DEBUG = 3;

	/**
	 * Log level TRACE: used to trace the program execution.
	 */
	public static final int TRACE = 4;

	// -------------------------------------------------------------
	// Constructors
	/**
	 * This class is static and cannot be intantiated
	 */
	private Log() {
	}

	// ----------------------------------------------------------- Public
	// methods
	/**
	 * Initialize log file with a specific appender and log level. Contextual
	 * errors handling is disabled after this call.
	 * @param appTAG
	 * 		the application tag
	 * @param object
	 *            the appender object that write log file
	 * @param level
	 *            the log level
	 */
	public synchronized static void initLog(String appTAG, Appender object, int level) {
		mLogImpl.initLog(appTAG, object, level);
	}

	/**
	 * Return a reference to the current appender
	 */
	public static Appender getAppender() {
		return mLogImpl.getAppender();
	}

	/**
	 * Enabled/disable the context logging feature. When this feature is on, any
	 * call to Log.error will trigger the dump of the error context.
	 */
	public static void enableContextLogging(boolean contextLogging) {
		mLogImpl.enableContextLogging(contextLogging);
	}

	/**
	 * Allow clients to specify their maximum log level. By default this value
	 * is set to TRACE.
	 */
	public static void setClientMaxLogLevel(int clientMaxLogLevel) {
		mLogImpl.setClientMaxLogLevel(clientMaxLogLevel);
	}

	/**
	 * Delete log file
	 * 
	 */
	public static void deleteLog() {
		mLogImpl.deleteLog();
	}

	/**
	 * Accessor method to define log level the method will be ignorated in Log
	 * level is locked
	 * 
	 * @param newlevel
	 *            log level to be set
	 */
	public static void setLogLevel(int newlevel) {
		mLogImpl.setLogLevel(newlevel);
	}

	/**
	 * Accessor method to lock defined log level
	 * 
	 * @param levelToLock
	 *            log level to be lock
	 */
	public static void lockLogLevel(int levelToLock) {
		mLogImpl.lockLogLevel(levelToLock);
	}

	/**
	 * Accessor method to lock defined log level
	 * 
	 */
	public static void unlockLogLevel() {
		mLogImpl.unlockLogLevel();
	}

	/**
	 * Accessor method to retrieve log level:
	 * 
	 * @return actual log level
	 */
	public static int getLogLevel() {
		return mLogImpl.getLogLevel();
	}

	/**
	 * ERROR: Error message
	 * 
	 * @param msg
	 *            the message to be logged
	 */
	public static void error(String msg) {
		mLogImpl.error(msg);
	}

	/**
	 * ERROR: Error message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param obj
	 *            the object that send error message
	 */
	public static void error(Object obj, String msg) {
		mLogImpl.error(obj, msg);
	}

	/**
	 * ERROR: Error message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param tag
	 *            the tag characterizing the log message initiator
	 */
	public static void error(String tag, String msg) {
		mLogImpl.error(tag, msg);
	}

	/**
	 * ERROR: Error message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param tag
	 *            the tag characterizing the log message initiator
	 * @param e
	 *            the exception that caused the error
	 */
	public static void error(String tag, String msg, Throwable e) {
		mLogImpl.error(tag, msg, e);
	}

	/**
	 * WARN: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 */
	public static void warn(String msg) {
		mLogImpl.warn(msg);
	}

	/**
	 * WARN: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param obj
	 *            the object that send log message
	 */
	public static void warn(Object obj, String msg) {
		mLogImpl.warn(obj, msg);
	}

	/**
	 * WARN: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param tag
	 *            the tag characterizing the log message initiator
	 */
	public static void warn(String tag, String msg) {
		mLogImpl.warn(tag, msg);
	}

	/**
	 * INFO: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 */
	public static void info(String msg) {
		mLogImpl.info(msg);
	}

	/**
	 * INFO: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param obj
	 *            the object that send log message
	 */
	public static void info(Object obj, String msg) {
		mLogImpl.info(obj, msg);
	}

	/**
	 * INFO: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param tag
	 *            the tag characterizing the log message initiator
	 */
	public static void info(String tag, String msg) {
		mLogImpl.info(tag, msg);
	}

	/**
	 * DEBUG: Debug message
	 * 
	 * @param msg
	 *            the message to be logged
	 */
	public static void debug(String msg) {
		mLogImpl.debug(msg);
	}

	/**
	 * DEBUG: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param tag
	 *            the tag characterizing the log message initiator
	 */
	public static void debug(String tag, String msg) {
		mLogImpl.debug(tag, msg);
	}

	/**
	 * DEBUG: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param obj
	 *            the object that send log message
	 */
	public static void debug(Object obj, String msg) {
		mLogImpl.debug(obj, msg);
	}

	/**
	 * TRACE: Debugger mode
	 */
	public static void trace(String msg) {
		mLogImpl.trace(msg);
	}

	/**
	 * TRACE: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param obj
	 *            the object that send log message
	 */
	public static void trace(Object obj, String msg) {
		mLogImpl.trace(obj, msg);
	}

	/**
	 * TRACE: Information message
	 * 
	 * @param msg
	 *            the message to be logged
	 * @param tag
	 *            the tag characterizing the log message initiator
	 */
	public static void trace(String tag, String msg) {
		mLogImpl.trace(tag, msg);
	}

	/**
	 * Dump memory statistics at this point. Dump if level >= DEBUG.
	 * 
	 * @param msg
	 *            message to be logged
	 */
	public static void memoryStats(String msg) {
		mLogImpl.memoryStats(msg);
	}

	/**
	 * Dump memory statistics at this point.
	 * 
	 * @param obj
	 *            caller object
	 * @param msg
	 *            message to be logged
	 */
	public static void memoryStats(Object obj, String msg) {
		mLogImpl.memoryStats(obj, msg);
	}

	/**
	 * Dump time statistics at this point.
	 * 
	 * @param msg
	 *            message to be logged
	 */
	public static void timeStats(String msg) {
		mLogImpl.timeStats(msg);
	}

	/**
	 * Dump time statistics at this point.
	 * 
	 * @param obj
	 *            caller object
	 * @param msg
	 *            message to be logged
	 */
	public static void timeStats(Object obj, String msg) {
		mLogImpl.timeStats(obj, msg);
	}

	/**
	 * Dump time statistics at this point.
	 * 
	 * @param msg
	 *            message to be logged
	 */
	public static void stats(String msg) {
		mLogImpl.stats(msg);
	}

	/**
	 * Dump time statistics at this point.
	 * 
	 * @param obj
	 *            caller object
	 * @param msg
	 *            message to be logged
	 */
	public static void stats(Object obj, String msg) {
		mLogImpl.stats(obj, msg);
	}

	/**
	 * Return the current log appender LogContent container object
	 */
	public static LogContent getCurrentLogContent() throws IOException {
		return mLogImpl.getCurrentLogContent();
	}

	/**
	 * Return true if a message is currently loggable at the given log level
	 * 
	 * @param msgLevel
	 *            the log level the msg shall be logged at.
	 */
	public static boolean isLoggable(int msgLevel) {
		return mLogImpl.isLoggable(msgLevel);
	}
}
