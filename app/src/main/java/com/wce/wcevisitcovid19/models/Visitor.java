package com.wce.wcevisitcovid19.models;

public class Visitor
{
    private String id;
    private String name;
    private String locationOfVisit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Visitor(String visitorId,String name, String locationOfVisit)
    {
        this.id = visitorId;
        this.name = name;
        this.locationOfVisit = locationOfVisit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationOfVisit() {
        return locationOfVisit;
    }

    public void setLocationOfVisit(String locationOfVisit) {
        this.locationOfVisit = locationOfVisit;
    }
}
