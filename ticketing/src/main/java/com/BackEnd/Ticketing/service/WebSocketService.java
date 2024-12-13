package com.BackEnd.Ticketing.service;

import com.BackEnd.Ticketing.handler.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebSocketService {

    @Autowired
    private WebSocketHandler webSocketHandler;

    public void sendMessage(String message) {
        try {
            webSocketHandler.sendMessageToAll(message);
        } catch (IOException e) {
            // Handle the exception
        }
    }
}
