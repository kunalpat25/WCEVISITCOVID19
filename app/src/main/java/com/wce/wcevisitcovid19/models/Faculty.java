package com.wce.wcevisitcovid19.models;

public class Faculty {

    private String Address;
    private long Contact;
    private String Department;
    private String Email;

    public Faculty(String address, long contact, String department, String email) {
        Address = address;
        Contact = contact;
        Department = department;
        Email = email;
    }

    public Faculty() {

    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public long getContact() {
        return Contact;
    }

    public void setContact(long contact) {
        Contact = contact;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
