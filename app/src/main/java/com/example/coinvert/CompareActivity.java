package com.example.coinvert;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coinvert.model.ExchangeRateResponse;
import com.example.coinvert.model.ExchangeRateService;
import com.example.coinvert.utils.NavBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class CompareActivity extends AppCompatActivity {

    private static final String TAG = "CompareActivity";
    private static final String API_KEY = "d4da29424f4cb2241bae0066";
    private Retrofit retrofit;

    private ListView currencyListView, strongestCurrenciesListView, weakestCurrenciesListView;
    private EditText currencySearchInput;

    private List<String> currencyList;
    private ArrayAdapter<String> currencyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        currencyListView = findViewById(R.id.currencyListView);
        currencySearchInput = findViewById(R.id.currencySearchInput);
        strongestCurrenciesListView = findViewById(R.id.strongestCurrenciesListView);
        weakestCurrenciesListView = findViewById(R.id.weakestCurrenciesListView);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")  // Base URL
                .addConverterFactory(GsonConverterFactory.create())  // JSON parsing
                .build();

        initializeCurrencyList();
        setupSearchInput();
        setupBottomNavBar();
    }

    private void initializeCurrencyList() {
        // Predefined list of popular currencies
        currencyList = new ArrayList<>();
        currencyList.add("TND");
        currencyList.add("USD");
        currencyList.add("EUR");
        currencyList.add("GBP");
        currencyList.add("JPY");
        currencyList.add("AUD");
        currencyList.add("CAD");
        currencyList.add("CHF");
        currencyList.add("CNY");
        currencyList.add("HKD");
        currencyList.add("INR");
        currencyList.add("NZD");

        // Set adapter for the ListView
        currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencyList);
        currencyListView.setAdapter(currencyAdapter);

        // Handle currency selection
        currencyListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCurrency = currencyAdapter.getItem(position);
            fetchExchangeRates(selectedCurrency);
        });
    }

    private void setupSearchInput() {
        // Filter currency list based on input
        currencySearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currencyAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupBottomNavBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavBar.setupBottomNav(bottomNavigationView, CompareActivity.this);
    }

    private void fetchExchangeRates(String baseCurrency) {
        ExchangeRateService service = retrofit.create(ExchangeRateService.class);
        Call<ExchangeRateResponse> call = service.getExchangeRates(API_KEY, baseCurrency);

        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Double> conversionRates = response.body().conversion_rates;

                    if (conversionRates != null) {
                        displayStrongestAndWeakestCurrencies(conversionRates);
                    } else {
                        Toast.makeText(CompareActivity.this, "No conversion rates available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API Request failed: " + response.message());
                    Toast.makeText(CompareActivity.this, "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e(TAG, "API Request failed: " + t.getMessage());
                Toast.makeText(CompareActivity.this, "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayStrongestAndWeakestCurrencies(Map<String, Double> conversionRates) {
        // Convert map to a sorted list
        List<Map.Entry<String, Double>> sortedRates = new ArrayList<>(conversionRates.entrySet());
        sortedRates.sort(Map.Entry.comparingByValue());

        // Get strongest currencies (smallest rates)
        List<String> strongestCurrencies = sortedRates.stream()
                .limit(3)
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.toList());

        // Get weakest currencies (largest rates)
        List<String> weakestCurrencies = sortedRates.stream()
                .skip(sortedRates.size() - 3)
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.toList());

        // Update ListView for strongest and weakest currencies
        strongestCurrenciesListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strongestCurrencies));
        weakestCurrenciesListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, weakestCurrencies));
    }
}
