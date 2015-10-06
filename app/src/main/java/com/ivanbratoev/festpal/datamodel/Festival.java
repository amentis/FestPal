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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Holder class for festival info
 */
public class Festival {
    private Integer id;
    private int externalId;
    private String name;
    private String description;
    private String country;
    private String city;
    private String address;
    private String genre;
    private String prices;
    private String owner;
    private boolean official;
    private int votes;
    private Date lastModified;
    private Date lastSynchronised;

    /**
     * @param id          id in internal DB. If object is not saved in the internal db set to null
     * @param externalId  id in external DB
     * @param name        festival name
     * @param description festival description
     * @param country     festival country
     * @param city        festival city
     * @param address     festival address
     * @param genre       genre or list of genres
     * @param prices      price or list of prices with supplied currency
     * @param owner       festival owner(uploader) name
     * @param official    true if the festival owner is official host of the festival
     * @param votes       number of people voted for festival
     */
    public Festival(@Nullable Integer id, int externalId, @NonNull String name,
                    @NonNull String description, @NonNull String country, @NonNull String city,
                    @NonNull String address, @NonNull String genre, @NonNull String prices,
                    @NonNull String owner, boolean official, int votes) {
        this.id = id;
        this.externalId = externalId;
        this.name = name;
        this.description = description;
        this.country = country;
        this.city = city;
        this.address = address;
        this.genre = genre;
        this.prices = prices;
        this.owner = owner;
        this.official = official;
        this.votes = votes;
        this.lastModified = new Date();
        this.lastSynchronised = new Date();
    }

    /**
     * @param id               id in internal DB. If object is not saved in the internal db set to null
     * @param externalId       id in external DB
     * @param name             festival name
     * @param description      festival description
     * @param country          festival country
     * @param city             festival city
     * @param address          festival address
     * @param genre            genre or list of genres
     * @param prices           price or list of prices with supplied currency
     * @param owner            festival owner(uploader) name
     * @param official         true if the festival owner is official host of the festival
     * @param votes            number of people voted for festival
     * @param lastModified     date/time the object was last modified internally
     * @param lastSynchronised date/time the object was last synchronised with dbs
     */
    public Festival(Integer id, int externalId, String name, String description,
                    String country, String city, String address,
                    String genre, String prices, String owner, boolean official, int votes,
                    Date lastModified, Date lastSynchronised) {
        this(id, externalId, name, description, country, city, address,
                genre, prices, owner, official, votes);
        this.lastModified = lastModified;
        this.lastSynchronised = lastSynchronised;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        if (id.equals(this.id)) {
            this.id = id;
            setModified();
        }
    }

    public int getExternalId() {
        return externalId;
    }

    public void setExternalId(int externalId){
        if (externalId != this.externalId){
            this.externalId = externalId;
            setModified();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (!name.equals(this.name)){
            this.name = name;
            setModified();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (!description.equals(this.description)){
            this.description = description;
            setModified();
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        if (!country.equals(this.country)){
            this.country = country;
            setModified();
        }
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        if (!city.equals(this.city)){
            this.city = city;
            setModified();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!address.equals(this.address)){
            this.address = address;
            setModified();
        }
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        if (!genre.equals(this.genre)){
            this.genre = genre;
            setModified();
        }
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        if (!prices.equals(this.prices)) {
            this.prices = prices;
            setModified();
        }
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        if (!owner.equals(this.owner)){
            this.owner = owner;
            setModified();
        }
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        if (official != this.official) {
            this.official = official;
            setModified();
        }
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        if (votes != this.votes) {
            this.votes = votes;
            setModified();
        }
    }

    /**
     * Sets current time to date modified.
     */
    private void setModified() {
        lastModified = new Date();
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getLastSynchronised() {
        return lastSynchronised;
    }

    /**
     * sets internal data to the supplied one and sets last synchronisation time accordingly
     *
     * @param festival the data to write
     */
    public void writeToSynchronise(Festival festival) {
        this.name = festival.name;
        this.description = festival.description;
        this.country = festival.country;
        this.city = festival.city;
        this.address = festival.address;
        this.genre = festival.genre;
        this.prices = festival.prices;
        this.owner = festival.owner;
        this.official = festival.official;
        this.votes = festival.votes;
        lastSynchronised = new Date();
    }

    /**
     * returns unsaved festival data and sets last synchronisation time accordingly
     *
     * @return reference to the object if the festival is uploaded by the logged user and there are
     * modifications done more recently than the object has been last synchronised, null otherwise
     */
    public Festival readToSynchronise(String username) {
        Date lastSynchronisedBefore = lastSynchronised;
        lastSynchronised = new Date();
        if (!this.owner.equals(username))
            return null;
        if (lastModified.before(lastSynchronisedBefore)){
            return null;
        }
        return this;
    }
}
