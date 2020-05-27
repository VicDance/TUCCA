package com.proyecto.tucca.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.proyecto.tucca.model.Centre;

import java.util.ArrayList;
import java.util.List;

public class LiveDataCentre extends ViewModel {
    private MutableLiveData<List<Centre>> centreListLiveData;
    private List<Centre> centreList;

    public MutableLiveData<List<Centre>> getCentreListLiveData() {
        return centreListLiveData;
    }

    public LiveData<List<Centre>> getCentreList(){
        if(centreListLiveData == null){
            centreListLiveData = new MutableLiveData<List<Centre>>();
            centreList = new ArrayList<Centre>();
        }
        return centreListLiveData;
    }

    public void addCentre(Centre centre){
        centreList.add(centre);
        centreListLiveData.setValue(centreList);
    }
}
