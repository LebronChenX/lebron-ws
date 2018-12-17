package com.lebron.ws.model;

import lombok.Data;

@Data
public class Message {

    private String roomId;
    private String name;
    private String message;

    public Message() {
    }

    public Message(String roomId, String name, String message) {
        this.roomId = roomId;
        this.name = name;
        this.message = message;
    }
}
