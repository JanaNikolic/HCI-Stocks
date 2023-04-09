package com.example.stockify.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.stockify.R;
import com.example.stockify.adapters.CompanyAdapter;
import com.example.stockify.adapters.WatchItemAdapter;
import com.example.stockify.model.Company;
import com.example.stockify.model.WatchItem;
import com.google.android.material.snackbar.Snackbar;
import com.scichart.charting.visuals.SciChartSurface;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LIST_STATE_KEY = "watchList";
    private static final String LIST_ARRAY_KEY = "watchListArray";
    private RecyclerView companyRV;
    private RecyclerView watchListRV;
    private CompanyAdapter adapter;
    private WatchItemAdapter adapterWatchList;
    private ArrayList<Company> companyArrayList;
    private ArrayList<WatchItem> watchArrayList;
    private RecyclerView.ItemAnimator viewAnimator;
    private LinearLayoutManager manager;
    private LinearLayoutManager managerWatchList;
    private Parcelable watchListState;
    private ScrollView constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            watchListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
            watchArrayList = savedInstanceState.getParcelableArrayList(LIST_ARRAY_KEY);
            buildWatchListView();
        } else {
            watchArrayList = new ArrayList<WatchItem>();

            buildWatchListView();
        }
        companyArrayList = new ArrayList<Company>();
        companyRV = findViewById(R.id.idRVCompanies);

        buildRecyclerView();
        setUpSciChartLicense();
        constraintLayout = findViewById(R.id.constraintLayout);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        initializeSpinner(spinner);


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

        enableSwipeToDeleteAndUndo();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        watchListState = watchListRV.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        watchListRV.getLayoutManager().onRestoreInstanceState(watchListState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        watchListState = watchListRV.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, watchListState);
        outState.putParcelableArrayList(LIST_ARRAY_KEY, watchArrayList);
//        Toast.makeText(MainActivity.this, "Save " + watchArrayList.size() +"  ", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        watchArrayList = savedInstanceState.getParcelableArrayList(LIST_ARRAY_KEY);
        watchListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        watchListRV.getLayoutManager().onRestoreInstanceState(watchListState);
        super.onRestoreInstanceState(savedInstanceState);
//        Toast.makeText(MainActivity.this, "Restore " + watchArrayList.size() +"  ", Toast.LENGTH_SHORT).show();
    }

    private void buildWatchListView() {
        watchListRV = findViewById(R.id.watchListRv);
        adapterWatchList = new WatchItemAdapter(watchArrayList, MainActivity.this);
        viewAnimator = new DefaultItemAnimator();

        watchListRV.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        managerWatchList = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        watchListRV.setLayoutManager(managerWatchList);

        watchListRV.setItemAnimator(viewAnimator);
        watchListRV.scrollToPosition(adapterWatchList.getItemCount() - 1);

        // setting adapter to
        // our recycler view.
        watchListRV.setAdapter(adapterWatchList);

        // TODO swipe for delete
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

        manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        // below line is to add data to our array list.
        companyArrayList.add(new Company("AACG", "ATA Creativity Global"));
        companyArrayList.add(new Company("AAPL", "Apple Inc."));
        companyArrayList.add(new Company("ABNB", "Airbnb, Inc."));
        companyArrayList.add(new Company("AMZN", "Amazon.com, Inc."));
        companyArrayList.add(new Company("TSLA", "Tesla, Inc."));

        // initializing our adapter class.
        adapter = new CompanyAdapter(companyArrayList, MainActivity.this);

        adapter.setCallback(new CompanyAdapter.ViewHolder.Callback() {
            @Override
            public void onItemClick(int position) {
                // you can get your clicked item here
                // now you can put texts object to another RecyclerView :)
                Toast.makeText(MainActivity.this, "Recycle Click" + position +"  ", Toast.LENGTH_SHORT).show();
                Company company = adapter.get(position);
                WatchItem watchItem = new WatchItem(company.getSymbol(), company.getName(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
                if (!watchArrayList.contains(watchItem) && watchArrayList.size() < 4)
                    watchArrayList.add(watchItem);
                else if (watchArrayList.size() == 4) {
                    Snackbar.make(constraintLayout, "You can only watch 4 at the time. Remove one by swiping to left.", Snackbar.LENGTH_LONG)
                            .show();
                }
                adapterWatchList.notifyDataSetChanged();

                //TODO open GraphActivity
            }
        });
        // adding layout manager to our recycler view.

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

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final WatchItem item = adapterWatchList.getData().get(position);

                adapterWatchList.removeItem(position);
                watchListRV.scrollToPosition(position);


                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        adapterWatchList.restoreItem(item, position);
                        watchListRV.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.parseColor("#73508D"));
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(watchListRV);
    }

}