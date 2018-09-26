package com.android.common.logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This appender logs messages to a file using JSR75 (FileConnection)
 * The appender tracks the file size and if it exceeds a given maximum size
 * (customizable by clients) then the current log file is renamed appending a
 * .old to the log name and a new one is created. Therefore the maximum size
 * on this is about 2 times the maxFileSize (this is not accurate as there is
 * no limit on the size of the single message printed).
 */
public class FileAppender implements Appender {
	/**
	 * the merged log file name
	 */
    private String contentPath = null;
    private boolean memory = false;
    
    private String fileName = null;
    private String path = null;
    private String suffix = ".txt";
    private String fileUrl = null;
    
    // default 1M
    private long maxFileSize = 1024 * 1024 * 1;
    private int backup = 4;
    
    private FileAdapter file = null;
    private OutputStream os = null;

    /**
     * lock
     */
    private Object lock = new Object();

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
    		Locale.getDefault());
    
    /**
     * Default constructor
     */
    public FileAppender(String path, String fileName) {
    	if (!isPathValid(path)) {
    		throw new IllegalArgumentException("[path:" + path + "] invalid path");
    	}
    	
    	if (isEmpty(fileName)) {
    		throw new IllegalArgumentException("[fileName:" + fileName + "] empty file name");
    	}
    	
		System.out.println("[path:" + path + "][fileName:" + fileName
				+ "] file appender initialize");
		this.fileName = fileName;
        this.path = path;
    	this.contentPath = path;
    	this.fileUrl = toFileUrl(path, fileName, suffix);
    }
    
    /**
     * Default constructor
     */
    public FileAppender(String path, String fileName, int backup) {
    	if (!isPathValid(path)) {
    		throw new IllegalArgumentException("[path:" + path + "] invalid path");
    	}
    	
    	if (isEmpty(fileName)) {
    		throw new IllegalArgumentException("[fileName:" + fileName + "] empty file name");
    	}
    	
    	System.out.println("[path:" + path + "][fileName:" + fileName
    			 + "][backup:" + backup + "] file appender initialize");
    	this.fileName = fileName;
        this.path = path;
    	this.contentPath = path;
    	this.fileUrl = toFileUrl(path, fileName, suffix);
    	
    	if (backup >= 0) {
    		this.backup = backup;
    	}
    }

    //----------------------------------------------------------- Public Methods
    /**
     * Sets the maximum file size. Once this is size is reached, the current log
     * file is renamed and a new one is created. This way we have at most 2 log
     * files whose size is (roughly) bound to maxFileSize.
     * The minimum file size is 1024 as smaller size does not really make sense.
     * If a client needs smaller files it should probably the usage of other
     * Appenders or modify the behavior of this one by deriving it.
     *
     * @param maxFileSize the max size in bytes
     */
    public void setMaxFileSize(long maxFileSize) {
        if (maxFileSize > 1024) {
            this.maxFileSize = maxFileSize;
        }
    }

    /**
     * Sets the content path. This path is the directory where the combined log
     * is placed so that the LogContent is accessible. By default this directory
     * is the same as the log directory, but it is possible to specify a
     * different one if needed.
     */
    public void setContentPath(String path) {
        contentPath = path;
    }

    /**
     * Sets the content type of the log when it is retrieved via getLogContent.
     * By default the FileAppender returns a content in a file, but if the
     * client prefers an inlined value, then this method allows to force this
     * behavior.
     * Note that regardless of this setting, the log is always written to a
     * file.
     */
    public void setLogContentType(boolean memory) {
        this.memory = memory;
    }
    
    private String getNow() {
    	try {
            return formatter.format(new Date());
    	} catch (Exception e) {
    		return "unknown";
    	}
    }

    /**
     * FileAppender writes one message to the output file
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    public void writeLogMessage(String paramTag, String level, String msg) throws IOException {
        synchronized(lock) {
        	if ((null != os) && (!file.exists())) {
    			closeLogFile();
    			initLogFile();
    		}
        	
        	if (null != os) {
        		byte[] logMsgBytes = null;
            	StringBuffer logMsg = new StringBuffer(128);
                logMsg.append(getNow())
                      .append(" [").append(level).append("] ")
                      .append(paramTag)
                      .append(msg).append("\r\n");
                
                try {
                	logMsgBytes = logMsg.toString().getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                	System.out.println("log UTF-8 exception: " + logMsg);
                	e.printStackTrace();
                //add by zouxiongjie 2014-2-25 对日志异常增加捕获
                } catch (OutOfMemoryError e) {
                	e.printStackTrace();
				}
                
                if ((null != logMsgBytes) && (logMsgBytes.length > 0)) {
                	os.write(logMsgBytes);
                    os.flush();
                }

                // If the file grows beyond the limit, we rename it and create a new
                // one
                long fileSize = file.getSize();
                
                if (fileSize >= maxFileSize) {
                	System.out.println("[fileSize:" + fileSize + "] >= [maxFileSize:"
                        + maxFileSize + "] roll logger file");
                	closeLogFile();
                	rollFile();
                	initLogFile();
                }
        	}
        }
    }
    
    /**
     * delete the file
     * @param fileUrl file name
     */
    private void deleteFile(String fileUrl) throws IOException {
    	FileAdapter file = null;
		
		try {
			file = new FileAdapter(fileUrl);
			
			if (file.exists()) {
				System.out.println("[fileUrl:" + fileUrl + "] delete file");
				file.delete();
			}
		} catch (IOException e) {
			System.out.println("[fileUrl:" + fileUrl
					+ "] delete file exception(IOException)");
			throw e;
		} finally {
			try {
				if (null != file) {
					file.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				file = null;
			}
		}
    }
    
    /**
     * rename the file
     * @param oldFileUrl old file name
     * @param newFileUrl new file name
     */
    private void renameFile(String oldFileUrl, String newFileUrl) throws IOException {
    	FileAdapter file = null;
		
		try {
			file = new FileAdapter(oldFileUrl);
			
			if (file.exists()) {
				System.out.println("[oldFileUrl:" + oldFileUrl + "][newFileUrl:"
			            + newFileUrl + "] rename file");
				file.rename(newFileUrl);
			}
		} catch (IOException e) {
			System.out.println("[oldFileUrl:" + oldFileUrl + "][newFileUrl:"
		            + newFileUrl + "] rename file exception(IOException)");
			throw e;
		} finally {
			try {
				if (null != file) {
					file.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				file = null;
			}
		}
    }
    
    /**
     * Close connection and streams
     */
    private void closeFile(OutputStream os, InputStream is, FileAdapter file) {
    	try {
    		if (null != os) {
    			os.close();
    		}
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	os = null;
        }
    	
    	try {
    		if (null != is) {
    			is.close();
    		}
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	is = null;
        }
    	
    	try {
    		if (null != file) {
    			file.close();
    		}
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
        	file = null;
        }
    }
    
    /**
     * roll the file
     */
    private void rollFile() throws IOException {
    	System.out.println("[backup:" + backup + "] roll file ...");
    	
    	synchronized(lock) {
    		if (0 == backup) {
    			deleteFile(fileUrl);
    		} else if (backup > 0) {
    			deleteFile(toFileUrl(path, (fileName + "." + backup), suffix));
    			
    			for (int i = backup - 1; i > 0; i--) {
    				renameFile(toFileUrl(path, (fileName + "." + i), suffix),
    						toFileUrl(path, (fileName + "." + (i + 1)), suffix));
    			}
    			
    			renameFile(fileUrl, toFileUrl(path, (fileName + ".1"), suffix));
    		}
        }
    }
    
    /**
     * Init the logger
     */
    public void initLogFile() {
    	System.out.println("[fileUrl:" + fileUrl + "] init logger file ...");
    	
    	synchronized (lock) {
    		try {
				file = new FileAdapter(fileUrl);
				os = file.openOutputStream(true);
			} catch (IOException e) {
				System.out.println("[fileUrl:" + fileUrl
						+ "] open file exception(IOException)");
				e.printStackTrace();
			}
    	}
    }

    /**
     * FileAppender doesn't implement this method
     */
    public void openLogFile() {
    	System.out.println("open logger file ...");
    }

    /**
     * Close connection and streams
     */
    public void closeLogFile() {
    	System.out.println("[fileUrl:" + fileUrl + "] close logger file ...");
    	
        synchronized(lock) {
        	closeFile(os, null, file);
        }
    }

    /**
     * Perform additional actions needed when setting a new level.
     * FileAppender doesn't implement this method
     */
    public void setLogLevel(int level) {
    	System.out.println("[level:" + level + "] set log level ...");
    }

    /**
     * Delete the log file
     */
    public void deleteLogFile() {
    	System.out.println("[fileUrl:" + fileUrl + "] delete logger file ...");
    	
        synchronized(lock) {
            try {
                FileAdapter file = new FileAdapter(fileUrl);
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                System.out.println("[fileUrl:" + fileUrl
						+ "] delete file exception(IOException)");
                e.printStackTrace();
            }
        }
    }

    public LogContent getLogContent() throws IOException {
        synchronized (lock) {
            FileAdapter mergedFa = null;
            OutputStream mergedOs = null;
            String shardFileUrl = null;
            String mergedFileUrl = toFileUrl(contentPath, (fileName + ".merged"), suffix);
            
            System.out.println("[backup:" + backup + "][mergedFileUrl:" + mergedFileUrl
            		+ "] start merge content ...");
            
            try {
                // Prepare the output stream
                if (memory) {
                	mergedOs = new ByteArrayOutputStream();
                } else {
                	mergedFa = new FileAdapter(mergedFileUrl);
                	mergedOs = mergedFa.openOutputStream();
                }
                
                for (int i = backup; i > 0; i--) {
                	shardFileUrl = toFileUrl(path, (fileName + "." + i), suffix);
                	merge(shardFileUrl, mergedOs);
                }
                
                merge(fileUrl, mergedOs);

                if (memory) {
                	return new LogContent(LogContent.STRING_CONTENT, mergedOs.toString());
                } else {
                	return new LogContent(LogContent.FILE_CONTENT, mergedFileUrl);
                }
            } catch (IOException e) {
            	System.out.println("[backup:" + backup + "][mergedFileUrl:" + mergedFileUrl
                		+ "] merge content exception(IOException)");
                throw e;
            } finally {
                closeFile(mergedOs, null, mergedFa);
            }
        }
    }
    
    private void merge(String fileUrl, OutputStream os) throws IOException {
    	FileAdapter file = null;
    	InputStream is = null;
    	
    	try {
    		file = new FileAdapter(fileUrl);
    		
    		if (file.exists()) {
    			is = file.openInputStream();
        		
        		if (null == is) {
        			System.out.println("[fileUrl:" + fileUrl + "] open file failed");
                } else {
                	System.out.println("[fileUrl:" + fileUrl + "] merge to content");
                	merge(is, os);
                }
    		}
        } catch (Exception e) {
            System.out.println("[fileUrl:" + fileUrl + "] open file exception(Exception)");
            e.printStackTrace();
        } finally {
        	closeFile(null, is, file);
        }
    }

    private void merge(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[4096];
        int length = 0;
        do {
            length = is.read(buffer);
            if (length > 0) {
                os.write(buffer, 0, length);
                os.flush();
            }
        } while(length > 0);
    }

    private boolean isPathValid(String path) {
    	return true;
    }
    
    private boolean isEmpty(String s) {
    	return ((null == s) || (s.length() <= 0));
    }
    
    private String toFileUrl(String path, String fileName, String suffix) {
    	if (path.endsWith(File.separator)) {
    		return path + fileName + suffix;
    	} else {
    		return path + File.separator + fileName + suffix;
    	}
    }
}

