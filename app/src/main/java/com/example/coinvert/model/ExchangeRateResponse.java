package com.example.coinvert.model;

import java.util.Map;

public class ExchangeRateResponse {
    public String base_code;
    public Map<String, Double> conversion_rates;

    // Getter for the conversion rates
    public Map<String, Double> getRates() {
        return conversion_rates;
    }
}
