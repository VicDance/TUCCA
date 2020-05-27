package com.proyecto.tucca.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.tucca.model.CardItem;
import com.proyecto.tucca.R;

import java.util.ArrayList;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardsViewHolder> {
    private ArrayList<CardItem> itemList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class CardsViewHolder extends RecyclerView.ViewHolder{
        public TextView textNumber;
        public TextView textType;

        public CardsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textNumber = itemView.findViewById(R.id.text_view_number_card);
            textType = itemView.findViewById(R.id.text_view_tipe_card);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        //System.out.println("Posicion " + position);
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CardsAdapter(ArrayList<CardItem> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public CardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        CardsViewHolder viewHolder = new CardsViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardsViewHolder holder, int position) {
        CardItem currentItem = itemList.get(position);
        holder.textNumber.setText(currentItem.getTextNumber());
        holder.textType.setText(currentItem.getTipe());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
