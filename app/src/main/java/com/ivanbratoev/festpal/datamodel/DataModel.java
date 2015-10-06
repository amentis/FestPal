/*
 * Copyright 2015 Ivan Bratoev
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ivanbratoev.festpal.datamodel;


import android.content.Context;
import android.support.annotation.NonNull;

import com.ivanbratoev.festpal.datamodel.db.external.ExternalDatabaseHandler;
import com.ivanbratoev.festpal.datamodel.db.internal.InternalDatabaseHandler;

/**
 * Singleton used to organise the model of the application, providing high-level interface
 */
public final class DataModel {

    private static DataModel instance;
    private InternalDatabaseHandler internalDatabaseHandler;
    private ExternalDatabaseHandler externalDatabaseHandler;

    /**
     * empty private constructor to forbid instantiation
     */
    private DataModel() {

    }

    /**
     * get reference to the DataModel Object. It is important, that init() is called after creation
     * of the singleton
     *
     * @return reference to the singleton
     */
    public static synchronized DataModel getInstance() {
        if (instance == null){
            instance = new DataModel();
        }
        return instance;
    }

    /**
     * @param context    application context
     * @param clientName client name used for authentication with external DB
     */
    public void init(@NonNull Context context, @NonNull String clientName) {
        internalDatabaseHandler = new InternalDatabaseHandler(context);
        externalDatabaseHandler = new ExternalDatabaseHandler(clientName);
    }

    /**
     * return top festival results from the online database
     *
     * @param numberOfResults number of festivals to return
     * @return festivals from the online database
     */
    public Festival[] getOnlineFestivals(int numberOfResults) {
        //TODO:implement
        return null;
    }

    /**
     * return top festival results matching the search criteria from the online database
     *
     * @param numberOfResults number of festivals to return
     * @param country         country to filter the results by, <code>null</code> to ignore
     * @param city            city to filter the results by, <code>null</code> to ignore
     * @param genre           genre to filter the results by, <code>null</code> to ignore
     * @param minPrice        minimum price to filter the results by, <code>null</code> to ignore
     * @param MaxPrice        maximum price to filter the results by, <code>null</code> to ignore
     * @param artist          artist to filter the results by, <code>null</code> to ignore
     * @param official        official to filter the results by, <code>null</code> to ignore
     * @param votes          number of votes to filter the results by, <code>null</code> to ignore
     * @return resulting festivals
     */
    public Festival[] getOnlineFestivals(int numberOfResults, String country, String city,
                                         String genre, Float minPrice, Float MaxPrice,
                                         String artist, Boolean official, Integer votes) {
        //TODO:implement
        return null;
    }


    /**
     *
     * @return all festivals in the offline database
     */
    public Festival[] getOfflineFestivals() {
        //TODO:implement
        return null;
    }


    /**
     * read festival information
     * @param id internal identifier of the festival information entry
     * @param update update the information from the external database before
     *               returning data if set <code>true</code>
     * @return festival object containing the data
     */
    public Festival readFestivalInfo(int id, boolean update) {
        //TODO:implement
        return null;
    }

    /**
     *  write festival information to the internal database
     * @param festival the festival information object to write
     * @param online also write to external database if set <code>true</code>
     * @return <code>true</code> if the written entry is new, <code>false</code> if an
     * old one has been edited instead
     */
    public boolean writeFestivalInfo(Festival festival, boolean online) {
        //TODO:implement
        return false;
    }

    /**
     * read concert information
     *
     * @param festivalId id of the festival the concert is part of
     * @param artist     artist name for the concert
     * @param update     update the information from the external database before
     *                   returning data if set <code>true</code>
     * @return concert object containing the data
     */
    public Concert readConcertInfo(int festivalId, @NonNull String artist, boolean update) {
        //TODO:implement
        return null;
    }

    /**
     * write concert information to the internal database
     * @param concert the concert information object to write
     * @param online also write to external database if set <code>true</code>
     * @return <code>true</code> if the written entry is new, <code>false</code> if an
     * old one has been edited instead
     */
    public boolean writeConcertInfo(@NonNull Concert concert, boolean online) {
        //TODO:implement
        return false;
    }

    /**
     * Update the internal database with the data from the external
     *
     * @param writeToOnline also update the external database with the internal if <code>true</code>
     * @return <code>true</code> on success, <code>false</code> otherwise
     */
    public boolean synchronise(boolean writeToOnline) {
        //TODO:implement
        return false;
    }

    /**
     * @return 0 if the external database is accessible, 1 if there is Internet connection, but
     * there is problem connecting to the external database, 2 if there is no Internet Connection
     */
    public int externalDatabaseConnectivityStatus() {
        //TODO:implement
        return 2;
    }

    /**
     * @return true if festivals are recorded in the internal database, false otherwise
     */
    public boolean internalDatabaseHasFestivals() {
        //TODO:implement
        return false;
    }

    /**
     *
     * @param festival festival to check for
     * @return true if there are concerts recorded for the input festival in the internal database,
     * false otherwise
     */
    public boolean internalDatabaseFestivalHasConcerts(@NonNull Festival festival) {
        //TODO:implement
        return false;
    }
}
