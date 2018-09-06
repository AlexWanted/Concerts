package ru.rewindforce.concerts.Homepage;

public class Concert {
    private int concert_id, crowd;

    private String band, club, description;
    private long datetime;

    public int getId() {
        return concert_id;
    }

    public String getBand() {
        return band;
    }

    public String getClub() {
        return club;
    }

    public int getCrowd() {
        return crowd;
    }

    public long getDatetime() {
        return datetime;
    }

    public String getDescription() {
        return description;
    }
}