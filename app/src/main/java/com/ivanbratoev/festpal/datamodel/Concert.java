package com.ivanbratoev.festpal.datamodel;


import java.util.Date;

public class Concert {
    private Festival festival;
    private String artist;
    private int scene;
    private int day;
    private Date start;
    private Date end;
    private boolean notify;

    public Concert(Festival festival, String artist, int scene, int day, Date start, Date end, boolean notify) {
        this.festival = festival;
        this.artist = artist;
        this.scene = scene;
        this.day = day;
        this.start = start;
        this.end = end;
        this.notify = notify;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getScene() {
        return scene;
    }

    public void setScene(int scene) {
        this.scene = scene;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
