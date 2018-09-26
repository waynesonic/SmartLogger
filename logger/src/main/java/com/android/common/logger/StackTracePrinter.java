package com.android.common.logger;

import java.io.StringWriter;
import java.io.PrintWriter;

public class StackTracePrinter {

    public static String getStackTrace(Throwable exception) 
    { 
        StringWriter sw = new StringWriter(); 
        PrintWriter pw = new PrintWriter(sw); 
        exception.printStackTrace(pw); 
        return sw.toString(); 
    }
}
