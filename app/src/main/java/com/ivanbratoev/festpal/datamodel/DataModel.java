package com.ivanbratoev.festpal.datamodel;


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

}
