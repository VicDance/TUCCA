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

public class CreditCardsAdapter extends RecyclerView.Adapter<CreditCardsAdapter.CreditCardsViewHolder> {
    private static final String TAG = "CreditCardAdapter";
    private ArrayList<CreditCard> itemList;
    private OnItemClickListener mListener;

    public CreditCardsAdapter(ArrayList<CreditCard> itemList, OnItemClickListener onItemClickListener){
        this.itemList = itemList;
        mListener = onItemClickListener;
    }

    /*public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }*/

    @NonNull
    @Override
    public CreditCardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_card_item, parent, false);
        //CreditCardsViewHolder viewHolder = new CreditCardsViewHolder(view, mListener);
        return new CreditCardsViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCardsViewHolder holder, int position) {
        //CreditCard currentItem = itemList.get(position);
        holder.user.setText(itemList.get(position).getCardUser());
        holder.textNumber.setText(itemList.get(position).getTextNumber());
        holder.cad.setText(itemList.get(position).getCad());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class CreditCardsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView user;
        public TextView textNumber;
        public TextView cad;
        OnItemClickListener mListener;

        public CreditCardsViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            user = itemView.findViewById(R.id.text_view_user_credit_card);
            textNumber = itemView.findViewById(R.id.text_view_number_credit_card);
            cad = itemView.findViewById(R.id.text_view_cad_credit_card);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Log.d(TAG, "onClick: " + getAdapterPosition());
            mListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
