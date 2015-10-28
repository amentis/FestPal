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

package com.ivanbratoev.festpal.datamodel.db.internal;


import android.provider.BaseColumns;

/**
 * Class used as a contract for the internal DB
 */
public class InternalDBContract {
    public static final String CREATE_TABLE_CONCERT_QUERY =
            "CREATE TABLE " + ConcertEntry.TABLE_NAME + " ("
            + ConcertEntry._ID + " INTEGER PRIMARY KEY, "
                    + ConcertEntry.COLUMN_NAME_EXTERNAL_ID + " INTEGER, "
            + ConcertEntry.COLUMN_NAME_FESTIVAL + " INTEGER, "
            + ConcertEntry.COLUMN_NAME_ARTIST + " TEXT, "
                    + ConcertEntry.COLUMN_NAME_STAGE + " INTEGER, "
            + ConcertEntry.COLUMN_NAME_DAY + " INTEGER, "
            + ConcertEntry.COLUMN_NAME_START + " INTEGER, "
            + ConcertEntry.COLUMN_NAME_END + " INTEGER, "
            + ConcertEntry.COLUMN_NAME_NOTIFY + " INTEGER, "
            + "FOREIGN KEY(" + ConcertEntry.COLUMN_NAME_FESTIVAL + ") REFERENCES " + FestivalEntry.TABLE_NAME + "(" + FestivalEntry._ID + ") "
            + ")";
    public static final String CREATE_TABLE_FESTIVAL_QUERY =
            "CREATE TABLE " + FestivalEntry.TABLE_NAME + " ("
            + FestivalEntry._ID + " INTEGER PRIMARY KEY, "
                    + FestivalEntry.COLUMN_NAME_EXTERNAL_ID + " INTEGER, "
            + FestivalEntry.COLUMN_NAME_NAME + " TEXT, "
            + FestivalEntry.COLUMN_NAME_DESCRIPTION + " TEXT, "
            + FestivalEntry.COLUMN_NAME_COUNTRY + " TEXT, "
            + FestivalEntry.COLUMN_NAME_CITY + " TEXT, "
            + FestivalEntry.COLUMN_NAME_ADDRESS + " TEXT, "
            + FestivalEntry.COLUMN_NAME_GENRE + " TEXT, "
            + FestivalEntry.COLUMN_NAME_PRICES + " TEXT, "
                    + FestivalEntry.COLUMN_NAME_OWNER + " TEXT, "
            + FestivalEntry.COLUMN_NAME_OFFICIAL + " INTEGER, "
                    + FestivalEntry.COLUMN_NAME_VOTES + " VOTES "
            + ")";

    public InternalDBContract() {
    }

    /**
     * Concert table definition
     */
    public static abstract class ConcertEntry implements BaseColumns {
        public static final String TABLE_NAME = "concert";
        public static final String COLUMN_NAME_EXTERNAL_ID = "externalId";
        public static final String COLUMN_NAME_FESTIVAL = "festival";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_STAGE = "stage";
        public static final String COLUMN_NAME_DAY = "day";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_END = "end";
        public static final String COLUMN_NAME_NOTIFY = "notify";
    }

    /**
     * Festival table definition
     */
    public static abstract class FestivalEntry implements BaseColumns {
        public static final String TABLE_NAME = "festival";
        public static final String COLUMN_NAME_EXTERNAL_ID = "externalId";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COUNTRY = "country";
        public static final String COLUMN_NAME_CITY = "city";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_GENRE = "genre";
        public static final String COLUMN_NAME_PRICES = "prices";
        public static final String COLUMN_NAME_OWNER = "owner";
        public static final String COLUMN_NAME_OFFICIAL = "official";
        public static final String COLUMN_NAME_VOTES = "votes";
    }
}
