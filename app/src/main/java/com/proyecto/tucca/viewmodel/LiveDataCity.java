package com.proyecto.tucca.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.tucca.model.City;

import java.util.ArrayList;
import java.util.List;

public class LiveDataCity extends ViewModel {
    private MutableLiveData<List<City>> cityListLiveData;
    private List<City> cityList;

    public MutableLiveData<List<City>> getCityListLiveData() {
        return cityListLiveData;
    }

    public LiveData<List<City>> getCityList(){
       if(cityListLiveData == null){
           cityListLiveData = new MutableLiveData<List<City>>();
           cityList = new ArrayList<City>();
       }
       return cityListLiveData;
    }

    public void addCity(City city){
        cityList.add(city);
        cityListLiveData.setValue(cityList);
    }
}
