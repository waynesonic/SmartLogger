package com.android.common.logger;

import java.io.IOException;

public interface Appender {
    
    /**
     * Initialize Log File
     */
    void initLogFile();
    
    /**
     * Open Log file
     */
    void openLogFile();
    
    /**
     * Close Log file
     */
    void closeLogFile();
    
    /**
     * Delete Log file
     */
    void deleteLogFile();
    
    /**
     * Perform additional actions needed when setting a new level.
     */
    public void setLogLevel(int level);

    /**
     * Append a message to the Log file
     */
    void writeLogMessage(String paramTag, String level, String msg) throws IOException;

    /**
     * Get the whole log content for this appender
     * @return LogContent the LogContent object log Container
     */
    public LogContent getLogContent() throws IOException;
}

