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

package com.ivanbratoev.festpal.datamodel.db.external;

/**
 * Holder class for strings used throughout the interface with the external db
 */
class ExternalDatabaseDefinitions {
    public static final String PROTOCOL = "http";
    public static final String ADDRESS = "127.0.0.1";
    public static final String HOST = "8000";

    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";

    public static final String MULTIPLE = "mult";

    public static final String READ = "r";
    public static final String WRITE = "w";
    public static final String UPDATE = "u";
    public static final String DELETE = "d";
    public static final String VOTE = "v";

    public static final String FESTIVAL = "fest";
    public static final String CONCERT = "conc";

    public static final String PARAMETER_CLIENT = "client";
    public static final String RESPONSE_NO_CLIENT_NAME = "Client name not provided\n";
    public static final String RESPONSE_CLIENT_NO_PERMISSION = "Permission not granted\n";
    public static final String LOG_OUT_RESPONSE_SUCCESS = "Logged out\n";
    public static final String PARAMETER_NUMBER = "num";
    public static final String PARAMETER_ID = "id";
    public static final String PARAMETER_FESTIVAL = "festival";
    public static final String RESPONSE_INVALID_FESTIVAL_ID = "Invalid Festival ID\n";
    public static final String RESPONSE_CONCERT_NOT_FOUND = "Concert Not Found\n";
    public static final String RESPONSE_INCORRECT_INPUT = "Incorrect input\n";
    public static final String RESPONSE_NAME_EXISTS = "Name exists\n";
    public static final String RESPONSE_ARTIST_EXISTS = "Artist exists\n";
    public static final String RESPONSE_PERMISSION_NOT_GRANTED = "Permission not granted\n";
    public static final String RESPONSE_OK = "OK\n";

    public class LogInContext {
        public static final String PARAMETER_USERNAME = "username";
        public static final String PARAMETER_PASSWORD = "password";
        public static final String RESPONSE_OK = "OK\n";
        public static final String RESPONSE_DISABLED = "Disabled account\n";
        public static final String RESPONSE_INVALID = "Invalid login\n";
        public static final String RESPONSE_NO_PASSWORD = "No password\n";
        public static final String RESPONSE_NO_USERNAME = "No username\n";
    }

    public class FestivalsContext {
        public static final String RESULT_PARAMETER_ID = "id";
        public static final String RESULT_PARAMETER_NAME = "name";
        public static final String RESULT_PARAMETER_DESCRIPTION = "description";
        public static final String RESULT_PARAMETER_COUNTRY = "country";
        public static final String RESULT_PARAMETER_CITY = "city";
        public static final String RESULT_PARAMETER_ADDRESS = "address";
        public static final String RESULT_PARAMETER_GENRE = "genre";
        public static final String RESULT_PARAMETER_PRICES = "prices";
        public static final String RESULT_PARAMETER_OWNER = "owner";
        public static final String RESULT_PARAMETER_OFFICIAL = "official";
        public static final String RESULT_PARAMETER_DOWNLOADS = "downloads";
        public static final String RESULT_PARAMETER_VOTES = "votes";
        public static final String RESULT_PARAMETER_FIRST_UPLOADED = "first_uploaded";
        public static final String RESULT_PARAMETER_LAST_MODIFIED = "last_modified";
    }

    public class ConcertContext {
        public static final String PARAMETER_FESTIVAL = "festival";
        public static final String PARAMETER_ARTIST = "artist";
        public static final String PARAMETER_OLD_ARTIST = "old_artist";
        public static final String PARAMETER_SCENE = "scene";
        public static final String PARAMETER_DAY = "day";
        public static final String PARAMETER_START = "start";
        public static final String PARAMETER_END = "end";
        public static final String RESULT_PARAMETER_FESTIVAL = "festival";
        public static final String RESULT_PARAMETER_ARTIST = "artist";
        public static final String RESULT_PARAMETER_SCENE = "scene";
        public static final String RESULT_PARAMETER_DAY = "day";
        public static final String RESULT_PARAMETER_START = "start";
        public static final String RESULT_PARAMETER_END = "end";
    }
}
