package com.proyecto.transportesbahiacadiz.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.Zone;

import java.util.ArrayList;

public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ZoneViewHolder>{
    private ArrayList<Zone> itemList;

    public ZoneAdapter(ArrayList<Zone> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.zone_item, parent, false);
        return new ZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        //CreditCard currentItem = itemList.get(position);
        holder.letter.setText(itemList.get(position).getIdZona());
        holder.letter.setBackgroundColor(Color.parseColor(itemList.get(position).getColor()));
        holder.zone.setText(itemList.get(position).getNombreZona());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ZoneViewHolder extends RecyclerView.ViewHolder{
        public TextView letter;
        public TextView zone;

        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            letter = itemView.findViewById(R.id.text_view_letter);
            zone = itemView.findViewById(R.id.text_view_zone);
        }
    }
}
