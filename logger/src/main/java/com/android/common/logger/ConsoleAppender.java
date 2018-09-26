package com.android.common.logger;

import java.io.IOException;

import java.util.Date;

/**
 * Default debugger to be used instea of System.out.println(msg);
 */ 
public class ConsoleAppender implements Appender {

    /**
     * Default constructor
     */
    public ConsoleAppender() {
    }
    
    //----------------------------------------------------------- Public Methods
    /**
     * ConsoleAppender writes one message on the standard output
     */
    public void writeLogMessage(String paramTag, String level, String msg) {
        Date now = new Date();
        System.out.print(now.toString());
        System.out.print(" [" + level + "] " );
        System.out.println(msg);
    }
    
    /**
     * ConsoleAppender doesn't implement this method
     */
    public void initLogFile() {
    }

    /**
     * ConsoleAppender doesn't implement this method
     */
    public void openLogFile() {
    }

    /**
     * ConsoleAppender doesn't implement this method
     */
    public void closeLogFile() {
    }

    /**
     * ConsoleAppender doesn't implement this method
     */
    public void deleteLogFile() {
    }

    /**
     * Perform additional actions needed when setting a new level.
     * ConsoleAppender doesn't implement this method
     */
    public void setLogLevel(int level) {
    }


    public LogContent getLogContent() throws IOException {
        throw new IOException("Cannot get log content");
    }
}
