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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivanbratoev.festpal.datamodel.db.external.ClientDoesNotHavePermissionException;
import com.ivanbratoev.festpal.datamodel.db.external.ExternalDatabaseHandler;
import com.ivanbratoev.festpal.datamodel.db.internal.InternalDatabaseHandler;

import java.util.Date;

/**
 * Singleton used to organise the model of the application, providing high-level interface
 */
public final class DataModel {

    private static DataModel instance;
    private InternalDatabaseHandler internalDatabaseHandler;
    private ExternalDatabaseHandler externalDatabaseHandler;
    private Context context;
    private String username;

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
        this.context = context;
    }

    public String getUsername() {
        return username;
    }

    /**
     * package-protected method used for testing
     */
    void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param representative whether the new user is an official representative of a festival's
     *                       organisers
     * @return 0 on success, 1 on missing non-optional fields, 2 on invalid non-optional fields,
     * 3 on invalid optional fields, 4 on network or other error
     * @throws ClientDoesNotHavePermissionException
     */
    public int register(@NonNull String username, @NonNull String email, @NonNull String password,
                        @Nullable String firstName, @Nullable String lastName,
                        @Nullable String country, @Nullable String city,
                        @Nullable Boolean representative) throws ClientDoesNotHavePermissionException {
        return externalDatabaseHandler.register(username, email, password, firstName, lastName,
                country, city, representative);
    }

    /**
     * @return 0 on success, 1 on invalid input, 2 on missing username, 3 on missing password,
     * 4 on disabled account, 5 on network or unknown error
     * @throws NullPointerException
     */
    public int logInExternalDatabase(@NonNull String username, @NonNull String password) {
        try {
            int result = externalDatabaseHandler.login(username, password);
            if (result == 0)
                this.username = username;
            return result;
        } catch (ClientDoesNotHavePermissionException ignore) {
            return 5;
        } catch (NullPointerException ignore) {
            throw new NullPointerException("init() method has not been called on this instance!");
        }
    }

    /**
     * @return true on success, false otherwise
     */
    public boolean logOutExternalDatabase() {
        try {
            boolean result = externalDatabaseHandler.logout();
            if (result)
                username = null;
            return result;
        } catch (ClientDoesNotHavePermissionException ignore) {
            return false;
        }
    }

    /**
     * return top festival results from the online database. If the number of found festival is
     * lesser than the requested number of festivals all found festivals are returned
     *
     * @param numberOfResults number of festivals to return
     * @return festivals from the online database
     * @throws ClientDoesNotHavePermissionException
     */
    public Festival[] getOnlineFestivals(int numberOfResults)
            throws ClientDoesNotHavePermissionException {
        return externalDatabaseHandler.readMultipleFestivals(numberOfResults);
    }

    /**
     * return top festival results matching the search criteria from the online database. If the
     * number of found festival is lesser than the requested number of festivals all found
     * festivals are returned
     *
     * @param numberOfResults number of festivals to return
     * @param official official to filter the results by, <code>null</code> to ignore
     * @param name name to filter the results by, <code>null</code> to ignore
     * @param country country to filter the results by, <code>null</code> to ignore
     * @param city city to filter the results by, <code>null</code> to ignore
     * @param genre genre to filter the results by, <code>null</code> to ignore
     * @param minPrice minimum price to filter the results by, <code>null</code> to ignore
     * @param maxPrice maximum price to filter the results by, <code>null</code> to ignore
     * @param artist artist performing in a concert hosted by the festival
     *               to filter the results by, <code>null</code> to ignore
     * @return resulting festivals
     * @throws ClientDoesNotHavePermissionException
     */
    public Festival[] getOnlineFestivals(int numberOfResults, Boolean official, String name,
                                         String country, String city, String genre,
                                         String minPrice, String maxPrice, String artist) throws ClientDoesNotHavePermissionException {
        return externalDatabaseHandler.readMultipleFestivals(numberOfResults, official,
                name, country, city, genre, minPrice, maxPrice, artist);
    }


    /**
     *
     * @return all festivals in the offline database
     */
    public Festival[] getOfflineFestivals() {
        return internalDatabaseHandler.getFestivals();
    }


    /**
     * read festival information
     * @param id internal identifier of the festival information entry
     * @param update update the information from the external database before
     *               returning data if set <code>true</code>
     * @return festival object containing the data, null if no data is found
     * @throws ClientDoesNotHavePermissionException
     */
    public Festival readFestivalInfo(long id, boolean update)
            throws ClientDoesNotHavePermissionException {
        Festival festival = internalDatabaseHandler.getFestival(id);

        try {
            if (update) {
                Festival externalFestival = externalDatabaseHandler.
                        readFestivalInfo(festival.getExternalId());
                updateInternalFestivalObjectFromExternal(festival, externalFestival);
            }
        } catch (NullPointerException ignore) {
            return null;
        }
        return festival;
    }

    private void updateInternalFestivalObjectFromExternal(@NonNull Festival festival,
                                                          @NonNull Festival newData) {
        if (festival.equals(newData))
            return;
        String name = null;
        if (!festival.getName().equals(newData.getName())) {
            name = newData.getName();
            festival.setName(name);
        }
        String description = null;
        if (!festival.getDescription().equals(newData.getDescription())) {
            description = newData.getDescription();
            festival.setDescription(description);
        }
        String country = null;
        if (!festival.getCountry().equals(newData.getCountry())) {
            country = newData.getCountry();
            festival.setCountry(country);
        }
        String city = null;
        if (!festival.getCity().equals(newData.getCity())) {
            city = newData.getCity();
            festival.setCity(city);
        }
        String address = null;
        if (!festival.getAddress().equals(newData.getAddress())) {
            address = newData.getAddress();
            festival.setAddress(address);
        }
        String genre = null;
        if (!festival.getGenre().equals(newData.getGenre())) {
            genre = newData.getGenre();
            festival.setGenre(genre);
        }
        String prices = null;
        if (!festival.getPrices().equals(newData.getPrices())) {
            prices = newData.getPrices();
            festival.setPrices(prices);
        }
        String owner = null;
        if (!festival.getOwner().equals(newData.getOwner())) {
            owner = newData.getOwner();
            festival.setOwner(owner);
        }
        Boolean official = null;
        if (festival.isOfficial() != newData.isOfficial()) {
            official = newData.isOfficial();
            festival.setOfficial(official);
        }
        Integer votes = null;
        if (festival.getVotes() != newData.getVotes()) {
            votes = newData.getVotes();
            festival.setVotes(votes);
        }
        internalDatabaseHandler.editFestival(festival.getId(), name, description, country, city,
                address, genre, prices, owner, official, votes);
    }

    /**
     *  write festival information to the internal database
     * @param festival the festival information object to write
     * @param online also write to external database if set <code>true</code> and festival owner
     *               is currently logged user
     * @return internal id of the written item. If online is chosen and online fails or the
     * currently logged user is not the owner of the festival -1 will be
     * returned
     */
    public long writeFestivalInfo(Festival festival, boolean online)
            throws ClientDoesNotHavePermissionException {
        if (!festival.getOwner().equals(username))
            return -1;
        if (online) {
            boolean result;
            if (externalDatabaseHandler.readFestivalInfo(festival.getExternalId()) == null) {
                result = externalDatabaseHandler.writeFestivalInfo(
                        festival.getName(), festival.getDescription(), festival.getCountry(),
                        festival.getCity(), festival.getAddress(), festival.getGenre(),
                        festival.getPrices(), festival.isOfficial()
                );
            } else {
                result = externalDatabaseHandler.updateFestivalInfo(
                        festival.getExternalId(), festival.getName(), festival.getDescription(),
                        festival.getCountry(), festival.getCity(), festival.getAddress(),
                        festival.getGenre(), festival.getPrices(), festival.isOfficial()
                );
            }
            if (!result)
                return -1;
        }
        if (festival.getId() != null) {
            if (internalDatabaseHandler.getFestival(festival.getId()) != null) {
                internalDatabaseHandler.editFestival(festival.getId(), festival.getName(),
                        festival.getDescription(), festival.getCountry(), festival.getCity(),
                        festival.getAddress(), festival.getGenre(), festival.getPrices(),
                        festival.getOwner(), festival.isOfficial(), festival.getVotes());
                return festival.getId();
            }
        }
        return internalDatabaseHandler.addFestival(festival);
    }

    /**
     * read concert information
     *
     * @param festivalId id of the festival the concert is part of
     * @param concertId     id of the concert
     * @param update     update the information from the external database before
     *                   returning data if set <code>true</code>
     * @return concert object containing the data or null on fail
     */
    public Concert readConcertInfo(long festivalId, long concertId, boolean update)
            throws ClientDoesNotHavePermissionException {
        try {
            Festival festival = internalDatabaseHandler.getFestival(festivalId);
            Concert concert = internalDatabaseHandler.getConcert(festival, concertId);
            if (update) {
                Concert external = externalDatabaseHandler.readConcertInfo(festival, concertId);
                if (!concert.getFestival().equals(external.getFestival()))
                    concert.setFestival(external.getFestival());
                if (!concert.getArtist().equals(external.getArtist()))
                    concert.setArtist(external.getArtist());
                if (concert.getStage() != external.getStage())
                    concert.setStage(external.getStage());
                if (concert.getDay() != external.getDay())
                    concert.setDay(external.getDay());
                if (!concert.getStart().equals(external.getStart()))
                    concert.setStart(external.getStart());
                if (!concert.getEnd().equals(external.getEnd()))
                    concert.setEnd(external.getEnd());
            }
            return concert;
        } catch (NullPointerException ignore) {
            return null;
        }
    }

    /**
     * write concert information to the internal database
     * @param concert the concert information object to write
     * @param online also write to external database if set <code>true</code>
     * @return true on success false otherwise
     */
    public boolean writeConcertInfo(@NonNull Concert concert, boolean online) throws ClientDoesNotHavePermissionException {
        Concert existentConcert = internalDatabaseHandler.getConcert
                (concert.getFestival(), concert.getId());
        if (existentConcert == null) {
            return externalDatabaseHandler.writeConcertInfo(concert.getExternalId(),
                    concert.getArtist(), concert.getStage(), concert.getDay(),
                    concert.getStart(), concert.getEnd()) &&
                    (internalDatabaseHandler.addConcert(concert) != -1);
        }

        Long festival = (concert.getFestival().equals(existentConcert.getFestival())) ?
                null : concert.getFestival().getId();
        String artist = (concert.getArtist().equals(existentConcert.getArtist())) ?
                null : concert.getArtist();
        Integer stage = (concert.getStage() == existentConcert.getStage()) ?
                null : concert.getStage();
        Integer day = (concert.getDay() == existentConcert.getDay()) ?
                null : concert.getDay();
        Date start = (concert.getStart().equals(existentConcert.getStart())) ?
                null : concert.getStart();
        Date end = (concert.getEnd().equals(existentConcert.getEnd())) ?
                null : concert.getEnd();
        Boolean notify = (concert.isToNotify() == existentConcert.isToNotify()) ?
                null : concert.isToNotify();
        internalDatabaseHandler.editConcert(concert.getId(), concert.getExternalId(),
                festival, artist, stage, day, start, end, notify);
        if (online) {
            if (externalDatabaseHandler.readConcertInfo(
                    concert.getFestival(), concert.getExternalId()) == null)
                return externalDatabaseHandler.writeConcertInfo(
                        concert.getFestival().getExternalId(),
                        concert.getArtist(), concert.getStage(),
                        concert.getDay(), concert.getStart(), concert.getEnd());
            else
                return externalDatabaseHandler.updateConcertInfo(
                        concert.getExternalId(), artist, stage, day, start, end);
        } else {
            return true;
        }
    }

    /**
     * Update the internal database with the data from the external
     *
     * @param writeToOnline also update the external database with the internal if <code>true</code>
     * @return <code>true</code> on success, <code>false</code> otherwise
     */
    public boolean synchronise(boolean writeToOnline) throws ClientDoesNotHavePermissionException {
        Festival[] festivals = internalDatabaseHandler.getFestivals();
        for (Festival festival : festivals) {
            if (!synchroniseFestival(festival, writeToOnline))
                return false;
            if (!synchroniseFestivalConcerts(festival, writeToOnline))
                return false;
        }
        return true;
    }

    private boolean synchroniseFestival(Festival festival, boolean writeToOnline) {
        try {
            Festival externalFestival = externalDatabaseHandler.readFestivalInfo(festival.getExternalId());
            if (externalFestival == null) {
                if (!(writeToOnline && festival.getOwner().equals(getUsername())))
                    return false;
                return externalDatabaseHandler.writeFestivalInfo(
                        festival.getName(),
                        festival.getDescription(),
                        festival.getCountry(),
                        festival.getCity(),
                        festival.getAddress(),
                        festival.getGenre(),
                        festival.getPrices(),
                        festival.isOfficial()
                );
            }
            if (writeToOnline && festival.getOwner().equals(getUsername())) {
                return writeFestInfoToExternalFromInternal(externalFestival, festival);
            } else {
                writeFestInfoToInternalFromExternal(festival, externalFestival);
                return true;
            }
        } catch (ClientDoesNotHavePermissionException ignore) {
            return false;
        }
    }

    private boolean writeFestInfoToExternalFromInternal(Festival external, Festival internal) throws ClientDoesNotHavePermissionException {
        if (internal.equals(external))
            return true;
        return externalDatabaseHandler.updateFestivalInfo(
                external.getExternalId(),
                (internal.getName().equals(external.getName())) ?
                        null : internal.getName(),
                (internal.getDescription().equals(external.getDescription())) ?
                        null : internal.getDescription(),
                (internal.getCountry().equals(external.getCountry())) ?
                        null : internal.getCountry(),
                (internal.getCity().equals(external.getCity())) ?
                        null : internal.getCity(),
                (internal.getAddress().equals(external.getAddress())) ?
                        null : internal.getAddress(),
                (internal.getGenre().equals(external.getGenre())) ?
                        null : internal.getGenre(),
                (internal.getPrices().equals(external.getPrices())) ?
                        null : internal.getPrices(),
                (internal.isOfficial() == external.isOfficial()) ?
                        null : internal.isOfficial()
        );
    }

    private void writeFestInfoToInternalFromExternal(Festival internal, Festival external) {
        internalDatabaseHandler.editFestival(
                internal.getId(),
                (internal.getName().equals(external.getName())) ?
                        null : external.getName(),
                (internal.getDescription().equals(external.getDescription())) ?
                        null : external.getDescription(),
                (internal.getCountry().equals(external.getCountry())) ?
                        null : external.getCountry(),
                (internal.getCity().equals(external.getCity())) ?
                        null : external.getCity(),
                (internal.getAddress().equals(external.getAddress())) ?
                        null : external.getAddress(),
                (internal.getGenre().equals(external.getGenre())) ?
                        null : external.getGenre(),
                (internal.getPrices().equals(external.getPrices())) ?
                        null : external.getPrices(),
                (internal.getOwner().equals(external.getOwner())) ?
                        null : external.getOwner(),
                (internal.isOfficial() == external.isOfficial()) ?
                        null : external.isOfficial(),
                (internal.getVotes() == external.getVotes()) ?
                        null : external.getVotes()
        );
    }

    private boolean synchroniseFestivalConcerts(Festival festival, boolean writeToOnline) {
        try {
            for (Concert concert : externalDatabaseHandler.readFestivalConcerts(festival)) {
                Concert internalConcert = internalDatabaseHandler.getConcert(festival, concert.getId());
                if (internalConcert == null) {
                    internalDatabaseHandler.addConcert(concert);
                } else {
                    if (!synchroniseConcert(internalConcert, concert, writeToOnline))
                        return false;
                }
            }
        } catch (ClientDoesNotHavePermissionException e) {
            return false;
        }
        return true;
    }

    private boolean synchroniseConcert(Concert internal, Concert external, boolean writeToOnline) throws ClientDoesNotHavePermissionException {
        if (internal.equals(external))
            return true;
        if (writeToOnline && external.getFestival().getOwner().equals(getUsername())) {
            return writeConcertFromInternalToExternal(external, internal);
        } else {
            writeConcertInfoFromExternalToInternal(internal, external);
            return true;
        }
    }

    private void writeConcertInfoFromExternalToInternal(Concert internal, Concert external) {
        internalDatabaseHandler.editConcert(
                internal.getId(),
                (internal.getExternalId().equals(external.getExternalId())) ?
                        null : external.getExternalId(),
                (internal.getFestival() == external.getFestival()) ?
                        null : external.getFestival().getId(),
                (internal.getArtist().equals(external.getArtist())) ?
                        null : external.getArtist(),
                (internal.getStage() == external.getStage()) ?
                        null : external.getStage(),
                (internal.getDay() == external.getDay()) ?
                        null : external.getDay(),
                (internal.getStart().equals(external.getStart())) ?
                        null : external.getStart(),
                (internal.getEnd().equals(external.getEnd())) ?
                        null : external.getEnd(),
                null
        );
    }

    private boolean writeConcertFromInternalToExternal(Concert external, Concert internal) throws ClientDoesNotHavePermissionException {
        return externalDatabaseHandler.updateConcertInfo(
                external.getExternalId(),
                (internal.getArtist().equals(external.getArtist())) ?
                        null : internal.getArtist(),
                (internal.getStage() == external.getStage()) ?
                        null : internal.getStage(),
                (internal.getDay() == external.getDay()) ?
                        null : internal.getDay(),
                (internal.getStart().equals(external.getStart())) ?
                        null : internal.getStart(),
                (internal.getEnd().equals(external.getEnd())) ?
                        null : internal.getEnd()
        );
    }

    /**
     * @return 0 if the external database is accessible, 1 if there is Internet connection, but
     * there is problem connecting to the external database, 2 if there is no Internet Connection
     */
    public int externalDatabaseConnectivityStatus() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null)
            return 2;
        return (externalDatabaseHandler.canConnectToDB()) ? 0 : 1;
    }

    /**
     * @return true if festivals are recorded in the internal database, false otherwise
     */
    public boolean internalDatabaseHasFestivals() {
        return internalDatabaseHandler.hasFestivals();
    }

    /**
     *
     * @param festival festival to check for
     * @return true if there are concerts recorded for the input festival in the internal database,
     * false otherwise
     */
    public boolean internalDatabaseFestivalHasConcerts(@NonNull Festival festival) {
        return internalDatabaseHandler.festivalHasConcerts(festival);
    }
}
