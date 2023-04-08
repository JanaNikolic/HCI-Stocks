package com.example.stockify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockify.R;
import com.example.stockify.model.Company;

import java.util.ArrayList;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private ArrayList<Company> companyArrayList;

    public CompanyAdapter(ArrayList<Company> courseModelArrayList, Context context) {
        this.companyArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public CompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdapter.ViewHolder holder, int position) {
        Company model = companyArrayList.get(position);
        holder.companyNameTV.setText(model.getSymbol());
        holder.companySymbolTV.setText(model.getName());
    }

    @Override
    public int getItemCount() {
        return companyArrayList.size();
    }

    public void filterList(ArrayList<Company> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        companyArrayList = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views.
        private final TextView companyNameTV;
        private final TextView companySymbolTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            companyNameTV = itemView.findViewById(R.id.idTVCompanyName);
            companySymbolTV = itemView.findViewById(R.id.idTVCompanySymbol);
        }
    }
}
