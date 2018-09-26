package com.android.common.logger;

import java.io.IOException;
import java.util.Vector;

public class MemoryAppender implements Appender {

    Vector  logData = null;

    private int limit   = 250;

    public MemoryAppender() {
        initLogFile();
    }

    public void closeLogFile() {
    }

    public void deleteLogFile() {
        initLogFile();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void initLogFile() {
        logData = new Vector();
        logData.ensureCapacity(limit + 1);
    }

    public void openLogFile() {
    }

    public void writeLogMessage(String paramTag, String level, String msg) throws IOException {
        StringBuffer message = new StringBuffer();
        message.append("[").append(level).append("] ").append(msg);
        msg = message.toString();
        
        logData.addElement(msg);
        if (logData.size() > limit) {
            logData.removeElementAt(0);
        }
    }
    
    public String getLogData() {
        StringBuffer result = new StringBuffer();
        for (int x = 0; x < logData.size(); x++) {
            result.append(logData.elementAt(x)).append("\n");
        }
        return result.toString();
    }

    /**
     * Perform additional actions needed when setting a new level.
     * MemoryAppender doesn't implement this method
     */
    public void setLogLevel(int level) {
    }

    public LogContent getLogContent() throws IOException {
        return new LogContent(LogContent.STRING_CONTENT, getLogData());
    }

}
