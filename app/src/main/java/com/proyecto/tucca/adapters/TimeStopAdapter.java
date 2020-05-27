package com.proyecto.tucca.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.tucca.R;
import com.proyecto.tucca.model.TimeStopsItem;

import java.util.ArrayList;

public class TimeStopAdapter extends RecyclerView.Adapter<TimeStopAdapter.TimeStopsViewHolder>{
    private ArrayList<TimeStopsItem> itemList;

    public static class TimeStopsViewHolder extends RecyclerView.ViewHolder{
        public ImageView lineNumber;
        public TextView time;
        public TextView to;

        public TimeStopsViewHolder(@NonNull View itemView) {
            super(itemView);
            lineNumber = itemView.findViewById(R.id.image_number_stop);
            time = itemView.findViewById(R.id.time_remaining);
            to = itemView.findViewById(R.id.to);
        }
    }

    public TimeStopAdapter(ArrayList<TimeStopsItem> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public TimeStopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_stops_item, parent, false);
        TimeStopAdapter.TimeStopsViewHolder viewHolder = new TimeStopAdapter.TimeStopsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeStopAdapter.TimeStopsViewHolder holder, int position) {
        TimeStopsItem currentItem = itemList.get(position);
        //holder.lineNumber.setI
        holder.time.setText(currentItem.getTiempoLLegada());
        holder.to.setText(currentItem.getDestino());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
