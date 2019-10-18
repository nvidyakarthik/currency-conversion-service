package com.gymnasticbodies.microservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeServiceProxy proxy;

    //@Value("${CURRENCY_EXCHANGE_URI:http://naming-server:8001}")
    @Value("${CURRENCY_EXCHANGE_URI:http://localhost:8000}")
    private String currencyExchangeHost;

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
        Map<String,String> uriVariables= new HashMap<>();
        uriVariables.put("from",from);
        uriVariables.put("to",to);
        ResponseEntity<CurrencyConversionBean> responseEntity=new RestTemplate().getForEntity((currencyExchangeHost +"/api/currency-exchange-microservice/currency-exchange/from/{from}/to/{to}"),CurrencyConversionBean.class,uriVariables);
        CurrencyConversionBean response=responseEntity.getBody();
        return new CurrencyConversionBean(response.getId(),from,to,response.getConversionMultiple(),quantity,quantity.multiply(response.getConversionMultiple()),response.getPort());

    }

    /*@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyfeign(@PathVariable String from, @PathVariable String to, @PathVariable BigDecimal quantity){
        CurrencyConversionBean response=proxy.retrieveExchangeValue(from,to);
        logger.info("{}",response);
        return new CurrencyConversionBean(response.getId(),from,to,response.getConversionMultiple(),quantity,quantity.multiply(response.getConversionMultiple()),response.getPort());
    }*/


    @GetMapping("/currency-converter/from")
    public ResponseEntity<List<String>> getFromCurrency() {
        List<CurrencyConversionBean>  responseExchangeList=proxy.getCurrencyExchangeValueList();
        List<String> fromCurrencyList =responseExchangeList.stream().map(u -> u.getFrom()).collect(Collectors.toList());
        return new ResponseEntity<>(fromCurrencyList, HttpStatus.OK);
    }

    @GetMapping("/currency-converter/to")
    public ResponseEntity<List<String>> getToCurrency() {
        List<CurrencyConversionBean>  responseExchangeList=proxy.getCurrencyExchangeValueList();
        List<String> toCurrencyList =responseExchangeList.stream().map(u -> u.getTo()).collect(Collectors.toList());
        return new ResponseEntity<>(toCurrencyList, HttpStatus.OK);
    }


}
