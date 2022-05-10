package com.cryptocurrency.service.service.impl;

import com.cryptocurrency.service.TestUtil;
import com.cryptocurrency.service.config.ExternalApiConfig;
import com.cryptocurrency.service.entity.CryptoCurrency;
import com.cryptocurrency.service.model.CryptoCurrencyConvert;
import com.cryptocurrency.service.model.CryptoCurrencyConvertResponse;
import com.cryptocurrency.service.repository.CryptoCurrencyRepository;
import com.cryptocurrency.service.service.CurrencyService;
import com.cryptocurrency.service.service.GeoIPLocationService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

/**
 * @author yuvaraj.sanjeevi
 */
class CryptoCurrencyServiceImplTest {

    @Mock
    private CryptoCurrencyRepository cryptoCurrencyRepository;
    @Mock
    private RestTemplate restTemplate;
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private GeoIPLocationService geoIPLocationService;
    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CryptoCurrencyServiceImpl cryptoCurrencyServiceImpl;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, Boolean.FALSE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Test
    void getAll_ShouldGetEmpty() {
        when(cryptoCurrencyRepository.findAll()).thenReturn(new ArrayList<>());
        Assertions.assertEquals(0, cryptoCurrencyServiceImpl.getAll().size());
    }


    @Test
    void getAll_ShouldGetResults() {
        String findAllResponse = "[{\"id\":2278,\"code\":\"bitcoin\",\"name\":\"Bitcoin\",\"rank\":1,\"symbol\":\"BTC\",\"rateUSD\":33805.58000000,\"createdAt\":1652070660074,\"updatedAt\":1652071620498},{\"id\":2279,\"code\":\"ethereum\",\"name\":\"Ethereum\",\"rank\":2,\"symbol\":\"ETH\",\"rateUSD\":2475.39000000,\"createdAt\":1652070660137,\"updatedAt\":1652071617712}]";

        List<CryptoCurrency> cryptoCurrencyList = TestUtil.convertJsonStrToObject(findAllResponse, new TypeReference<>() {});

        when(cryptoCurrencyRepository.findAll()).thenReturn(cryptoCurrencyList);

        Assertions.assertEquals(cryptoCurrencyServiceImpl.getAll().size(), cryptoCurrencyList.size());
    }

    @Test
    void convertToLocalCurrency_ShouldThrowInvalidCryptoCurrency() {
        CryptoCurrencyConvert cryptoCurrencyConvert = new CryptoCurrencyConvert();
        cryptoCurrencyConvert.setCryptoCurrencySymbol("ABCD");

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Model model = Mockito.mock(Model.class);

        String errorMessage = "Invalid Cryptocurrency";
        when(cryptoCurrencyRepository.findTopBySymbol(anyString())).thenThrow(new RuntimeException(errorMessage));
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            cryptoCurrencyServiceImpl.convertToLocalCurrency(cryptoCurrencyConvert, httpServletRequest, model);
        });
        Assertions.assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void convertToLocalCurrency_ShouldThrowInvalidIP() throws IOException, GeoIp2Exception {
        CryptoCurrencyConvert cryptoCurrencyConvert = new CryptoCurrencyConvert();
        cryptoCurrencyConvert.setCryptoCurrencySymbol("BTC");
        cryptoCurrencyConvert.setIpAddress("12.");

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Model model = Mockito.spy(ConcurrentModel.class);
        CryptoCurrency cryptoCurrency = Mockito.mock(CryptoCurrency.class);

        String errorMessage = "Invalid IP";
        when(cryptoCurrencyRepository.findTopBySymbol(anyString())).thenReturn(cryptoCurrency);
        when(geoIPLocationService.getLocation(anyString())).thenThrow(new GeoIp2Exception(errorMessage));

        cryptoCurrencyServiceImpl.convertToLocalCurrency(cryptoCurrencyConvert, httpServletRequest, model);

        Assertions.assertEquals(errorMessage, ((CryptoCurrencyConvertResponse)model.getAttribute("convertResponse")).getError());
    }

    @Test
    void convertToLocalCurrency_ShouldSuccess() throws IOException, GeoIp2Exception {
        CryptoCurrencyConvert cryptoCurrencyConvert = new CryptoCurrencyConvert();
        cryptoCurrencyConvert.setCryptoCurrencySymbol("BTC");
        cryptoCurrencyConvert.setIpAddress("157.51.32.182");

        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
        Model model = Mockito.spy(ConcurrentModel.class);
        CryptoCurrency cryptoCurrency = Mockito.mock(CryptoCurrency.class);

        String currencyResponse = "Current unit price is\n" + "₹ 190,391.00";
        when(cryptoCurrencyRepository.findTopBySymbol(anyString())).thenReturn(cryptoCurrency);
        when(geoIPLocationService.getLocation(anyString())).thenReturn("IN");
        when(currencyService.convertToLocalCurrency(any(), any())).thenReturn(currencyResponse);

        cryptoCurrencyServiceImpl.convertToLocalCurrency(cryptoCurrencyConvert, httpServletRequest, model);
        Assertions.assertEquals(currencyResponse, ((CryptoCurrencyConvertResponse)model.getAttribute("convertResponse")).getSuccess());
    }

    @Test
    void handleWSResponse_ShouldSuccess() {
        String mockWSResponseStr = "{\"bitcoin\":\"33664.87\",\"lobstex\":\"0.018498\",\"jointer\":\"0.024346\",\"openocean\":\"0.074098\",\"unilayer\":\"0.123510\",\"ocean-protocol\":\"0.352370\",\"lcg\":\"0.006947\",\"keep3rv1\":\"265.95\",\"kiwigo\":\"0.039652\"}";
        ObjectNode objectNode = TestUtil.convertJsonStrToObject(mockWSResponseStr, ObjectNode.class);
        CryptoCurrency cryptoCurrency1 = Mockito.spy(CryptoCurrency.class);
        CryptoCurrency cryptoCurrency2 = Mockito.spy(CryptoCurrency.class);


        when(cryptoCurrency1.getCode()).thenReturn("bitcoin");
        when(cryptoCurrency2.getCode()).thenReturn("lobstex");
        List<CryptoCurrency> cryptoCurrencyList = Arrays.asList(cryptoCurrency1, cryptoCurrency2);
        when(cryptoCurrencyRepository.findByCodeIn(anySet())).thenReturn(cryptoCurrencyList);
        when(cryptoCurrencyRepository.saveAll(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        cryptoCurrencyServiceImpl.handleWSResponse(objectNode);

        Assertions.assertEquals(33664.87d, cryptoCurrency1.getRateUSD().doubleValue());
        Assertions.assertEquals(0.018498d, cryptoCurrency2.getRateUSD().doubleValue());
    }


}
