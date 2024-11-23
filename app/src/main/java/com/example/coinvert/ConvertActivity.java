package com.example.coinvert;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConvertActivity extends AppCompatActivity {

    private final HashMap<String, Double> exchangeRates = new HashMap<>(); // To store exchange rates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);

        // Data for the dropdown
        String[] currencies = {"TND", "USD", "EUR"};

        // Define exchange rates (relative to 1 Tunisian Dinar)
        exchangeRates.put("TND to USD", 0.32);
        exchangeRates.put("TND to EUR", 0.29);
        exchangeRates.put("USD to TND", 3.12);
        exchangeRates.put("USD to EUR", 0.91);
        exchangeRates.put("EUR to TND", 3.45);
        exchangeRates.put("EUR to USD", 1.10);

        // Find the Spinners and other UI elements
        Spinner currencySpinnerFrom = findViewById(R.id.currencySpinnerFrom);
        Spinner currencySpinnerTo = findViewById(R.id.currencySpinnerTo);
        EditText amountEditText = findViewById(R.id.fromAmountTextView); // Input amount
        ImageButton convertButton = findViewById(R.id.convertButton); // Convert button
        TextView resultTextView = findViewById(R.id.convertedAmountTextView); // Conversion result
        TextView exchangeRateTextView = findViewById(R.id.exchangeRateTextView);

        // Create an ArrayAdapter for the first Spinner
        ArrayAdapter<String> adapterFrom = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinnerFrom.setAdapter(adapterFrom);

        // Create an ArrayAdapter for the second Spinner
        ArrayAdapter<String> adapterTo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new ArrayList<>(Arrays.asList(currencies)) // Copy initial list for manipulation
        );
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinnerTo.setAdapter(adapterTo);

        // Set listeners for the first Spinner
        currencySpinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCurrency = currencies[position];
                Toast.makeText(ConvertActivity.this, "Selected from: " + selectedCurrency, Toast.LENGTH_SHORT).show();

                // Update the second spinner to exclude the selected currency
                List<String> filteredCurrencies = new ArrayList<>(Arrays.asList(currencies));
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

            // Calculate the conversion
            String conversionKey = currencyFrom + " to " + currencyTo;
            if (exchangeRates.containsKey(conversionKey)) {
                double rate = exchangeRates.get(conversionKey);
                double result = amount * rate;

                // Display the result
                resultTextView.setText(String.format("%.2f %s", result, currencyTo));
                exchangeRateTextView.setText(String.format("Indicative Exchange Rate \n1 %s = %.4f %s", currencyFrom, rate, currencyTo));
            } else {
                Toast.makeText(ConvertActivity.this, "Conversion rate not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
