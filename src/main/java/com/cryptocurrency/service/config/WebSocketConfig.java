package com.cryptocurrency.service.config;

import com.cryptocurrency.service.websocket.CryptoCurrencyWSHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

/**
 * @author yuvaraj.sanjeevi
 */
@Configuration
@ComponentScan("com.cryptocurrency.service.websocket")
public class WebSocketConfig {

    @Value("${external-api.live-prices-ws-url}")
    private String webSocketUri;

    @Lazy
    @Autowired
    private CryptoCurrencyWSHandler cryptoCurrencyWSHandler;


    @Bean
    public WebSocketConnectionManager wsConnectionManager() {

        //Generates a web socket connection
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                new StandardWebSocketClient(),
                cryptoCurrencyWSHandler,
                webSocketUri);

        //Will connect as soon as possible
        manager.setAutoStartup(true);
        return manager;
    }
}
