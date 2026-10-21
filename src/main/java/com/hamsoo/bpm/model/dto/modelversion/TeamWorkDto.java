package com.hamsoo.bpm.model.dto.modelversion;

import java.time.LocalDateTime;

public class TeamWorkDto {

    private Object content;
    private String message;
    private String sender;
    private String senderUsername;

    private LocalDateTime dateTime;
    private MessageType messageType;
    private String modelVersionId;

    public TeamWorkDto(String message, String sender, String senderUsername, MessageType messageType, String modelVersionId) {
        this.message = message;
        this.sender = sender;
        this.dateTime = LocalDateTime.now();
        this.messageType = messageType;
        this.modelVersionId = modelVersionId;
        this.senderUsername = senderUsername;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getModelVersionId() {
        return modelVersionId;
    }

    public void setModelVersionId(String modelVersionId) {
        this.modelVersionId = modelVersionId;
    }

    public enum MessageType {
        JOIN, LEAVE, UPDATE_XML,COMMENT,UPDATE_MODEL_MEMBER
    }

}
