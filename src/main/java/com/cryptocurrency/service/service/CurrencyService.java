package com.cryptocurrency.service.service;

import com.cryptocurrency.service.entity.CryptoCurrency;

/**
 * @author yuvaraj.sanjeevi
 */
public interface CurrencyService {

    String convertToLocalCurrency(CryptoCurrency cryptoCurrency, String countryCode);
}
