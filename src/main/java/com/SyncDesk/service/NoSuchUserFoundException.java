package com.SyncDesk.service;

public class NoSuchUserFoundException extends RuntimeException {
    public NoSuchUserFoundException(String message) {
        super(message);
    }
}
