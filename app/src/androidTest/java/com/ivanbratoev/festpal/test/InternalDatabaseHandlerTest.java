package com.ivanbratoev.festpal.test;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.ivanbratoev.festpal.datamodel.db.internal.InternalDBContract;
import com.ivanbratoev.festpal.datamodel.db.internal.InternalDBHelper;
import com.ivanbratoev.festpal.datamodel.db.internal.InternalDatabaseHandler;

import junit.framework.Assert;

import java.io.File;

public class InternalDatabaseHandlerTest extends AndroidTestCase {

    private InternalDBHelper dbHelper;
    private RenamingDelegatingContext mockContext;

    public void setUp() throws Exception {
        super.setUp();

        final String prefix = "test";
        mockContext = new RenamingDelegatingContext(getContext(), prefix);
        dbHelper = new InternalDBHelper(mockContext);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        dbHelper.close();
        File dbFile = new File(dbHelper.getWritableDatabase().getPath());
        dbFile.delete();
    }

    public void testHasFestivals() throws Exception {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Assert.assertFalse(InternalDatabaseHandler.getInstance(mockContext).hasFestivals());

        ContentValues values = new ContentValues();
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_NAME, "name");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_DESCRIPTION, "description");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_COUNTRY, "country");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_CITY, "city");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_ADDRESS, "address");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_GENRE, "genre");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_PRICES, "price");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_UPLOADER, "uploader");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_OFFICIAL, "0");
        values.put(InternalDBContract.FestivalEntry.COLUMN_NAME_RANK, "5");

        db.insert(InternalDBContract.FestivalEntry.TABLE_NAME, null, values);

        Assert.assertTrue(InternalDatabaseHandler.getInstance(mockContext).hasFestivals());
    }

    public void testHasConcerts() throws Exception {

    }

    public void testGetFestivals() throws Exception {

    }

    public void testAddFestival() throws Exception {

    }

    public void testEditFestival() throws Exception {

    }

    public void testRemoveFestival() throws Exception {

    }

    public void testGetConcerts() throws Exception {

    }

    public void testAddConcert() throws Exception {

    }

    public void testEditConcert() throws Exception {

    }

    public void testRemoveConcert() throws Exception {

    }
}