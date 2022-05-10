package com.cryptocurrency.service.config;

import com.cryptocurrency.service.service.CryptoCurrencyService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yuvaraj.sanjeevi
 */
@Configuration
@AllArgsConstructor
public class StartupDataLoader {


    private final CryptoCurrencyService cryptoCurrencyService;

    @PostConstruct
    public void loadData() {
        this.loadCryptoCurrencies();
    }

    private void loadCryptoCurrencies() {
        cryptoCurrencyService.loadInitialData();
    }
}
