package com.wce.wcevisitcovid19.models;

public class Visitor
{
    private String id;
    private String name;
    private String extraInfo;
    private String type;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Visitor(String visitorId,String name,String userType, String extraInfo)
    {
        this.id = visitorId;
        this.name = name;
        this.type = userType;
        this.extraInfo = extraInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}
