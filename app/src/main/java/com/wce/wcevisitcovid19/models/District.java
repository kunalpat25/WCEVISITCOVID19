package com.wce.wcevisitcovid19.models;

public class District {

    private int Count_of_faculty;

    private int Count_of_student;

    public District(int count_of_faculty, int count_of_student) {
        Count_of_faculty = count_of_faculty;
        Count_of_student = count_of_student;
    }

    public District(){}

    public int getCount_of_faculty() {
        return Count_of_faculty;
    }

    public void setCount_of_faculty(int count_of_faculty) {
        Count_of_faculty = count_of_faculty;
    }

    public int getCount_of_student() {
        return Count_of_student;
    }

    public void setCount_of_student(int count_of_student) {
        Count_of_student = count_of_student;
    }
}
