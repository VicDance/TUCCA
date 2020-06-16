package com.proyecto.transportesbahiacadiz.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyecto.transportesbahiacadiz.interfaces.FareSystemAPI;
import com.proyecto.transportesbahiacadiz.R;
import com.proyecto.transportesbahiacadiz.adapters.NewsAdapter;
import com.proyecto.transportesbahiacadiz.model.News;
import com.proyecto.transportesbahiacadiz.model.NewsList;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFragment extends Fragment {
    private View view;
    private News[] news;
    private RecyclerView recyclerView;
    private List<News> newsArrayList;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_news);
        newsArrayList = new ArrayList<News>();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.ctan.es/v1/Consorcios/2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        FareSystemAPI fareSystemAPI = retrofit.create(FareSystemAPI.class);
        Call<NewsList> newsListCall = fareSystemAPI.getNewsList();
        newsListCall.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {
                NewsList newsList = response.body();
                //news = new News[newsList.getNewsList().size()];
                for(int i = 0; i < newsList.getNewsList().size(); i++){
                    if(newsList.getNewsList().get(i).getCategory().toLowerCase().contains("interurbanos") ||
                            newsList.getNewsList().get(i).getCategory().toLowerCase().contains("general")){
                        //news[i] = newsList.getNewsList().get(i);
                        newsArrayList.add(newsList.getNewsList().get(i));
                    }
                }
                buildRecycler();
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {

            }
        });
        return view;
    }

    private void buildRecycler(){
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter((ArrayList<News>) newsArrayList);
        recyclerView.setAdapter(adapter);
    }
}
