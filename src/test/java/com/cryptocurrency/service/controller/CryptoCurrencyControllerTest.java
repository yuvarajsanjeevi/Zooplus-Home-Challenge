package com.cryptocurrency.service.controller;

import com.cryptocurrency.service.TestUtil;
import com.cryptocurrency.service.model.CryptoCurrency;
import com.cryptocurrency.service.model.CryptoCurrencyConvert;
import com.cryptocurrency.service.service.CryptoCurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @author yuvaraj.sanjeevi
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CryptoCurrencyController.class)
@Import(CryptoCurrencyController.class)
class CryptoCurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void showForm() throws Exception {
        CryptoCurrency cryptoCurrency1 = Mockito.spy(CryptoCurrency.class);
        CryptoCurrency cryptoCurrency2 = Mockito.spy(CryptoCurrency.class);

        when(cryptoCurrencyService.getAll()).thenReturn(Arrays.asList(cryptoCurrency1, cryptoCurrency2));
        mockMvc.perform(get("/cryptoCurrencies/showForm"))
                .andExpect(model().attributeExists("cryptoCurrencyConvertRequest"))
                .andExpect(model().attribute("cryptoCurrencyList", hasSize(2)))
                .andExpect(view().name("index"));
    }

    @Test
    void convertToLocalCurrency() throws Exception {
        CryptoCurrency cryptoCurrency1 = Mockito.spy(CryptoCurrency.class);
        CryptoCurrency cryptoCurrency2 = Mockito.spy(CryptoCurrency.class);

        CryptoCurrencyConvert cryptoCurrencyConvert = new CryptoCurrencyConvert();
        cryptoCurrencyConvert.setCryptoCurrencySymbol("BTC");
        cryptoCurrencyConvert.setIpAddress("157.51.32.182");


        when(cryptoCurrencyService.getAll()).thenReturn(Arrays.asList(cryptoCurrency1, cryptoCurrency2));

        mockMvc.perform(post("/cryptoCurrencies/convertToLocalCurrency").content(TestUtil.convertObjectToJson(cryptoCurrencyConvert)))
                .andExpect(model().attributeExists("cryptoCurrencyConvertRequest"))
                .andExpect(model().attributeExists("cryptoCurrencyConvertRequest"))
                .andExpect(model().attribute("cryptoCurrencyList", hasSize(2)))
                .andExpect(view().name("index"));
    }
}
