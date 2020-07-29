package com.wce.wcevisitcovid19.models;

public class Admin {
    private String Email;

    public Admin(){}

    public Admin(String email) {
        this.Email = email;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }
}
