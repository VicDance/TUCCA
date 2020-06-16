package com.proyecto.transportesbahiacadiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.R;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder>{
    private ArrayList<String> itemList;

    public TripAdapter(ArrayList<String> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_item, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        String currentItem = itemList.get(position);
        holder.trip.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder{
        public TextView trip;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            trip = itemView.findViewById(R.id.text_view_trips);
        }
    }
}
