package com.example.coinvert;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coinvert.model.ExchangeRateResponse;
import com.example.coinvert.model.ExchangeRateService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConvertActivity extends AppCompatActivity {

    private static final String TAG = "ConvertActivity";
    private Retrofit retrofit;
    private final String apiKey = "d4da29424f4cb2241bae0066";  // Replace with your actual API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        // Initialize Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/")  // Base URL
                .addConverterFactory(GsonConverterFactory.create())  // JSON parsing
                .build();

        // Find the Spinners and other UI elements
        Spinner currencySpinnerFrom = findViewById(R.id.currencySpinnerFrom);
        Spinner currencySpinnerTo = findViewById(R.id.currencySpinnerTo);
        EditText amountEditText = findViewById(R.id.fromAmountTextView); // Input amount
        ImageButton convertButton = findViewById(R.id.convertButton); // Convert button
        TextView resultTextView = findViewById(R.id.convertedAmountTextView); // Conversion result
        TextView exchangeRateTextView = findViewById(R.id.exchangeRateTextView);

        // Find the BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        // Set the listener for bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item selection
                if (item.getItemId() == R.id.menu_profile) {
                    startActivity(new Intent(ConvertActivity.this, ProfileActivity.class)); // Open ProfileActivity
                } else if (item.getItemId() == R.id.menu_convert) {
                    startActivity(new Intent(ConvertActivity.this, ConvertActivity.class)); // Open ConvertActivity
                } else if (item.getItemId() == R.id.menu_logout) {
                    logout();
                }
                return true; // Return true to indicate the item was selected
            }
        });


        // Make the Retrofit call to get exchange rates
        ExchangeRateService service = retrofit.create(ExchangeRateService.class);
        Call<ExchangeRateResponse> call = service.getExchangeRates(apiKey, "USD");  // Using USD as the base currency

        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (response.isSuccessful()) {
                    ExchangeRateResponse exchangeRateResponse = response.body();
                    if (exchangeRateResponse != null && exchangeRateResponse.conversion_rates != null) {
                        // Extract all currencies
                        Map<String, Double> conversionRates = exchangeRateResponse.conversion_rates;
                        List<String> currencies = new ArrayList<>(conversionRates.keySet());

                        // Create an ArrayAdapter for the first Spinner (currencyFrom)
                        ArrayAdapter<String> adapterFrom = new ArrayAdapter<>(
                                ConvertActivity.this,
                                android.R.layout.simple_spinner_item,
                                currencies
                        );
                        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        currencySpinnerFrom.setAdapter(adapterFrom);

                        // Create an ArrayAdapter for the second Spinner (currencyTo)
                        ArrayAdapter<String> adapterTo = new ArrayAdapter<>(
                                ConvertActivity.this,
                                android.R.layout.simple_spinner_item,
                                new ArrayList<>(currencies) // Copy initial list for manipulation
                        );
                        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        currencySpinnerTo.setAdapter(adapterTo);

                        // Set listeners for the first Spinner
                        currencySpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedCurrency = currencies.get(position);
                                Toast.makeText(ConvertActivity.this, "Selected from: " + selectedCurrency, Toast.LENGTH_SHORT).show();

                                // Update the second spinner to exclude the selected currency
                                List<String> filteredCurrencies = new ArrayList<>(currencies);
                                filteredCurrencies.remove(selectedCurrency); // Remove the selected item
                                adapterTo.clear();
                                adapterTo.addAll(filteredCurrencies); // Add updated list to adapter
                                adapterTo.notifyDataSetChanged();

                                // Reset the second spinner to the first item in the filtered list
                                if (!filteredCurrencies.isEmpty()) {
                                    currencySpinnerTo.setSelection(0);
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // No action needed
                            }
                        });

                        // Convert button click listener
                        convertButton.setOnClickListener(v -> {
                            // Get the selected currencies
                            String currencyFrom = currencySpinnerFrom.getSelectedItem().toString();
                            String currencyTo = currencySpinnerTo.getSelectedItem().toString();

                            // Get the input amount
                            String amountText = amountEditText.getText().toString();
                            if (amountText.isEmpty()) {
                                Toast.makeText(ConvertActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double amount = Double.parseDouble(amountText);

                            // Make the Retrofit call to get exchange rates for the selected currencies
                            Call<ExchangeRateResponse> callConversion = service.getExchangeRates(apiKey, currencyFrom);
                            callConversion.enqueue(new Callback<ExchangeRateResponse>() {
                                @Override
                                public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                                    if (response.isSuccessful()) {
                                        ExchangeRateResponse exchangeRateResponse = response.body();
                                        if (exchangeRateResponse != null && exchangeRateResponse.conversion_rates != null) {
                                            // Get the exchange rate for the target currency
                                            Double exchangeRate = exchangeRateResponse.conversion_rates.get(currencyTo);
                                            if (exchangeRate != null) {
                                                double result = amount * exchangeRate;
                                                resultTextView.setText(String.format("%.2f %s", result, currencyTo));
                                                exchangeRateTextView.setText(String.format("Indicative Exchange Rate\n1 %s = %.4f %s", currencyFrom, exchangeRate, currencyTo));
                                            } else {
                                                Toast.makeText(ConvertActivity.this, "Exchange rate not available for the selected currencies", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        Log.e(TAG, "API Request failed: " + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                                    Log.e(TAG, "API Request failed: " + t.getMessage());
                                }
                            });
                        });
                    }
                } else {
                    Log.e(TAG, "API Request failed: " + response.message());
                    Toast.makeText(ConvertActivity.this, "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                Log.e(TAG, "API Request failed: " + t.getMessage());
                Toast.makeText(ConvertActivity.this, "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    Logout function
// Optional: Log out functionality
public void logout() {
    // Clear the saved user email from SharedPreferences
    SharedPreferences sharedPreferences = getSharedPreferences("USER_PREF", MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.remove("USER_EMAIL");  // Remove the stored email
    editor.apply();  // Commit the changes

    // Navigate to MainActivity (or any other screen)
    Intent intent = new Intent(ConvertActivity.this, MainActivity.class);
    startActivity(intent);
    finish();  // Optionally finish the current activity
}
}
