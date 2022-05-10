package com.cryptocurrency.service.service.impl;

import com.cryptocurrency.service.TestUtil;
import com.cryptocurrency.service.config.ExternalApiConfig;
import com.cryptocurrency.service.entity.CryptoCurrency;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * @author yuvaraj.sanjeevi
 */
class CurrencyServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ExternalApiConfig externalApiConfig;

    @InjectMocks
    private CurrencyServiceImpl currencyServiceImpl;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    void convertToLocalCurrency_ShouldThrowException() {
        ObjectNode emptyNode = TestUtil.createObjectMapper().createObjectNode();
        when(externalApiConfig.getCurrencyRatesUrl()).thenReturn("http://localhost");
        when(restTemplate.getForObject(anyString(), any())).thenReturn(emptyNode);

        CryptoCurrency cryptoCurrency = Mockito.mock(CryptoCurrency.class);
        String countryCode = "GB";
        RuntimeException runtimeException = Assertions.assertThrows(RuntimeException.class, () -> currencyServiceImpl.convertToLocalCurrency(cryptoCurrency, countryCode));

        Assertions.assertEquals("Unable to get rates", runtimeException.getMessage());
    }

    @Test
    void convertToLocalCurrency_ShouldSuccess() {
        String responseStr = "{\"amount\":10.0,\"base\":\"USD\",\"date\":\"2022-05-09\",\"rates\":{\"GBP\":8.0723}}";
        ObjectNode response = TestUtil.convertJsonStrToObject(responseStr, ObjectNode.class);
        when(externalApiConfig.getCurrencyRatesUrl()).thenReturn("http://localhost");
        when(restTemplate.getForObject(anyString(), any())).thenReturn(response);

        CryptoCurrency cryptoCurrency = Mockito.mock(CryptoCurrency.class);
        String countryCode = "GB";
        String localCurrencyResponse = currencyServiceImpl.convertToLocalCurrency(cryptoCurrency, countryCode);

        Assertions.assertEquals("Current unit price is \n" + "£8.07", localCurrencyResponse);
    }

}
