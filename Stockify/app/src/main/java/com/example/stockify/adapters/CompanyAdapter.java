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
    private ViewHolder.Callback callback;

    public CompanyAdapter(ArrayList<Company> courseModelArrayList, Context context) {
        this.companyArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public CompanyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_card, parent, false);
        return new ViewHolder(view, new ViewHolder.Callback() {
            @Override
            public void onItemClick(int position) {
                if (callback != null) {
                    // get actual item from its position
//                    final Texts texts = getItemByPosition(position);
                    // send to adapter callback
                    callback.onItemClick(position);
                }
            }
        });
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

    public void updateList(ArrayList<Company> list){
        companyArrayList = list;
    }

    public Company get(int position)
    {
         return companyArrayList.get(position);
    }

    public void setCallback(ViewHolder.Callback callback) {
        this.callback = callback;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views.
        private final TextView companyNameTV;
        private final TextView companySymbolTV;
        private final Callback callback; // custom callback

        public ViewHolder(@NonNull View itemView, Callback callback) {
            super(itemView);
            this.callback = callback;
            // initializing our views with their ids.
            companyNameTV = itemView.findViewById(R.id.idTVCompanyName);
            companySymbolTV = itemView.findViewById(R.id.idTVCompanySymbol);

//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    int p=getLayoutPosition();
//                    System.out.println("LongClick: "+p);
//                    return true;// returning true instead of false, works for me
//                }
//            });
            itemView.setOnClickListener(view -> {
                this.callback.onItemClick(getAdapterPosition());
            });

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int p=getLayoutPosition();
//
//                    // TODO open GraphActivity
//                    Toast.makeText(itemView.getContext(), "Recycle Click" + p +"  ", Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        public interface Callback {
            void onItemClick(int position);
        }
    }


}
