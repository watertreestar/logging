package com.xingyao.simple.log;

/**
 * @Author ranger
 * @Date 2021/1/24 21:09
 **/
public class LogConfigurationException extends RuntimeException {

    /**
     * Construct a new exception with <code>null</code> as its detail message.
     */
    public LogConfigurationException() {
    }

    /**
     * Construct a new exception with the specified detail message.
     *
     * @param message The detail message
     */
    public LogConfigurationException(final String message) {
        super(message);
    }

    /**
     * Construct a new exception with the specified cause and a derived
     * detail message.
     *
     * @param cause The underlying cause
     */
    public LogConfigurationException(final Throwable cause) {
        this(cause == null ? null : cause.toString(), cause);
    }

    /**
     * Construct a new exception with the specified detail message and cause.
     *
     * @param message The detail message
     * @param cause The underlying cause
     */
    public LogConfigurationException(final String message, final Throwable cause) {
        super(message + " (Caused by " + cause + ")");
        this.cause = cause; // Two-argument version requires JDK 1.4 or later
    }

    /**
     * The underlying cause of this exception.
     */
    protected Throwable cause;

    /**
     * Return the underlying cause of this exception (if any).
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
