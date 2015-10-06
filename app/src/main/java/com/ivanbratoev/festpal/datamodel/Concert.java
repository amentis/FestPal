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

import java.util.Date;

/**
 * Concert information holder class
 */
public class Concert {
    private Festival festival;
    private String artist;
    private int stage;
    private int day;
    private Date start;
    private Date end;
    private boolean notify;
    private Date lastModified;
    private Date lastSynchronised;

    /**
     * @param festival festival hosting the concert
     * @param artist   concert's performing artist name
     * @param stage    stage number, relevant to the festival, where the concert is being held
     * @param day      day number, relevant to the festival, when the concert is being held
     * @param start    time the fest starts
     * @param end      time the fest ends
     * @param notify   whether the user is to be notified that this concert is about to start
     */
    public Concert(@NonNull Festival festival, @NonNull String artist, int stage, int day,
                   @NonNull Date start, @NonNull Date end, boolean notify) {
        this.festival = festival;
        this.artist = artist;
        this.stage = stage;
        this.day = day;
        this.start = start;
        this.end = end;
        this.notify = notify;
        this.lastModified = new Date();
        this.lastSynchronised = new Date();
    }

    /**
     * @param festival         festival hosting the concert
     * @param artist           concert's performing artist name
     * @param stage            stage number, relevant to the festival, where the concert is being held
     * @param day              day number, relevant to the festival, when the concert is being held
     * @param start            time the fest starts
     * @param end              time the fest ends
     * @param notify           whether the user is to be notified that this concert is about to start
     * @param lastModified     date/time the object was last modified internally
     * @param lastSynchronised date/time the object was last synchronised with dbs
     */
    public Concert(@NonNull Festival festival, @NonNull String artist, int stage, int day,
                   @NonNull Date start, @NonNull Date end, boolean notify,
                   @NonNull Date lastModified, @NonNull Date lastSynchronised) {
        this.festival = festival;
        this.artist = artist;
        this.stage = stage;
        this.day = day;
        this.start = start;
        this.end = end;
        this.notify = notify;
        this.lastModified = lastModified;
        this.lastSynchronised = lastSynchronised;
    }

    public Festival getFestival() {
        return festival;
    }

    public void setFestival(Festival festival) {
        if (!festival.equals(this.festival)){
            this.festival = festival;
            setModified();
        }
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        if (!artist.equals(this.artist)) {
            this.artist = artist;
            setModified();
        }
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        if (stage != this.stage){
            this.stage = stage;
            setModified();
        }
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (day != this.day){
            this.day = day;
            setModified();
        }
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        if (!start.equals(this.start)){
            this.start = start;
            setModified();
        }
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        if (!end.equals(this.end)){
            this.end = end;
            setModified();
        }
    }

    public boolean willNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        if (notify != this.notify) {
            this.notify = notify;
            setModified();
        }
    }

    public Date getLastModified() {
        return lastModified;
    }

    public Date getLastSynchronised() {
        return lastSynchronised;
    }

    /**
     * Sets current time to date modified.
     */
    private void setModified() {
        lastModified = new Date();
    }

    /**
     * sets internal data to the supplied one and sets last synchronisation time accordingly
     *
     * @param concert the data to write
     */
    public void writeToSynchronise(Concert concert) {
        this.festival = concert.festival;
        this.artist = concert.artist;
        this.stage = concert.stage;
        this.day = concert.day;
        this.start = concert.start;
        this.end = concert.end;
        this.notify = concert.notify;
        lastSynchronised = new Date();
    }

    /**
     * returns unsaved concert data and sets last synchronisation time accordingly
     *
     * @return reference to the object if the festival hosting the concert is uploaded by the
     * logged user and there are modifications done more recently than the object has been last
     * synchronised, null otherwise
     */
    public Concert readToSynchronise(String username) {
        Date lastSynchronisedBefore = lastSynchronised;
        lastSynchronised = new Date();
        if (!this.festival.getOwner().equals(username))
            return null;
        if (lastModified.before(lastSynchronisedBefore)){
            return null;
        }
        return this;
    }
}
