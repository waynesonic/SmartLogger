package com.android.common.logger;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Default debugger to be used instea of System.out.println(msg);
 */ 
public class AndroidLogAppender implements Appender {
    
    // ---------------------------------------------------------------------------

    /** Default constructor */
    public AndroidLogAppender() {
    }

    //----------------------------------------------------------- Public Methods
    /**
     * ConsoleAppender writes one message on the standard output
     */
    public void writeLogMessage(String paramTag, String level, String msg) {
        String tempTag = null;
        if (paramTag != null && !paramTag.equals("")) {
        	tempTag = paramTag;
        }

        if (level.equals("ERROR")) {
            Log.e(tempTag, msg);
        } else if (level.equals("INFO")) {
            Log.i(tempTag, msg);
        } else if (level.equals("DEBUG")) {
            Log.d(tempTag, msg);
        } else if (level.equals("WARN")) {
            Log.w(tempTag, msg);
        } else {
            Log.v(tempTag, msg);
        } 
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

    public void setLogLevel(int i) {}

    public LogContent getLogContent() throws IOException {
        StringBuffer log = new StringBuffer();
        String commandLine = "logcat -d";

        Process process = Runtime.getRuntime().exec(commandLine);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = bufferedReader.readLine()) != null){
            log.append(line);
            log.append(System.getProperty("line.separator"));
        }
        return new LogContent(LogContent.STRING_CONTENT, log.toString());
    }

}
