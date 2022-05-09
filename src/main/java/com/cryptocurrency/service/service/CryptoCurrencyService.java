package com.cryptocurrency.service.service;

import com.cryptocurrency.service.model.CryptoCurrency;
import com.cryptocurrency.service.model.CryptoCurrencyConvert;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author yuvaraj.sanjeevi
 */
public interface CryptoCurrencyService {


    void loadInitialData();

    List<CryptoCurrency> getAll();

    void convertToLocalCurrency(CryptoCurrencyConvert cryptoCurrencyConvert, HttpServletRequest request, Model model);
}
