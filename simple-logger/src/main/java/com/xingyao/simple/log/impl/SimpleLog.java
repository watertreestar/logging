package com.xingyao.simple.log.impl;

import com.xingyao.simple.log.Log;
import com.xingyao.simple.log.LogConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * @Author ranger
 * @Date 2021/1/22 17:44
 * a simple implementation of {@link Log}
 **/
public class SimpleLog implements Log {

    /** common configuration */
    private static Properties logProperties = new Properties();

    private static String PROPERTIES_PREFIX = "com.xingyao.logging.";

    private static final String DEFUALT_DATE_TIME_FORMAT = "yyyy-MM-dd hh:MM:ss SSS";

    protected static boolean showLogName = true;

    protected static boolean showShortName = true;

    protected static boolean showDateTime = true;

    protected static String dateTimeFormat = DEFUALT_DATE_TIME_FORMAT;

    protected static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);

    protected static String configurationFileName = "logging.properties";

    /** "Trace" level logging. */
    public static final int LOG_LEVEL_TRACE  = 1;
    /** "Debug" level logging. */
    public static final int LOG_LEVEL_DEBUG  = 2;
    /** "Info" level logging. */
    public static final int LOG_LEVEL_INFO   = 3;
    /** "Warn" level logging. */
    public static final int LOG_LEVEL_WARN   = 4;
    /** "Error" level logging. */
    public static final int LOG_LEVEL_ERROR  = 5;
    /** "Fatal" level logging. */
    public static final int LOG_LEVEL_FATAL  = 6;

    /** Enable all logging levels */
    public static final int LOG_LEVEL_ALL    = LOG_LEVEL_TRACE - 1;

    /** Enable no logging levels */
    public static final int LOG_LEVEL_OFF    = LOG_LEVEL_FATAL + 1;


    /** full class name */
    private String logName;

    private int logLevel;

    private String shortLogName;

    public SimpleLog(String name){
        this.logName = name;

        this.setLevel(LOG_LEVEL_INFO);
        // Set log level from properties
        String lvl = getStringProperty(PROPERTIES_PREFIX + "log." + logName);

        /** get log instance specific log level configuration */
        int i = String.valueOf(name).lastIndexOf(".");
        while(null == lvl && i > -1) {
            name = name.substring(0,i);
            lvl = getStringProperty(PROPERTIES_PREFIX + "log." + name);
            i = String.valueOf(name).lastIndexOf(".");
        }

        if(null == lvl) {
            lvl =  getStringProperty(PROPERTIES_PREFIX + "defaultlog");
        }

        if("all".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_ALL);
        } else if("trace".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_TRACE);
        } else if("debug".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_DEBUG);
        } else if("info".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_INFO);
        } else if("warn".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_WARN);
        } else if("error".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_ERROR);
        } else if("fatal".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_FATAL);
        } else if("off".equalsIgnoreCase(lvl)) {
            setLevel(SimpleLog.LOG_LEVEL_OFF);
        }
    }

    private static String getStringProperty(final String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (final SecurityException e) {
            // Ignore
        }
        return prop == null ? logProperties.getProperty(name) : prop;
    }

    private static String getStringProperty(final String name, final String defaultValue) {
        final String prop = getStringProperty(name);
        return prop == null ? defaultValue : prop;
    }

    private static boolean getBooleanProperty(final String name, final boolean defaultValue) {
        final String prop = getStringProperty(name);
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    static {
        InputStream in = getResourceAsStream(configurationFileName);

        Properties properties = new Properties();
        try{
            properties.load(in);
        }catch (IOException e){
            throw new LogConfigurationException("loading log configuration error",e);
        }

        showDateTime = getBooleanProperty(PROPERTIES_PREFIX + "showDateTime",showDateTime);
        showLogName = getBooleanProperty(PROPERTIES_PREFIX + "showLogName",showLogName);
        showShortName = getBooleanProperty(PROPERTIES_PREFIX + "showShortName",showShortName);

        if(showDateTime) {
            dateTimeFormat = getStringProperty(PROPERTIES_PREFIX + "dateTimeFormat",
                    dateTimeFormat);
            dateTimeFormatter  = DateTimeFormatter.ofPattern(dateTimeFormat);
        }
    }



    /**
     * Set logging level.
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(final int currentLogLevel) {
        this.logLevel = currentLogLevel;
    }

    /**
     * Get logging level.
     */
    public int getLevel() {
        return logLevel;
    }

    /**
     * general method to do logging
     * all level log methods will call this method finally
     *
     * @param type        log level
     * @param message     message from log method
     * @param t           exception, cause stack trace will be logged
     */
    private void log(final int type,final Object message,final Throwable t){
        // Use a string buffer for better performance
        final StringBuffer buf = new StringBuffer();

        // Append date-time if so configured
        if(showDateTime) {
            final LocalDateTime now = LocalDateTime.now();
            String dateText;

            dateText = dateTimeFormatter.format(now);

            buf.append(dateText);
            buf.append(" ");
        }

        // Append a readable representation of the log level
        switch(type) {
            case SimpleLog.LOG_LEVEL_TRACE: buf.append("[TRACE] "); break;
            case SimpleLog.LOG_LEVEL_DEBUG: buf.append("[DEBUG] "); break;
            case SimpleLog.LOG_LEVEL_INFO:  buf.append("[INFO] ");  break;
            case SimpleLog.LOG_LEVEL_WARN:  buf.append("[WARN] ");  break;
            case SimpleLog.LOG_LEVEL_ERROR: buf.append("[ERROR] "); break;
            case SimpleLog.LOG_LEVEL_FATAL: buf.append("[FATAL] "); break;
        }

        // Append the name of the log instance if so configured
        if(showShortName) {
            if(shortLogName == null) {
                // Cut all but the last component of the name for both styles
                final String slName = logName.substring(logName.lastIndexOf(".") + 1);
                shortLogName = slName.substring(slName.lastIndexOf("/") + 1);
            }
            buf.append(String.valueOf(shortLogName)).append(" - ");
        } else if(showLogName) {
            buf.append(String.valueOf(logName)).append(" - ");
        }

        // Append the message
        buf.append(String.valueOf(message));

        // Append stack trace if not null
        if(t != null) {
            buf.append(" <");
            buf.append(t.toString());
            buf.append(">");

            final java.io.StringWriter sw = new java.io.StringWriter(1024);
            final java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        this.print(buf);

    }

    private void print(StringBuffer wb){
        System.out.println(wb.toString());
    }

    @Override
    public void debug(Object message) {
        if(this.isDebugEnabled()){
            this.log(SimpleLog.LOG_LEVEL_DEBUG,message,null);
        }
    }


    @Override
    public void debug(Object message, Throwable t) {
        if(this.isDebugEnabled()){
            this.log(SimpleLog.LOG_LEVEL_DEBUG,message,t);
        }
    }

    @Override
    public void error(Object message) {
        if(this.isErrorEnabled()){
            this.log(SimpleLog.LOG_LEVEL_ERROR,message,null);
        }
    }

    @Override
    public void error(Object message, Throwable t) {
        if(this.isErrorEnabled()){
            this.log(SimpleLog.LOG_LEVEL_ERROR,message,t);
        }
    }

    @Override
    public void fatal(Object message) {
        if(this.isFatalEnabled()){
            this.log(SimpleLog.LOG_LEVEL_FATAL,message,null);
        }
    }

    @Override
    public void fatal(Object message, Throwable t) {
        if(this.isFatalEnabled()){
            this.log(SimpleLog.LOG_LEVEL_FATAL,message,t);
        }
    }

    @Override
    public void info(Object message) {
        if(this.isInfoEnabled()){
            this.log(SimpleLog.LOG_LEVEL_INFO,message,null);
        }
    }

    @Override
    public void info(Object message, Throwable t) {
        if(this.isInfoEnabled()){
            this.log(SimpleLog.LOG_LEVEL_INFO,message,t);
        }
    }

    @Override
    public void trace(Object message) {
        if(this.isTraceEnabled()){
            this.log(SimpleLog.LOG_LEVEL_TRACE,message,null);
        }
    }

    @Override
    public void trace(Object message, Throwable t) {
        if(this.isTraceEnabled()){
            this.log(SimpleLog.LOG_LEVEL_TRACE,message,t);
        }
    }

    @Override
    public void warn(Object message) {
        if(this.isWarnEnabled()){
            this.log(SimpleLog.LOG_LEVEL_WARN,message,null);
        }
    }

    @Override
    public void warn(Object message, Throwable t) {
        if(this.isWarnEnabled()){
            this.log(SimpleLog.LOG_LEVEL_WARN,message,t);
        }
    }


    @Override
    public boolean isDebugEnabled() {
        return this.logLevel <= SimpleLog.LOG_LEVEL_DEBUG;
    }

    @Override
    public boolean isErrorEnabled() {
         return this.logLevel <= SimpleLog.LOG_LEVEL_ERROR;
    }

    @Override
    public boolean isFatalEnabled() {
         return this.logLevel <= SimpleLog.LOG_LEVEL_FATAL;
    }

    @Override
    public boolean isInfoEnabled() {
         return this.logLevel <= SimpleLog.LOG_LEVEL_INFO;
    }

    @Override
    public boolean isTraceEnabled() {
         return this.logLevel <= SimpleLog.LOG_LEVEL_TRACE;
    }

    @Override
    public boolean isWarnEnabled() {
         return this.logLevel <= SimpleLog.LOG_LEVEL_WARN;
    }


    /**
     * Return the thread context class loader if available.
     * Otherwise return null.
     *
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
     *
     * @throws LogConfigurationException if a suitable class loader
     * cannot be identified.
     */
    private static ClassLoader getContextClassLoader() {
        ClassLoader classLoader = null;

        try {
            // Are we running on a JDK 1.2 or later system?
            final Method method = Thread.class.getMethod("getContextClassLoader", (Class[]) null);

            // Get the thread context class loader (if there is one)
            try {
                classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Class[]) null);
            } catch (final IllegalAccessException e) {
                // ignore
            } catch (final InvocationTargetException e) {
                /**
                 * InvocationTargetException is thrown by 'invoke' when
                 * the method being invoked (getContextClassLoader) throws
                 * an exception.
                 *
                 * getContextClassLoader() throws SecurityException when
                 * the context class loader isn't an ancestor of the
                 * calling class's class loader, or if security
                 * permissions are restricted.
                 *
                 * In the first case (not related), we want to ignore and
                 * keep going.  We cannot help but also ignore the second
                 * with the logic below, but other calls elsewhere (to
                 * obtain a class loader) will trigger this exception where
                 * we can make a distinction.
                 */
                if (e.getTargetException() instanceof SecurityException) {
                    // ignore
                } else {
                    // Capture 'e.getTargetException()' exception for details
                    // alternate: log 'e.getTargetException()', and pass back 'e'.
                    throw new LogConfigurationException
                            ("Unexpected InvocationTargetException", e.getTargetException());
                }
            }
        } catch (final NoSuchMethodException e) {
            // Assume we are running on JDK 1.1
            // ignore
        }

        if (classLoader == null) {
            classLoader = SimpleLog.class.getClassLoader();
        }

        // Return the selected class loader
        return classLoader;
    }

    private static InputStream getResourceAsStream(final String name) {
        return (InputStream) AccessController.doPrivileged(
                new PrivilegedAction() {
                    @Override
                    public Object run() {
                        final ClassLoader threadCL = getContextClassLoader();

                        if (threadCL != null) {
                            return threadCL.getResourceAsStream(name);
                        } else {
                            return ClassLoader.getSystemResourceAsStream(name);
                        }
                    }
                });
    }
}
