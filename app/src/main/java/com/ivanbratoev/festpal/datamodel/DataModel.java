package com.ivanbratoev.festpal.datamodel;


import java.util.List;

public class DataModel {
    private static DataModel instance;

    protected DataModel() {

    }

    public static DataModel getInstance(){
        if (instance == null){
            instance = new DataModel();
        }
        return instance;
    }

    public List<Festival> getOnlineFestivals(int numberOfResults) {
        //TODO
        return null;
    }

    public List<Festival> getOnlineFestivals(int numberOfResults, String country, String city,
                                             String genre, float minPrice, float MaxPrice,
                                             String artist, boolean official, int rank) {
        //TODO
        return null;
    }

    public List<Festival> getOfflineFestivals() {
        //TODO
        return null;
    }

    public Festival readFestivalInfo(boolean update) {
        //TODO
        return null;
    }

    public boolean writeFestivalInfo(Festival festival, boolean online) {
        //TODO
        return false;
    }

    public Concert readConcertInfo(boolean update) {
        //TODO
        return null;
    }

    public boolean writeConcertInfo(Concert concert, boolean online) {
        //TODO
        return false;
    }

    boolean synchronise(boolean writeToOnline) {
        //TODO
        return false;
    }
}
