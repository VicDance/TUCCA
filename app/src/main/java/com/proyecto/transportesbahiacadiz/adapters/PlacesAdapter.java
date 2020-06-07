package com.proyecto.transportesbahiacadiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Places;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlacesViewHolder>{
    private ArrayList<String> itemList;
    private OnItemClickListener mListener;

    public PlacesAdapter(ArrayList<String> itemList, OnItemClickListener itemClickListener){
        this.itemList = itemList;
        this.mListener = itemClickListener;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_item, parent, false);
        PlacesViewHolder viewHolder = new PlacesViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.PlacesViewHolder holder, int position) {
        String currentItem = itemList.get(position);
        holder.textCity.setText(currentItem);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textCity;
        OnItemClickListener mListener;

        public PlacesViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textCity = itemView.findViewById(R.id.text_view_city);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
