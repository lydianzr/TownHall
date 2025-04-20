package com.example.modulehajar;

public class PeopleForLocation {
    private String name;
    private int imageResId;

    public PeopleForLocation(String name, int imageResId){
        this.name = name;
        this.imageResId = imageResId;
    }


    public int getImageResId() {
        return imageResId;
    }

    public String getName() {
        return name;
    }
}
