package com.SyncDesk.utils;

public class NoSuchUserFoundException extends RuntimeException {
    public NoSuchUserFoundException(String message) {
        super(message);
    }
}
