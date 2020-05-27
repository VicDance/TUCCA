package com.proyecto.tucca.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.tucca.R;
import com.proyecto.tucca.model.StopsItem;

import java.util.ArrayList;

public class StopsAdapter extends RecyclerView.Adapter<StopsAdapter.StopsViewHolder> {
    private ArrayList<StopsItem> itemList;
    private StopsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(StopsAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    public static class StopsViewHolder extends RecyclerView.ViewHolder{
        public TextView stopsName;

        public StopsViewHolder(@NonNull View itemView, final StopsAdapter.OnItemClickListener listener) {
            super(itemView);
            stopsName = itemView.findViewById(R.id.stopName);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public StopsAdapter(ArrayList<StopsItem> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public StopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stops_item, parent, false);
        StopsAdapter.StopsViewHolder viewHolder = new StopsAdapter.StopsViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StopsViewHolder holder, int position) {
        StopsItem currentItem = itemList.get(position);
        holder.stopsName.setText(currentItem.getStopName());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
