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
 * Concert information holder class
 */
public class Concert {

    private Long id;
    private Long externalId;
    private Festival festival;
    private String artist;
    private int stage;
    private int day;
    private Date start;
    private Date end;
    private boolean notify;

    /**
     * @param festival festival hosting the concert
     * @param artist   concert's performing artist name
     * @param stage    stage number, relevant to the festival, where the concert is being held
     * @param day      day number, relevant to the festival, when the concert is being held
     * @param start    time the fest starts
     * @param end      time the fest ends
     * @param notify   whether the user is to be notified that this concert is about to start
     */
    public Concert(@Nullable Long id, long externalId, @NonNull Festival festival,
                   @NonNull String artist, int stage, int day,
                   @NonNull Date start, @NonNull Date end, boolean notify) {
        this.id = id;
        this.externalId = externalId;
        this.festival = festival;
        this.artist = artist;
        this.stage = stage;
        this.day = day;
        this.start = start;
        this.end = end;
        this.notify = notify;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExternalId() {
        return externalId;
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

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
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

    public boolean isToNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Concert concert = (Concert) o;

        if (stage != concert.stage) return false;
        if (day != concert.day) return false;
        if (!festival.equals(concert.festival)) return false;
        if (!artist.equals(concert.artist)) return false;
        if (!start.equals(concert.start)) return false;
        return end.equals(concert.end);

    }

    @Override
    public int hashCode() {
        int result = festival.hashCode();
        result = 31 * result + artist.hashCode();
        result = 31 * result + stage;
        result = 31 * result + day;
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }

}
