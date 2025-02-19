package com.example.Test1.handler;



import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

public class VideoChatHandler extends TextWebSocketHandler {
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Parse message data
        String payload = message.getPayload();
        // Assuming you're passing JSON like { type: "join-meeting", meetingId: "123" }
        Map<String, Object> messageData = new ObjectMapper().readValue(payload, Map.class);

        if ("join-meeting".equals(messageData.get("type"))) {
            // Broadcast to all connected users
            for (WebSocketSession s : sessions) {
                if (s.isOpen() && !s.equals(session)) {
                    String joinMessage = "{\"type\": \"user-joined\", \"peerId\": \"" + session.getId() + "\"}";
                    s.sendMessage(new TextMessage(joinMessage));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}
