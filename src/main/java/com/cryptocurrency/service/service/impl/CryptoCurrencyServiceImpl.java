package com.cryptocurrency.service.service.impl;

import com.cryptocurrency.service.config.ExternalApiConfig;
import com.cryptocurrency.service.entity.CryptoCurrency;
import com.cryptocurrency.service.model.CryptoCurrencyConvert;
import com.cryptocurrency.service.model.CryptoCurrencyConvertResponse;
import com.cryptocurrency.service.repository.CryptoCurrencyRepository;
import com.cryptocurrency.service.service.CryptoCurrencyService;
import com.cryptocurrency.service.service.CurrencyService;
import com.cryptocurrency.service.service.GeoIPLocationService;
import com.cryptocurrency.service.util.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Layer for Crypto Currency Conversion
 *
 * @author yuvaraj.sanjeevi
 */
@Service
@AllArgsConstructor
@Slf4j
public class CryptoCurrencyServiceImpl implements CryptoCurrencyService {


    private final CryptoCurrencyRepository cryptoCurrencyRepository;
    private final ExternalApiConfig externalApiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GeoIPLocationService geoIPLocationService;
    private final CurrencyService currencyService;

    /**
     * This method is used to truncate existing data and pull , store latest data in DB
     */
    @Override
    public void loadInitialData() {
        // clean existing data
        cryptoCurrencyRepository.truncateTable();

        ArrayNode cryptoCurrencies = getAllCryptoCurrencies();
        List<CryptoCurrency> cryptoCurrencyList = new ArrayList<>();
        for (JsonNode node : cryptoCurrencies) {
            if (node.hasNonNull("priceUsd")) {
                CryptoCurrency cryptoCurrency = new CryptoCurrency();
                cryptoCurrency.setCode(node.get("id").asText());
                cryptoCurrency.setName(node.get("name").asText());
                cryptoCurrency.setSymbol(node.get("symbol").asText());
                cryptoCurrency.setRank(node.get("rank").asLong());
                cryptoCurrency.setRateUSD(new BigDecimal(node.get("priceUsd").asText()));
                cryptoCurrencyList.add(cryptoCurrency);
            }
        }
        cryptoCurrencyRepository.saveAll(cryptoCurrencyList);
    }

    /**
     * This method is used to get all the crypto currencies from the table
     * @return       - returns  List<com.cryptocurrency.service.model.CryptoCurrency>
     */
    @Override
    public List<com.cryptocurrency.service.model.CryptoCurrency> getAll() {
        return objectMapper.convertValue(cryptoCurrencyRepository.findAll(), new TypeReference<>() {
        });
    }

    /**
     * This method is used to convert to local currency
     * @param cryptoCurrencyConvert     - crypto currency convert request
     * @param request                   - http servlet request
     * @param model                     - model
     */
    @Override
    public void convertToLocalCurrency(CryptoCurrencyConvert cryptoCurrencyConvert, HttpServletRequest request, Model model) {
        CryptoCurrency cryptoCurrency = this.findCryptoCurrencyBySymbol(cryptoCurrencyConvert.getCryptoCurrencySymbol());
        String ipAddress = StringUtils.defaultIfBlank(cryptoCurrencyConvert.getIpAddress(), HttpUtil.getClientIP(request));
        CryptoCurrencyConvertResponse cryptoCurrencyConvertResponse = new CryptoCurrencyConvertResponse();
        try {
            // get location by ip
            String country = geoIPLocationService.getLocation(ipAddress);
            // convert to local currency
            String conversionResponse = currencyService.convertToLocalCurrency(cryptoCurrency, country);
            cryptoCurrencyConvertResponse.setSuccess(conversionResponse);
        } catch (Exception e) {
            log.error("Error Occurred", e);
            cryptoCurrencyConvertResponse.setError(e.getMessage());
        }
        model.addAttribute("convertResponse", cryptoCurrencyConvertResponse);
    }

    @Override
    public void handleWSResponse(JsonNode wsResponse) {
        Map<String, BigDecimal> latestPrices = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> iterator = wsResponse.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            latestPrices.put(entry.getKey(), new BigDecimal(entry.getValue().asText()));
        }
        List<CryptoCurrency> cryptoCurrencyList = cryptoCurrencyRepository.findByCodeIn(latestPrices.keySet());
        if(!CollectionUtils.isEmpty(cryptoCurrencyList)) {

            cryptoCurrencyList.forEach(cryptoCurrency -> {
                BigDecimal latestPrice = latestPrices.get(cryptoCurrency.getCode());
                Optional.ofNullable(latestPrice).ifPresent(cryptoCurrency::setRateUSD);
            });

            cryptoCurrencyRepository.saveAll(cryptoCurrencyList);
        }

    }

    /**
     * This method is used to find the crypto currency from DB by symbol
     * @param cryptoCurrencySymbol      - cyrptocurrency symbol
     * @return                          - cryptocurrency entity
     */
    private CryptoCurrency findCryptoCurrencyBySymbol(String cryptoCurrencySymbol) {
        return Optional.ofNullable(cryptoCurrencyRepository.findTopBySymbol(cryptoCurrencySymbol)).orElseThrow(() -> new RuntimeException("Invalid Cryptocurrency"));
    }

    /**
     * This method is used to call external API and get all crypto currencies
     * @return      - return array of crypto currencies
     */
    private ArrayNode getAllCryptoCurrencies() {
        ArrayNode response = objectMapper.createArrayNode();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + externalApiConfig.getApiKey());
        HttpEntity httpEntity = new HttpEntity(headers);
        int limit = 2000;
        int offset = 0;
        int page = 1;
        while (true) {
            try {
                String url = externalApiConfig.getAssetsUrl() + "?offset=" + offset + "&limit=" + limit;
                ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JsonNode.class);
                if ((responseEntity.getBody() != null && responseEntity.getBody().get("data") != null && responseEntity.getBody().get("data").size() > 0)) {
                    ArrayNode arrayNode = (ArrayNode) responseEntity.getBody().get("data");
                    response.addAll(arrayNode);
                } else {
                    break;
                }
                offset = (limit * page) + 1;
                page++;
            } catch (Exception ex) {
                log.error("Error while consuming assets api", ex);
                break;
            }
        }
        return response;
    }
}
