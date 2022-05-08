package com.cryptocurrency.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yuvaraj.sanjeevi
 */
@Configuration
@ConfigurationProperties(prefix = "coincap")
@Data
public class CoinCapConfig {

    private String assetsUrl;
    private String livePricesWsUrl;
    private String currencyRatesUrl;
    private String apiKey;
}
