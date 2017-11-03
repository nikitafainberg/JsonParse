package com.fineapp.jsonparse;

public class Form {

    public String title;
    public byte[] imageKey;
    public String rating;
    public int year;
    public String genre;

    public Form(String title, byte[] imageKey, String rating, int year, String genre) {
        this.title = title;
        this.imageKey = imageKey;
        this.rating = rating;
        this.year = year;
        this.genre = genre;
    }
}
