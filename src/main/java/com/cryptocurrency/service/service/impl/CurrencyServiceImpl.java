package com.cryptocurrency.service.service.impl;

import com.cryptocurrency.service.config.ExternalApiConfig;
import com.cryptocurrency.service.entity.CryptoCurrency;
import com.cryptocurrency.service.service.CurrencyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Service Layer for Currency Conversion
 *
 * @author yuvaraj.sanjeevi
 */
@Service
@AllArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private static final Map<String, Pair<Currency, Locale>> CURRENCIES = new HashMap<>();
    private static final String USD = "USD";
    private static final String AMOUNT = "amount";
    private static final String FROM = "from";
    private static final String TO = "to";

    private final RestTemplate restTemplate;
    private final ExternalApiConfig externalApiConfig;

    static {
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                if (currency != null && locale.getLanguage().equalsIgnoreCase("en")) {
                    CURRENCIES.put(locale.getCountry(), Pair.of(currency, locale));
                }
            } catch (IllegalArgumentException e) {

            }
        }
    }


    /**
     * This method is used to convert the crypto currency to local currency
     * @param cryptoCurrency       - crypto currency entity
     * @param countryCode          - country code
     * @return                     - return response
     */
    @Override
    public String convertToLocalCurrency(CryptoCurrency cryptoCurrency, String countryCode) {
        Pair<Currency, Locale> pair = CURRENCIES.get(countryCode);
        BigDecimal localCurrencyRate = getUsdToLocalRate(cryptoCurrency.getRateUSD(), pair.getFirst().getCurrencyCode());
        return "Current unit price is \n" + DecimalFormat.getCurrencyInstance(pair.getSecond()).format(localCurrencyRate.doubleValue());
    }

    /**
     * This method is used to convert usd to local currency rate
     * @param usdAmount         - amount in usd
     * @param localCurrency     - local currency
     * @return                  - returns local currency equivalent
     */
    private BigDecimal getUsdToLocalRate(BigDecimal usdAmount, String localCurrency) {

        if (USD.equalsIgnoreCase(localCurrency)) {
            return usdAmount;
        }
        String url = UriComponentsBuilder.fromUriString(externalApiConfig.getCurrencyRatesUrl())
                .queryParam(AMOUNT, usdAmount)
                .queryParam(FROM, USD)
                .queryParam(TO, localCurrency).toUriString();
        JsonNode localCurrencyRate = restTemplate.getForObject(url, JsonNode.class);
        if (localCurrencyRate != null && localCurrencyRate.hasNonNull("rates") && localCurrencyRate.get("rates").hasNonNull(localCurrency)) {
            return localCurrencyRate.get("rates").get(localCurrency).decimalValue();
        }

        throw new RuntimeException("Unable to get rates");
    }


}
