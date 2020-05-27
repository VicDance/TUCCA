package com.proyecto.tucca.model;

public class CreditCard {
    private String cardUser;
    private String textNumber;
    private String cad;

    public CreditCard(String cardUser, String textNumber, String cad){
        this.cardUser = cardUser;
        this.textNumber = textNumber;
        this.cad = cad;
    }

    public String getCardUser() {
        return cardUser;
    }

    public void setCardUser(String cardUser) {
        this.cardUser = cardUser;
    }

    public String getTextNumber() {
        return textNumber;
    }

    public void setTextNumber(String textNumber) {
        this.textNumber = textNumber;
    }

    public String getCad() {
        return cad;
    }

    public void setCad(String cad) {
        this.cad = cad;
    }
}
