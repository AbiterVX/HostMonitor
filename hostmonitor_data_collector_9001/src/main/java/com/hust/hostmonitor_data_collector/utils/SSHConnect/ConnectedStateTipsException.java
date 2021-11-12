package com.hust.hostmonitor_data_collector.utils.SSHConnect;

public class ConnectedStateTipsException extends Exception{
    public ConnectedStateTipsException() {
        super();
    }

    public ConnectedStateTipsException(String message) {
        super(message);
    }

    public ConnectedStateTipsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectedStateTipsException(Throwable cause) {
        super(cause);
    }

    public ConnectedStateTipsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
