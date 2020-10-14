package com.wce.wcevisitcovid19.utils;

import java.util.Calendar;
import java.util.TimeZone;

public class DateUtils {
    String date;
    String month;
    String year;
    Calendar calendar;
    String completeDate;

    public DateUtils(String completeDate)
    {
        this.completeDate = completeDate;
    }

    public DateUtils()
    {

    }

    public String extractDate()
    {
        String date = completeDate.substring(0, 2);
        if (date.contains("/"))
            date = "0" + date.substring(0, 1);
        return date;
    }

    public String extractMonth()
    {
        String month = completeDate.substring(3,5);
        if (month.contains("/"))
            month = "0" + month;
        return month;
    }

    public String extractYear()
    {
        return completeDate.substring(6);
    }

    public String getDate() {
        calendar = Calendar.getInstance(TimeZone.getDefault());
        date = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        if(date.length() == 1)
            date ="0"+ date;
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMonth() {
        calendar = Calendar.getInstance(TimeZone.getDefault());
        month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        if(month.length() == 1)
            month = "0"+ month;
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        calendar = Calendar.getInstance(TimeZone.getDefault());
        year = String.valueOf(calendar.get(Calendar.YEAR));
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public String toString()
    {
        return getDate() +"/"+getMonth()+"/"+getYear();
    }
}
