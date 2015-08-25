package com.ivanbratoev.festpal.datamodel.db.internal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ivanbratoev.festpal.datamodel.Concert;
import com.ivanbratoev.festpal.datamodel.Festival;

import java.util.Date;

/**
 * Helper singleton for accessing the internal database
 */
public class InternalDatabaseHandler {

    private static InternalDatabaseHandler instance;

    private InternalDBHelper dbHelper;

    protected InternalDatabaseHandler() {

    }

    /**
     *
     * @param context context. Must not be null upon instantiation of the singleton
     * @return singleton instance
     */
    public static InternalDatabaseHandler getInstance(Context context){
        if (instance == null){
            instance = new InternalDatabaseHandler();
            instance.dbHelper = new InternalDBHelper(context);
        }
        return instance;
    }

    /**
     *
     * @return true if festivals are recorded in the database, false otherwise
     */
    public boolean hasFestivals(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                new String[]{InternalDBContract.FestivalEntry._ID},
                null, null, null, null, null);

        boolean result = (cursor.getCount() != 0);

        cursor.close();

        return result;
    }

    /**
     *
     * @param festival festival to check for
     * @return true if there are concerts recorded for the input festival, false otherwise
     */
    public boolean hasConcerts(Festival festival){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                new String[]{InternalDBContract.FestivalEntry._ID},
                InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + "=?",
                new String[]{String.valueOf(festival.getId())},
                null, null, null);

        boolean result = (cursor.getCount() != 0);

        cursor.close();

        return result;
    }

    public Festival[] getFestivals(){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.FestivalEntry.TABLE_NAME,
                null, null, null, null, null, null);

        Festival[] result = new Festival[cursor.getCount()];

        cursor.moveToFirst();
        int i = 0;

        while (!cursor.isAfterLast()){
            result[i] = new Festival(
            cursor.getInt(cursor.getColumnIndex(InternalDBContract.FestivalEntry._ID)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES)),
            cursor.getString(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_UPLOADER)),
            (1 == cursor.getInt(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL))),
            cursor.getInt(cursor.getColumnIndex(InternalDBContract.FestivalEntry.COLUMN_NAME_RANK))
            );
            i++;
            cursor.moveToNext();
        }

        cursor.close();

        return result;
    }

    public long addFestival(Festival festival){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME, festival.getName());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION, festival.getDescription());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY, festival.getCountry());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY, festival.getCity());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS, festival.getAddress());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE, festival.getGenre());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES, festival.getPrices());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_UPLOADER, festival.getUploader());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL, festival.isOfficial());
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_RANK, festival.getRank());

        return db.insert(InternalDBContract.FestivalEntry.TABLE_NAME, null, values);
    }

    public void editFestival(int id, String name, String description, String country,
                             String city, String address, String genre, String prices,
                             String uploader, Boolean official, Integer rank){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (name != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME, name);
        if (description != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION, description);
        if (country != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY, country);
        if (city != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY, city);
        if (address != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS, address);
        if (genre != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE, genre);
        if (prices != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES, prices);
        if (uploader != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_UPLOADER, uploader);
        if (official != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL, official);
        if (rank != null)
            values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_RANK, rank);

        db.update(InternalDBContract.FestivalEntry.TABLE_NAME,
                values,
                InternalDBContract.FestivalEntry._ID + " LIKE ? ",
                new String[]{String.valueOf(id)});
    }

    public void removeFestival(int id){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(InternalDBContract.FestivalEntry.TABLE_NAME,
                InternalDBContract.FestivalEntry._ID + "LIKE ?",
                new String[]{String.valueOf(id)});
    }

    public Concert[] getConcerts(Festival festival){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(InternalDBContract.ConcertEntry.TABLE_NAME,
                null,
                "WHERE " + InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + "=?",
                new String[]{String.valueOf(festival.getId())},
                null, null, null);

        Concert[] result = new Concert[cursor.getCount()];

        cursor.moveToFirst();
        int i = 0;

        while (!cursor.isAfterLast()){
            result[i] = new Concert(
                    festival,
                    cursor.getString(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST)),
                    cursor.getString(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_GENRE)),
                    cursor.getInt(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_SCENE)),
                    cursor.getInt(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_DAY)),
                    new Date(cursor.getInt(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_START))),
                    new Date(cursor.getInt(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_END))),
                    (1 == cursor.getInt(cursor.getColumnIndex(InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY)))
            );
            i++;
            cursor.moveToNext();
        }

        cursor.close();

        return result;
    }

    public long addConcert(Concert concert){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL, concert.getFestival().getId());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST, concert.getArtist());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_GENRE, concert.getGenre());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_SCENE, concert.getScene());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_DAY, concert.getDay());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_START, concert.getStart().getTime());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_END, concert.getEnd().getTime());
        values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY, concert.isNotify());

        return db.insert(InternalDBContract.ConcertEntry.TABLE_NAME, null, values);
    }

    public void editConcert(int festivalOld, String artistOld, Integer festival,
                            String artist, String genre, Integer scene,
                            Integer day, Date start, Date end, Boolean notify){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        if (festival != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL, festival);
        if (artist != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST, artist);
        if (genre != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_GENRE, genre);
        if (scene != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_SCENE, scene);
        if (day != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_DAY, day);
        if (start != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_START, start.getTime());
        if (end != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_END, end.getTime());
        if (notify != null)
            values.put(InternalDBContract.ConcertEntry.COLUMN_NAME_NOTIFY, notify);

        db.update(InternalDBContract.ConcertEntry.TABLE_NAME,
                values,
                InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + " LIKE ? AND " +
                        InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST + " LIKE ?",
                new String[]{String.valueOf(festivalOld), artistOld});

    }
    public void removeConcert(int festival, String artist){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(InternalDBContract.ConcertEntry.TABLE_NAME,
                InternalDBContract.ConcertEntry.COLUMN_NAME_FESTIVAL + " LIKE ? AND "
                        + InternalDBContract.ConcertEntry.COLUMN_NAME_ARTIST + " LIKE ? ",
                new String[]{String.valueOf(festival), artist});
    }
}
