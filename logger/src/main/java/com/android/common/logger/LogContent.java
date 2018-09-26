package com.android.common.logger;

/**
 * Log Container class. Holds the properties of the log appender which belongs 
 * to and the references the log content. This class can be used by all log 
 * appenders.
 */ 
public class LogContent {
   
    public static final int FILE_CONTENT   = 0;
    public static final int STRING_CONTENT = 1;

    private int contentType;
    private String content = null;
    
    /**
     * Build a log container specifying the appender information
     */         
    public LogContent(int contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    /**
     * @return the content type. Possible values are:
     *  <ul>
     *    <li> FILE_CONTENT if the content is in a file </li>
     *    <li> STRING_CONTENT if the content is inlined </li>
     *  </ul>
     */             
    public int getContentType() {
        return contentType;
    }

    /**
     * @return the log content as a formatted string. Depending on the content
     * type the  value returned may represent a filename or the inlined log
     * content. This method can return null if the content type is NULL_CONTENT
     */
    public String getContent() {
        return content;
    }
}
