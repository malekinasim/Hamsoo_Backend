package com.hamsoo.bpm.model.dto.error;

public class ErrorDto {

    private Status status;
    private String code;
    private String message;
    private Object detail;

    private ErrorDto(Status status, String code, String message, Object detail) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.detail = detail;
    }

    public Status getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getDetail() {
        return detail;
    }

    public static class Builder {
        private Status status;
        private String code;
        private String message;
        private Object detail;

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder detail(Object detail) {
            this.detail = detail;
            return this;
        }

        public ErrorDto build() {
            return new ErrorDto(status, code, message, detail);
        }
    }

    public enum Status {
        WARNING, ERROR;
    }
}
