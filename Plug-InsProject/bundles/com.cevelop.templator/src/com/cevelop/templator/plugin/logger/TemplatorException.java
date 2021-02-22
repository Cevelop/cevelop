package com.cevelop.templator.plugin.logger;

public class TemplatorException extends Exception {

    private static final long serialVersionUID = 1L;

    public TemplatorException() {}

    public TemplatorException(String message) {
        super(message);
    }

    public TemplatorException(Throwable cause) {
        super(cause);
    }

    public TemplatorException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplatorException(String message, Throwable cause, boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
