package com.cryptocurrency.service.services.impl;

import com.cryptocurrency.service.config.CoinCapConfig;
import com.cryptocurrency.service.entity.CryptoCurrency;
import com.cryptocurrency.service.repository.CryptoCurrencyRepository;
import com.cryptocurrency.service.services.CryptoCurrencyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuvaraj.sanjeevi
 */
@Service
@AllArgsConstructor
@Slf4j
public class CryptoCurrencyServiceImpl implements CryptoCurrencyService {


    private final CryptoCurrencyRepository cryptoCurrencyRepository;
    private final CoinCapConfig coinCapConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void loadInitialData() {
        // clean existing data
        cryptoCurrencyRepository.truncateTable();

        ArrayNode cryptoCurrencies = getAllCryptoCurrencies();
        List<CryptoCurrency> cryptoCurrencyList = new ArrayList<>();
        for (JsonNode node : cryptoCurrencies) {
            if(node.hasNonNull("priceUsd")) {
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

    private ArrayNode getAllCryptoCurrencies() {
        ArrayNode response = objectMapper.createArrayNode();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + coinCapConfig.getApiKey());
        HttpEntity httpEntity = new HttpEntity(headers);
        int limit = 2000;
        int offset = 0;
        int page = 1;
        while (true) {
            try {
                String url = coinCapConfig.getAssetsUrl() + "?offset=" + offset + "&limit=" + limit;
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
