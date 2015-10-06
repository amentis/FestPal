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
 * Helper class building the URL necessary for the handler
 */
class ExternalDatabaseHelper {
    public static String getAddress() {
        return ExternalDatabaseDefinitions.PROTOCOL + "://" +
                ExternalDatabaseDefinitions.ADDRESS +
                ":" + ExternalDatabaseDefinitions.HOST + "/";
    }

    public static String getLogin() {
        return getAddress() + ExternalDatabaseDefinitions.LOGIN + "/";
    }

    public static String getLogout() {
        return getAddress() + ExternalDatabaseDefinitions.LOGOUT + "/";
    }

    public static String getReadMultipleFestivals() {
        return getAddress() + ExternalDatabaseDefinitions.MULTIPLE +
                "/" + ExternalDatabaseDefinitions.FESTIVAL + "/";
    }

    public static String getReadMultipleConcerts() {
        return getAddress() + ExternalDatabaseDefinitions.MULTIPLE +
                "/" + ExternalDatabaseDefinitions.CONCERT + "/";
    }

    public static String getReadFestivalInfo() {
        return getAddress() + ExternalDatabaseDefinitions.READ +
                "/" + ExternalDatabaseDefinitions.FESTIVAL + "/";
    }

    public static String getWriteFestivalInfo() {
        return getAddress() + ExternalDatabaseDefinitions.WRITE +
                "/" + ExternalDatabaseDefinitions.FESTIVAL + "/";
    }

    public static String getUpdateFestivalInfo() {
        return getAddress() + ExternalDatabaseDefinitions.UPDATE +
                "/" + ExternalDatabaseDefinitions.FESTIVAL + "/";
    }

    public static String getDeleteFestivalInfo() {
        return getAddress() + ExternalDatabaseDefinitions.DELETE +
                "/" + ExternalDatabaseDefinitions.FESTIVAL + "/";
    }

    public static String getReadConcertInfo() {
        return getAddress() + ExternalDatabaseDefinitions.READ +
                "/" + ExternalDatabaseDefinitions.CONCERT + "/";
    }

    public static String getWriteConcertInfo() {
        return getAddress() + ExternalDatabaseDefinitions.WRITE +
                "/" + ExternalDatabaseDefinitions.CONCERT + "/";
    }

    public static String getUpdateConcertInfo() {
        return getAddress() + ExternalDatabaseDefinitions.UPDATE +
                "/" + ExternalDatabaseDefinitions.CONCERT + "/";
    }

    public static String getDeleteConcertInfo() {
        return getAddress() + ExternalDatabaseDefinitions.DELETE +
                "/" + ExternalDatabaseDefinitions.CONCERT + "/";
    }

    public static String getVote() {
        return getAddress() + ExternalDatabaseDefinitions.VOTE + "/";
    }
}
