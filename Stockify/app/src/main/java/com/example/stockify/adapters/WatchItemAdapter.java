package com.example.stockify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockify.R;
import com.example.stockify.activities.GraphActivity;
import com.example.stockify.activities.MainActivity;
import com.example.stockify.model.WatchItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class WatchItemAdapter  extends RecyclerView.Adapter<WatchItemAdapter.ViewHolder> {

    private ArrayList<WatchItem> watchItemArrayList;
    private DecimalFormat fmt = new DecimalFormat("#,##0.00");
    private int cardBackground;
    private int percentageChangeBackground;
    private String sign = "";
    private Context context;

    public WatchItemAdapter(ArrayList<WatchItem> courseModelArrayList, Context context) {
        this.watchItemArrayList = courseModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WatchItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.watch_list_item_card, parent, false);
        return new WatchItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchItemAdapter.ViewHolder holder, int position) {
        WatchItem model = watchItemArrayList.get(position);
        holder.watchItemNameTV.setText(model.getSymbol());
        holder.watchItemSymbolTV.setText(model.getName());
//        holder.watchItemHighTV.setText(fmt.format(model.getHigh()));

        cardBackground = R.drawable.background_watch_list_item_red;
        holder.layout.setBackgroundResource(cardBackground);
//        holder.percentChangeTv.setBackgroundResource(percentageChangeBackground);

        if (model.getChange() > 0)
            sign = "+";
        else
            sign = "";
//        holder.percentChangeTv.setText(sign + fmt.format(model.getChange()) + "%");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GraphActivity.class);
                intent.putExtra("item", model);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return watchItemArrayList.size();
    }

    public void updateList(ArrayList<WatchItem> list){
        this.watchItemArrayList = list;
    }

    public WatchItem get(int position) {
        return watchItemArrayList.get(position);
    }

    public void delete(int position) {
        watchItemArrayList.remove(position);
    }
    public void removeItem(int position) {
        watchItemArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(WatchItem item, int position) {
        watchItemArrayList.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<WatchItem> getData() {
        return watchItemArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views.
        private final TextView watchItemNameTV;
        private final TextView watchItemSymbolTV;
//        private final TextView watchItemHighTV;
//        private final TextView percentChangeTv;
        private final ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            watchItemNameTV = itemView.findViewById(R.id.companyNameTv);
            watchItemSymbolTV = itemView.findViewById(R.id.companySymbolTv);
//            watchItemHighTV = itemView.findViewById(R.id.highTv);
//            percentChangeTv = itemView.findViewById(R.id.percentChangeTv);
            layout = itemView.findViewById(R.id.cardLayout);

        }
    }
}
