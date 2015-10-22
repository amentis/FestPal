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

/**
 * Holder class for festival info
 */
public class Festival {
    private Long id;
    private long externalId;
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
    public Festival(@Nullable Long id, long externalId, @NonNull String name,
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isOfficial() {
        return official;
    }

    public void setOfficial(boolean official) {
        this.official = official;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Festival festival = (Festival) o;

        if (externalId != festival.externalId) return false;
        if (official != festival.official) return false;
        if (!name.equals(festival.name)) return false;
        if (!description.equals(festival.description)) return false;
        if (!country.equals(festival.country)) return false;
        if (!city.equals(festival.city)) return false;
        if (!address.equals(festival.address)) return false;
        if (!genre.equals(festival.genre)) return false;
        if (!prices.equals(festival.prices)) return false;
        return owner.equals(festival.owner);

    }

    @Override
    public int hashCode() {
        int result = (int) externalId;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + country.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + genre.hashCode();
        result = 31 * result + prices.hashCode();
        result = 31 * result + owner.hashCode();
        result = 31 * result + (official ? 1 : 0);
        return result;
    }
}
