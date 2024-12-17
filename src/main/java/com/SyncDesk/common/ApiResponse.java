package com.SyncDesk.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String message;
    private T data;
    private String errorCode;

    public ApiResponse(String message, T data, String errorCode) {
        this.message = message;
        this.data = data;
        this.errorCode = errorCode;
    }

    public ApiResponse(String message, T data) {
        this(message, data, null);
    }

}

