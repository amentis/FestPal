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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ivanbratoev.festpal.datamodel.Concert;
import com.ivanbratoev.festpal.datamodel.Festival;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for accessing the external database
 */
public class ExternalDatabaseHandler {

    private final String client;

    /**
     * @param client client name used for authentication with external DB
     */
    public ExternalDatabaseHandler(@NonNull String client) {
        this.client = client;
    }

    /**
     * check whether connection to the database server can be established
     *
     * @return true on success false otherwise
     */
    public boolean canConnectToDB() {
        try {
            URL url = new URL(ExternalDatabaseHelper.getAddress());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15_000);
            connection.connect();
            connection.disconnect();
            return true;
        } catch (Exception ignore) {
        }
        return false;
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
        try {
            URL url = new URL(ExternalDatabaseDefinitions.REGISTER);
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_USERNAME,
                    username);
            parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_EMAIL,
                    email);
            parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_PASSWORD,
                    password);
            if (firstName != null) {
                parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_FIRST_NAME,
                        firstName);
            }
            if (lastName != null) {
                parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_LAST_NAME,
                        lastName);
            }
            if (country != null) {
                parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_COUNTRY,
                        country);
            }
            if (city != null) {
                parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_CITY,
                        city);
            }
            if (representative != null) {
                if (representative)
                    parameters.put(ExternalDatabaseDefinitions.RegisterContext.PARAMETER_REPRESENTATIVE,
                            "1");
            }
            String response = getRemoteData(url, parameters);
            if (response == null)
                return 4;
            switch (response) {
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_MISSING_NON_OPTIONAL_FIELDS:
                    return 1;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_USERNAME:
                    return 2;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_EMAIL:
                    return 2;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_PASSWORD:
                    return 2;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_FIRST_NAME:
                    return 3;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_LAST_NAME:
                    return 3;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_COUNTRY:
                    return 3;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_INVALID_CITY:
                    return 3;
                case ExternalDatabaseDefinitions.RegisterContext.RESULT_OK:
                    return 0;
                default:
                    return 4;
            }
        } catch (MalformedURLException ignore) {
            return 4;
        }
    }

    /**
     * @return 0 on success, 1 on invalid input, 2 on missing username, 3 on missing password,
     * 4 on disabled account, 5 on network or unknown error
     * @throws ClientDoesNotHavePermissionException
     */
    public int login(@NonNull String username, @NonNull String password)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getLogin());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.LogInContext.PARAMETER_USERNAME,
                    username);
            parameters.put(ExternalDatabaseDefinitions.LogInContext.PARAMETER_PASSWORD,
                    password);
            String response = getRemoteData(url, parameters);
            if (response == null) {
                return 5;
            }
            switch (response) {
                case ExternalDatabaseDefinitions.LogInContext.RESPONSE_OK:
                    return 0;
                case ExternalDatabaseDefinitions.LogInContext.RESPONSE_INVALID:
                    return 1;
                case ExternalDatabaseDefinitions.LogInContext.RESPONSE_NO_USERNAME:
                    return 2;
                case ExternalDatabaseDefinitions.LogInContext.RESPONSE_NO_PASSWORD:
                    return 3;
                case ExternalDatabaseDefinitions.LogInContext.RESPONSE_DISABLED:
                    return 4;
                default:
                    return 5;
            }
        } catch (IOException ignore) {
            return 5;
        }
    }

    /**
     * @return true on success, false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean logout() throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getLogout());
            Map<String, String> parameters = new HashMap<>();
            return (ExternalDatabaseDefinitions.LOG_OUT_RESPONSE_SUCCESS.equals(
                    getRemoteData(url, parameters)));
        } catch (IOException | NullPointerException ignore) {
            return false;
        }
    }

    /**
     * @param num number of festivals to return
     * @return num number festivals or less if not enough festivals are found. Null on incorrect
     * input or error
     * Override method
     * @throws ClientDoesNotHavePermissionException
     */
    public Festival[] readMultipleFestivals(int num) throws ClientDoesNotHavePermissionException {
        return readMultipleFestivals(num, null, null, null, null, null, null, null, null);
    }

    /**
     * return top festival results matching the search criteria. If the
     * number of found festival is lesser than the requested number of festivals all found
     * festivals are returned
     *
     * @param num      number of festivals to return
     * @param official official to filter the results by, <code>null</code> to ignore
     * @param name     name to filter the results by, <code>null</code> to ignore
     * @param country  country to filter the results by, <code>null</code> to ignore
     * @param city     city to filter the results by, <code>null</code> to ignore
     * @param genre    genre to filter the results by, <code>null</code> to ignore
     * @param minPrice minimum price to filter the results by, <code>null</code> to ignore
     * @param maxPrice maximum price to filter the results by, <code>null</code> to ignore
     * @param artist   artist performing in a concert hosted by the festival
     *                 to filter the results by, <code>null</code> to ignore
     * @return resulting festivals
     */
    public Festival[] readMultipleFestivals(int num, Boolean official, String name, String country,
                                            String city, String genre,
                                            String minPrice, String maxPrice, String artist)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getReadMultipleFestivals());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.PARAMETER_NUMBER,
                    String.valueOf(num));
            if (official != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_OFFICIAL,
                        String.valueOf(official));
            if (name != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_NAME,
                        name);
            if (country != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_COUNTRY,
                        country);
            if (city != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_CITY,
                        city);
            if (genre != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_GENRE,
                        genre);
            if (minPrice != null)
                parameters.put(ExternalDatabaseDefinitions.PARAMETER_MIN_PRICE,
                        minPrice);
            if (maxPrice != null)
                parameters.put(ExternalDatabaseDefinitions.PARAMETER_MAX_PRICE,
                        maxPrice);
            if (artist != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.RESULT_PARAMETER_ARTIST,
                        artist);
            String response = getRemoteData(url, parameters);
            JSONArray json = new JSONArray(response);
            Festival[] result = new Festival[json.length()];
            for (int i = 0; i < json.length(); i++) {
                JSONObject current = json.getJSONObject(i);
                result[i] = new Festival(
                        -1L,
                        current.getLong(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_ID),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_NAME),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_DESCRIPTION),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_COUNTRY),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_CITY),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_ADDRESS),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_GENRE),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_PRICES),
                        current.getString(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_OWNER),
                        current.getBoolean(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_OFFICIAL),
                        current.getInt(
                                ExternalDatabaseDefinitions.FestivalsContext.
                                        RESULT_PARAMETER_VOTES)
                );
            }
            return result;
        } catch (JSONException | MalformedURLException ignore) {
            return null;
        }
    }

    /**
     * @param festival festival object to return concerts for
     * @return an array of the concerts hosted by the festival or null on wrong input
     * @throws ClientDoesNotHavePermissionException
     */
    public Concert[] readFestivalConcerts(@NonNull Festival festival)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getReadMultipleConcerts());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.PARAMETER_ID,
                    String.valueOf(festival.getId()));
            String content = getRemoteData(url, parameters);
            if (content == null)
                return null;
            if (content.equals(ExternalDatabaseDefinitions.RESPONSE_INVALID_FESTIVAL_ID))
                return null;
            JSONArray json = new JSONArray(content);
            Concert[] result = new Concert[json.length()];
            for (int i = 0; i < json.length(); i++) {
                JSONObject current = json.getJSONObject(i);
                result[i] = new Concert(
                        null,
                        current.getLong(ExternalDatabaseDefinitions.ConcertContext.
                                RESULT_PARAMETER_EXTERNAL_ID),
                        festival,
                        current.getString(ExternalDatabaseDefinitions.ConcertContext.
                                RESULT_PARAMETER_ARTIST),
                        current.getInt(ExternalDatabaseDefinitions.ConcertContext.
                                RESULT_PARAMETER_SCENE),
                        current.getInt(ExternalDatabaseDefinitions.ConcertContext.
                                RESULT_PARAMETER_DAY),
                        new Date(
                                current.getInt(ExternalDatabaseDefinitions.ConcertContext.
                                        RESULT_PARAMETER_START)),
                        new Date(
                                current.getInt(ExternalDatabaseDefinitions.ConcertContext.
                                        RESULT_PARAMETER_END)),
                        false
                );
            }
            return result;
        } catch (MalformedURLException | JSONException ignore) {
            return null;
        }
    }

    /**
     * @param festivalID external id of the festival
     * @return festival object or null
     * @throws ClientDoesNotHavePermissionException
     */
    public Festival readFestivalInfo(long festivalID) throws ClientDoesNotHavePermissionException {
        if (festivalID < 0)
            return null;
        try {
            URL url = new URL(ExternalDatabaseHelper.getReadFestivalInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.PARAMETER_ID, String.valueOf(festivalID));
            String response = getRemoteData(url, parameters);
            if (response == null)
                return null;
            if (response.equals(ExternalDatabaseDefinitions.RESPONSE_INVALID_FESTIVAL_ID))
                return null;
            JSONObject json = new JSONObject(response);
            return new Festival(
                    -1L,
                    json.getLong(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_ID),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_NAME),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_DESCRIPTION),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_COUNTRY),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_CITY),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_ADDRESS),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_GENRE),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_PRICES),
                    json.getString(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_OWNER),
                    json.getBoolean(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_OFFICIAL),
                    json.getInt(ExternalDatabaseDefinitions.FestivalsContext.
                            RESULT_PARAMETER_VOTES)

            );
        } catch (MalformedURLException | JSONException ignore) {
            return null;
        }
    }

    /**
     * Writes festival data to external database.
     *
     * @param name        name for new festival object. Mandatory field
     * @param description description for new festival object or null
     * @param country     country  for new festival object or null
     * @param city        city for new festival object or null
     * @param address     address for new festival object or null
     * @param genre       genre for new festival object or null
     * @param prices      prices for new festival object or null
     * @param official    official for new festival object or null
     * @return True if festival has been written successfully, false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean writeFestivalInfo(@NonNull String name, String description, String country,
                                     String city, String address, String genre, String prices,
                                     Boolean official)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getWriteFestivalInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_NAME,
                    name);
            if (description != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_DESCRIPTION,
                        description);
            if (country != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_COUNTRY,
                        country);
            if (city != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_CITY,
                        city);
            if (address != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_ADDRESS,
                        address);
            if (genre != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_GENRE,
                        genre);
            if (prices != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_PRICES,
                        prices);
            if (official != null)
                parameters.put(
                        ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_OFFICIAL,
                        String.valueOf(official));
            return ExternalDatabaseDefinitions.RESPONSE_OK.equals(getRemoteData(url, parameters));
        } catch (MalformedURLException ignore) {
            return false;
        }

    }

    /**
     * @param externalID  external ID of the festival
     * @param name        new name to set or null to remain unchanged
     * @param description new description to set or null to remain unchanged
     * @param country     new country to set or null to remain unchanged
     * @param city        new city to set or null to remain unchanged
     * @param address     new address to set or null to remain unchanged
     * @param genre       new genre to set or null to remain unchanged
     * @param prices      new prices to set or null to remain unchanged
     * @param official    new official to set or null to remain unchanged
     * @return true on success false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean updateFestivalInfo(long externalID, String name, String description,
                                      String country, String city, String address,
                                      String genre, String prices, Boolean official)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getUpdateFestivalInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_ID,
                    String.valueOf(externalID));
            if (name != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.
                                RESULT_PARAMETER_NAME,
                        name);
            if (description != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.
                                RESULT_PARAMETER_DESCRIPTION,
                        description);
            if (country != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.
                                RESULT_PARAMETER_COUNTRY,
                        country);
            if (city != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_CITY,
                        city);
            if (address != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.
                                RESULT_PARAMETER_ADDRESS,
                        address);
            if (genre != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_GENRE,
                        genre);
            if (prices != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_PRICES,
                        prices);
            if (official != null)
                parameters.put(ExternalDatabaseDefinitions.FestivalsContext.
                                RESULT_PARAMETER_OFFICIAL,
                        String.valueOf(official));
            String response = getRemoteData(url, parameters);
            return !ExternalDatabaseDefinitions.RESPONSE_INVALID_FESTIVAL_ID.equals(response) &&
                    !(ExternalDatabaseDefinitions.RESPONSE_INCORRECT_INPUT.equals(response));
        } catch (MalformedURLException ignore) {
            return false;
        }
    }

    /**
     * @param festival festival hosting the concert
     * @param id   external database id of the concert item
     * @return concert object or null
     * @throws ClientDoesNotHavePermissionException
     */
    public Concert readConcertInfo(@NonNull Festival festival, long id)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getReadConcertInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_ID,
                    String.valueOf(id));
            String response = getRemoteData(url, parameters);
            if (response == null)
                return null;
            if (ExternalDatabaseDefinitions.RESPONSE_INVALID_FESTIVAL_ID.equals(response))
                return null;
            JSONObject json = new JSONObject(response);
            return new Concert(
                    null,
                    id,
                    festival,
                    json.getString(
                            ExternalDatabaseDefinitions.ConcertContext.RESULT_PARAMETER_ARTIST),
                    json.getInt(ExternalDatabaseDefinitions.ConcertContext.RESULT_PARAMETER_SCENE),
                    json.getInt(ExternalDatabaseDefinitions.ConcertContext.RESULT_PARAMETER_DAY),
                    new Date(
                            json.getInt(ExternalDatabaseDefinitions.ConcertContext.
                                    RESULT_PARAMETER_START)),
                    new Date(
                            json.getInt(ExternalDatabaseDefinitions.ConcertContext.
                                    RESULT_PARAMETER_END)),
                    false
            );
        } catch (MalformedURLException | JSONException ignore) {
            return null;
        }
    }

    /**
     * @param festivalExternalID external ID of the festival hosting the concert
     * @param artist             name of the concert's performing artist
     * @param stage              stage of the festival the concert takes place in
     * @param day                day of the festival the concert takes place in
     * @param start              time starting
     * @param end                time ending
     * @return true on success false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean writeConcertInfo(long festivalExternalID, @NonNull String artist, Integer stage,
                                    Integer day, @NonNull Date start, @NonNull Date end)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getWriteConcertInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_FESTIVAL,
                    String.valueOf(festivalExternalID));
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_ARTIST,
                    artist);
            if (stage != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_SCENE,
                        String.valueOf(stage));
            if (day != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_DAY,
                        String.valueOf(day));
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_START,
                    String.valueOf(start.getTime()));
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_END,
                    String.valueOf(end.getTime()));
            String response = getRemoteData(url, parameters);
            return response != null && ExternalDatabaseDefinitions.RESPONSE_OK.equals(response);
        } catch (MalformedURLException ignore) {
            return false;
        }
    }

    /**
     * @param id id of the concert item
     * @param artist             new artist name or null to remain unchanged
     * @param stage              new stage or null to remain unchanged
     * @param day                new day or null to remain unchanged
     * @param start              new start or null to remain unchanged
     * @param end                new end or null to remain unchanged
     * @return true on success false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean updateConcertInfo(long id,
                                     String artist, Integer stage, Integer day,
                                     Date start, Date end)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getUpdateConcertInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_ID,
                    String.valueOf(id));
            if (artist != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_ARTIST,
                        artist);
            if (stage != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_SCENE,
                        String.valueOf(stage));
            if (day != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_DAY,
                        String.valueOf(day));
            if (start != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_START,
                        String.valueOf(start.getTime()));
            if (end != null)
                parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_END,
                        String.valueOf(end.getTime()));
            String response = getRemoteData(url, parameters);
            if (response == null)
                return false;
            switch (response) {
                case ExternalDatabaseDefinitions.RESPONSE_CONCERT_NOT_FOUND:
                    return false;
                case ExternalDatabaseDefinitions.RESPONSE_PERMISSION_NOT_GRANTED:
                    return false;
                case ExternalDatabaseDefinitions.RESPONSE_INCORRECT_INPUT:
                    return false;
                default:
                    return true;
            }
        } catch (MalformedURLException ignore) {
            return false;
        }
    }

    /**
     * @param externalID external id of the festival
     * @return true on success false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean deleteFestival(int externalID) throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getDeleteFestivalInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_ID,
                    String.valueOf(externalID));
            String response = getRemoteData(url, parameters);
            return ExternalDatabaseDefinitions.RESPONSE_OK.equals(response);
        } catch (MalformedURLException ignore) {
            return false;
        }
    }

    /**
     * @param id id of the concert
     * @return true on success false otherwise
     * @throws ClientDoesNotHavePermissionException
     */
    public boolean deleteConcert(long id)
            throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getDeleteConcertInfo());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.ConcertContext.PARAMETER_ID,
                    String.valueOf(id));
            String response = getRemoteData(url, parameters);
            return ExternalDatabaseDefinitions.RESPONSE_OK.equals(response);
        } catch (MalformedURLException ignore) {
            return false;
        }
    }

    /**
     * @param festivalExternalID external id of the festival
     * @return number of voters after vote or -1 on fail
     * @throws ClientDoesNotHavePermissionException
     */
    public int vote(int festivalExternalID) throws ClientDoesNotHavePermissionException {
        try {
            URL url = new URL(ExternalDatabaseHelper.getVote());
            Map<String, String> parameters = new HashMap<>();
            parameters.put(ExternalDatabaseDefinitions.FestivalsContext.RESULT_PARAMETER_ID,
                    String.valueOf(festivalExternalID));
            String response = getRemoteData(url, parameters);

            if (response == null ||
                    ExternalDatabaseDefinitions.RESPONSE_INVALID_FESTIVAL_ID.equals(response))
                return -1;
            return Integer.getInteger(response, -1);
        } catch (MalformedURLException ignore) {
            return -1;
        }
    }

    private String getRemoteData(URL url, Map<String, String> parameters)
            throws ClientDoesNotHavePermissionException {
        try {
            parameters.put(ExternalDatabaseDefinitions.PARAMETER_CLIENT,
                    client);
            HttpURLConnection connection = setupConnection(url, parameters);
            connection.connect();
            String response = parseResponse(connection.getContent());
            connection.disconnect();
            return response;
        } catch (IOException ignore) {
            return null;
        }
    }

    private String parseResponse(Object content)
            throws IOException, ClientDoesNotHavePermissionException {
        InputStreamReader inputStreamReader = new InputStreamReader((InputStream) content);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder result = new StringBuilder();
        String line;
        do {
            line = bufferedReader.readLine();
            result.append(line).append("\n");
        } while (line != null);
        checkForDeniedClientPermission(result.toString());
        return result.toString();
    }

    private void checkForDeniedClientPermission(String response)
            throws ClientDoesNotHavePermissionException {
        switch (response) {
            case ExternalDatabaseDefinitions.RESPONSE_NO_CLIENT_NAME:
                throw new ClientDoesNotHavePermissionException(response.split("\n")[0]);
            case ExternalDatabaseDefinitions.RESPONSE_CLIENT_NO_PERMISSION:
                throw new ClientDoesNotHavePermissionException(response.split("\n")[0]);
        }
    }

    private HttpURLConnection setupConnection(URL url, Map<String, String> parameters)
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10_000);
        connection.setConnectTimeout(15_000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(buildParametersList(parameters));
        writer.flush();
        writer.close();
        os.close();
        return connection;
    }

    private String buildParametersList(Map<String, String> parameters) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        try {
            for (String key : parameters.keySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(parameters.get(key));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
