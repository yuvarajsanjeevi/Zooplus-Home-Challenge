# Spring Boot Crypto Currency Application

# Implementation Approach

 * Spring WebSocket Manager will connect with the external Websocket uri, receive the crypto currency prices data continuously and update in DB
 * Store the Crytocurrency price in DB as USD price
 * Get IP Address from client input or request then get country from IP using (maxmind geoip2) and convert to local currency
 * When Receive convert to local currency request then get crypto currency price in USD and call external service to convert to local currency

# Assumption
 *  Prices of crypto currency to updated based on market data
 *  Supported only major used crypto currencies(1000+)
 *  Support only single unit price to local currency(not multi unit)
 
# Used following tech stack in this project
   * Java 11
   * Maven >= 3.3.9
   * Spring boot 2.6.7
   * H2 Database
   * Mockito
   * Lombok 
 
Project Summary
   * Consume live prices from websocket API
   * Created form to show cryptocurrency drop down and convert button
   * Junit + Mockito for unit testing
   * Spotify maven plugin for docker image build
    
### Build this docker
  * With Maven Plugin
  
       `mvn clean install dockerfile:build`
  * With Docker File

      `docker build -t zooplus/cryptocurrency-service:latest .`

### Show docker images
`docker images`

### Run our image
`docker run -dp 9000:9000 --name cryptocurrency-service -t zooplus/cryptocurrency-service:latest`

### Application URLs

   `http://<MACHINE-IP>:9000/h2-console` - h2 console
   
   ![alt text](/sample-results/H2-Console.png)
   
   `http://<MACHINE-IP>:9000/cryptoCurrencies/convertToLocalCurrency` - UI Dropdown
   
   ![alt text](/sample-results/CryptoCurrency_Dropdown.png)
   
   `http://<MACHINE-IP>:9000/cryptoCurrencies/convertToLocalCurrency` - BitCoin(BTC) To United Kingdom Currency (GBP)
   
   ![alt text](/sample-results/BTC_UK.png)
   
   `http://<MACHINE-IP>:9000/cryptoCurrencies/convertToLocalCurrency` - BitCoin(BTC) To Germany Currency(EURO)
      
   ![alt text](/sample-results/BTC_DE.png)
   
   `http://<MACHINE-IP>:9000/cryptoCurrencies/convertToLocalCurrency` - Ethereum(ETH) To India Currency(INR)
      
   ![alt text](/sample-results/ETH_IN.png)   

   `http://<MACHINE-IP>:9000/cryptoCurrencies/convertToLocalCurrency` - Ethereum(ETH) To United States America Currency(USA)
      
   ![alt text](/sample-results/ETH_US.png)   
   
### Future Enhancements
  * Store USD rate to Country Local Currency rate in Table and use it instead of External API
