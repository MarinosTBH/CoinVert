package com.example.coinvert.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ExchangeRateService {
//    https://v6.exchangerate-api.com/v6/d4da29424f4cb2241bae0066/latest/USD
    @GET("v6/{apiKey}/latest/{baseCurrency}")
    Call<ExchangeRateResponse> getExchangeRates(
            @Path("apiKey") String apiKey,
            @Path("baseCurrency") String baseCurrency
    );
}
