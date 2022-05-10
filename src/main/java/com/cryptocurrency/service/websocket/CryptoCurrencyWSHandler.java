package com.cryptocurrency.service.websocket;

import com.cryptocurrency.service.service.CryptoCurrencyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author yuvaraj.sanjeevi
 */
@Slf4j
@Component
@AllArgsConstructor
public class CryptoCurrencyWSHandler extends TextWebSocketHandler {


    private final ObjectMapper objectMapper;
    private final CryptoCurrencyService cryptoCurrencyService;
    @Lazy
    private final WebSocketConnectionManager webSocketConnectionManager;

    /**
     * Called when WS connects to the server.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("Connection Established");
    }

    /**
     * Main method to handle server messages.
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        TextMessage textMessage = (TextMessage) message;
        JsonNode wsResponse = objectMapper.readValue(textMessage.asBytes(), JsonNode.class);
        log.debug("Received Message {}", wsResponse);
        cryptoCurrencyService.handleWSResponse(wsResponse);
    }

    /**
     * Error handling.
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Error While Connecting Web socket", exception);

    }

    /**
     * Called when WS is closed.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        super.afterConnectionClosed(session, closeStatus);
        log.error("after connection closed close status {}", closeStatus);
        this.reconnect();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * Reconnect after close
     */
    private void reconnect() {
        webSocketConnectionManager.stop();
        webSocketConnectionManager.start();
    }



}
