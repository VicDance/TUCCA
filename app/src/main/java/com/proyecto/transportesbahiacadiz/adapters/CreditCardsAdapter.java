package com.proyecto.transportesbahiacadiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.model.CreditCard;
import com.proyecto.transportesbahiacadiz.R;

import java.util.ArrayList;

import serializable.TarjetaCredito;

public class CreditCardsAdapter extends RecyclerView.Adapter<CreditCardsAdapter.CreditCardsViewHolder> {
    private static final String TAG = "CreditCardAdapter";
    private ArrayList<TarjetaCredito> itemList;
    private OnItemClickListener mListener;
    private OnLongItemCliclListener longListener;

    public CreditCardsAdapter(ArrayList<TarjetaCredito> itemList, OnItemClickListener onItemClickListener, OnLongItemCliclListener onLongItemCliclListener){
        this.itemList = itemList;
        mListener = onItemClickListener;
        longListener = onLongItemCliclListener;
    }

    @NonNull
    @Override
    public CreditCardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_card_item, parent, false);
        return new CreditCardsViewHolder(view, mListener, longListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCardsViewHolder holder, int position) {
        //CreditCard currentItem = itemList.get(position);
        holder.user.setText(itemList.get(position).getTitular());
        holder.textNumber.setText(itemList.get(position).getNumTarjeta());
        holder.cad.setText(itemList.get(position).getCaducidad());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CreditCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView user;
        public TextView textNumber;
        public TextView cad;
        OnItemClickListener mListener;
        OnLongItemCliclListener longItemCliclListener;

        public CreditCardsViewHolder(@NonNull View itemView, OnItemClickListener listener, OnLongItemCliclListener longListener) {
            super(itemView);
            user = itemView.findViewById(R.id.text_view_user_credit_card);
            textNumber = itemView.findViewById(R.id.text_view_number_credit_card);
            cad = itemView.findViewById(R.id.text_view_cad_credit_card);
            mListener = listener;
            longItemCliclListener = longListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            longItemCliclListener.onLongItemClick(getAdapterPosition());
            return true;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnLongItemCliclListener{
        void onLongItemClick(int position);
    }
}
