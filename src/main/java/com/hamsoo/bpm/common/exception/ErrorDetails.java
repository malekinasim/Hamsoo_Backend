package com.hamsoo.bpm.common.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorDetails {
    private List<String> message;
    private LocalDateTime timeStamp;
    private String details;
    private String currentUser;
    private String serverAddress;
    private Object additionalData;
    private String errorCode;

    public ErrorDetails(final List<String> message, final LocalDateTime timeStamp, final String details, final String currentUser, final String serverAddress) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.details = details;
        this.currentUser = currentUser;
        this.serverAddress = serverAddress;
    }

    public ErrorDetails(List<String> message, LocalDateTime timeStamp, String details, String currentUser, String serverAddress, Object additionalData, String errorCode) {
        this.message = message;
        this.timeStamp = timeStamp;
        this.details = details;
        this.currentUser = currentUser;
        this.serverAddress = serverAddress;
        this.additionalData = additionalData;
        this.errorCode = errorCode;
    }

    public ErrorDetails() {
    }

    public List<String> getMessage() {
        return this.message;
    }

    public LocalDateTime getTimeStamp() {
        return this.timeStamp;
    }

    public String getDetails() {
        return this.details;
    }

    public String getCurrentUser() {
        return this.currentUser;
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setMessage(final List<String> message) {
        this.message = message;
    }

    public void setTimeStamp(final LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setDetails(final String details) {
        this.details = details;
    }

    public void setCurrentUser(final String currentUser) {
        this.currentUser = currentUser;
    }

    public void setServerAddress(final String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Object getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Object additionalData) {
        this.additionalData = additionalData;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    public enum ErrorCode {
        GENERAL_ERROR("GENERAL_ERROR");

        private String code;

        ErrorCode(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}
