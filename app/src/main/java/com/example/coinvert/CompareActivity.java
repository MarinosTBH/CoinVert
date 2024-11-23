package com.example.coinvert;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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

public class CompareActivity extends AppCompatActivity {

    private static final String TAG = "CompareActivity";
    private static final String API_KEY = "d4da29424f4cb2241bae0066";
    private Retrofit retrofit;

    private TextView strongestTextView, weakestTextView;
    private Spinner currencySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        strongestTextView = findViewById(R.id.strongestCurrenciesTextView);
        weakestTextView = findViewById(R.id.weakestCurrenciesTextView);
        currencySpinner = findViewById(R.id.currencySpinner);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")  // Base URL
                .addConverterFactory(GsonConverterFactory.create())  // JSON parsing
                .build();

        setupCurrencySpinner();
        setupBottomNavBar();
    }

    private void setupCurrencySpinner() {
        // Predefined list of popular currencies
        String[] currencies = {"TND", "USD", "EUR", "GBP", "JPY", "AUD", "CAD"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        // Fetch exchange rates whenever a new currency is selected
        currencySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCurrency = currencies[position];
                fetchExchangeRates(selectedCurrency);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // Default action if nothing is selected
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

        // Update UI
        strongestTextView.setText( String.join("\n", strongestCurrencies));
        weakestTextView.setText(String.join("\n", weakestCurrencies));
    }
}
