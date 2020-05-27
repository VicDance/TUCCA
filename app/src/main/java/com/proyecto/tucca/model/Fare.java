package com.proyecto.tucca.model;

import com.google.gson.annotations.SerializedName;

public class Fare {
    @SerializedName("saltos")
    private int saltos;
    @SerializedName("bs")
    private double bs;

    public Fare(){

    }

    public Fare(int saltos) {
        this.saltos = saltos;
    }

    public Fare(int saltos, double bs){
        this.saltos = saltos;
        this.bs = bs;
    }

    public int getSaltos() {
        return saltos;
    }

    public void setSaltos(int saltos) {
        this.saltos = saltos;
    }

    public double getBs() {
        return bs;
    }

    public void setBs(double bs) {
        this.bs = bs;
    }

    @Override
    public String toString() {
        return "Fare{" +
                "saltos=" + saltos +
                ", bs=" + bs +
                '}';
    }
}
