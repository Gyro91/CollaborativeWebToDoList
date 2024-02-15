package org.gyro.todoapp.exceptions;

public class TaskVersionException extends Exception {

    public TaskVersionException() {
    }

    public TaskVersionException(String message) {
        super(message);
    }

    public TaskVersionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskVersionException(Throwable cause) {
        super(cause);
    }

    public TaskVersionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
