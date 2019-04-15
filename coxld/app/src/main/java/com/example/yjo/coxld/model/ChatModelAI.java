package com.example.yjo.coxld.model;

public class ChatModelAI  {

    public String message;
    public boolean isSend;

    public ChatModelAI(String message, boolean isSend) {
        this.message = message;
        this.isSend = isSend;
    }

    public ChatModelAI() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }
}
