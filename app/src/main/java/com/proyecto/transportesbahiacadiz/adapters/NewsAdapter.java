package com.proyecto.transportesbahiacadiz.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.model.News;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{
    private ArrayList<News> newsList;

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        public TextView textTitle;
        public TextView textSubtitle;
        public TextView textOverview;
        public TextView textStart;
        public TextView textFinish;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_view_news_title);
            textSubtitle = itemView.findViewById(R.id.text_view_news_subtitle);
            textOverview = itemView.findViewById(R.id.text_view_news_overview);
            textStart = itemView.findViewById(R.id.text_view_news_start);
            textFinish = itemView.findViewById(R.id.text_view_news_finish);
        }
    }

    public NewsAdapter(ArrayList<News> itemList){
        this.newsList = itemList;
    }

    @NonNull
    @Override
    public NewsAdapter.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        NewsAdapter.NewsViewHolder viewHolder = new NewsAdapter.NewsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.NewsViewHolder holder, int position) {
        News currentItem = newsList.get(position);
        holder.textTitle.setText(currentItem.getTitle());
        holder.textSubtitle.setText(currentItem.getSubtitle());
        holder.textOverview.setText(currentItem.getOverview());
        holder.textStart.append(": " + currentItem.getStartDate());
        holder.textFinish.append(": " +currentItem.getFinishDate());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}
