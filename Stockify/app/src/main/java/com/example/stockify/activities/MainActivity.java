package com.example.stockify.activities;

import static com.scichart.charting3d.interop.eTSRPlatform.Android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stockify.R;
import com.example.stockify.adapters.CompanyAdapter;
import com.example.stockify.model.Company;
import com.scichart.charting.visuals.SciChartSurface;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView companyRV;
    private CompanyAdapter adapter;
    private ArrayList<Company> companyArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpSciChartLicense();
        setContentView(R.layout.activity_main);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        initializeSpinner(spinner);

        companyRV = findViewById(R.id.idRVCompanies);
        SearchView searchView = findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        buildRecyclerView();
    }
    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<Company> filteredList = new ArrayList<Company>();

        // running a for loop to compare elements.
        for (Company item : companyArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredList);
        }
    }

    private void buildRecyclerView() {

        // below line we are creating a new array list
        companyArrayList = new ArrayList<Company>();

        // below line is to add data to our array list.
        companyArrayList.add(new Company("AACG", "ATA Creativity Global"));
        companyArrayList.add(new Company("AAPL", "Apple Inc."));
        companyArrayList.add(new Company("ABNB", "Airbnb, Inc."));
        companyArrayList.add(new Company("AMZN", "Amazon.com, Inc."));
        companyArrayList.add(new Company("TSLA", "Tesla, Inc."));

        // initializing our adapter class.
        adapter = new CompanyAdapter(companyArrayList, MainActivity.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        companyRV.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        companyRV.setLayoutManager(manager);

        // setting adapter to
        // our recycler view.
        companyRV.setAdapter(adapter);
    }
    private void setUpSciChartLicense() {
        try {
            SciChartSurface.setRuntimeLicenseKey("UhUDqx6TcvunlrRj4ug7MRoZ99PJxHD1d4MKMXmNZFINgkg35puTv351I6HYq08eRdsokKqnZhfmKy0OJ42xfoctt9WX/9K/hrVHPT+OaITr3q2q4uxIEizn9QX8GK57F5k3vilGIouuy5IGyiON8IT6WymTRNCkDb81EP/Cshn/GNSqCW5V+lhU0mL4pdfxVL4XCuLeYWEjMurIZPjtCDukksrhAaZo0Cf0GN/K2L326PYfCbr8fBk1g6QMtlD7EkJ3q4B1pU+G6Vui7py9TU5iDcvKriWMtpLj0N9P3WN9yprR4Mjuj+EXzZ3/RkndZqE2Of3EbgHkz6hhXfiZBF5+3kS5XR8ubHuUSmO+AURdMbMixBgXF+djz5HsCKvzm3H/UruegavlEfuwyRTiKK7mQnOEr4SmEZekGbShOiHpzvTn4kEX+5r86eJnIQL7/Bq9Ew0hClR4D8j6RLSPfgqcUJ53Gn5EkXDHygr/3fUjFWCIRb3znVqSCeJLa0+6+0PSOSDKYA5NY4qxZAmvRCF17UYR");
        } catch (Exception e) {
            Log.e("SciChart", "Error when setting the license", e);
        }
    }



    private void initializeSpinner(Spinner spinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.values_array, R.layout.spinner_item_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}