package com.proyecto.tucca.model;

public class CardItem {
    private String textNumber;
    private String tipe;

    public CardItem(String textNumber, String tipe){
        this.textNumber = textNumber;
        this.tipe = tipe;
    }

    public String getTextNumber() {
        return textNumber;
    }

    public String getTipe() {
        return tipe;
    }
}
