package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HorarioList {
    @SerializedName("horario")
    private List<Horario> horarioList;

    public List<Horario> getHorarioList() {
        return horarioList;
    }
}
