package ru.rewindforce.concerts.HomeScreen;

import java.io.Serializable;

public class Concert implements Serializable {

    private int concert_id, crowd;
    private String title, club, description;
    private long datetime;

    public int getId() { return concert_id; }
    public String getBand() { return title; }
    public String getClub() { return club; }
    public int getCrowd() { return crowd; }
    public long getDatetime() { return datetime; }
    public String getDescription() { return description; }
}