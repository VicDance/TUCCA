package com.proyecto.transportesbahiacadiz.interfaces;

import com.proyecto.transportesbahiacadiz.model.FareList;
import com.proyecto.transportesbahiacadiz.model.GapList;
import com.proyecto.transportesbahiacadiz.model.HorarioList;
import com.proyecto.transportesbahiacadiz.model.NewsList;
import com.proyecto.transportesbahiacadiz.model.SegmentList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FareSystemAPI {
    /*@GET("zonas")
    Call<FareSystemList> getFareSystemList();

    @GET("municipios")
    Call<CityList> getCityList();

    @GET("municipios/{id}/nucleos")
    Call<CityList> getCentreList(@Path("id") long idNucleo);

    @GET("nucleos/{id}/lineas")
    Call<LineList> getLineList(@Path("id") long idLinea);

    /*@GET("horarios_origen_destino?destino={idDestino}&lang=ES&origen={idOrigen}")
    Call<Horario> getHorarios(@Query("destino") int destino, @Query("origen") int origen);*/
    @GET("horarios_origen_destino")
    Call<SegmentList> getBloques(@Query("destino") int destino, @Query("origen") int origen);

    @GET("horarios_origen_destino")
    Call<HorarioList> getHorarios(@Query("destino") int destino, @Query("origen") int origen);

    @GET("tarifas_interurbanas")
    Call<FareList> getFareList();

    @GET("saltos")
    Call<GapList> getGapList();

    @GET("noticias?lang=ES")
    Call<NewsList> getNewsList();
}
