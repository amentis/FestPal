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

import android.test.InstrumentationTestCase;

import com.ivanbratoev.festpal.datamodel.db.external.ExternalDatabaseHandler;
import com.ivanbratoev.festpal.datamodel.db.internal.InternalDatabaseHandler;

import junit.framework.Assert;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Date;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DataModelTest extends InstrumentationTestCase {

    @Mock
    private InternalDatabaseHandler internalDatabaseHandler;
    @Mock
    private ExternalDatabaseHandler externalDatabaseHandler;

    @InjectMocks
    private DataModel dataModel = DataModel.getInstance();

    public void setUp() throws Exception {
        super.setUp();
        System.setProperty(
                "dexmaker.dexcache",
                getInstrumentation().getTargetContext().getCacheDir().getPath()
        );
        initMocks(this);

    }

    public void testLogInSuccessful() throws Exception {
        dataModel.setUsername(null); //clean singleton from previous data having been set
        String username = "test";
        String password = "test";
        when(externalDatabaseHandler.login(username, password)).thenReturn(0);
        int result = dataModel.logInExternalDatabase(username, password);
        Assert.assertEquals(0, result);
        Assert.assertEquals(username, dataModel.getUsername());
    }

    public void testLogInUnsuccessful() throws Exception {
        dataModel.setUsername(null); //clean singleton from previous data having been set
        String username = "test";
        String password = "test";
        when(externalDatabaseHandler.login(username, password)).thenReturn(1);
        int result = dataModel.logInExternalDatabase(username, password);
        Assert.assertEquals(1, result);
        Assert.assertEquals(null, dataModel.getUsername());

        when(externalDatabaseHandler.login(username, password)).thenReturn(2);
        result = dataModel.logInExternalDatabase(username, password);
        Assert.assertEquals(2, result);
        Assert.assertEquals(null, dataModel.getUsername());

        when(externalDatabaseHandler.login(username, password)).thenReturn(3);
        result = dataModel.logInExternalDatabase(username, password);
        Assert.assertEquals(3, result);
        Assert.assertEquals(null, dataModel.getUsername());

        when(externalDatabaseHandler.login(username, password)).thenReturn(4);
        result = dataModel.logInExternalDatabase(username, password);
        Assert.assertEquals(4, result);
        Assert.assertEquals(null, dataModel.getUsername());

        when(externalDatabaseHandler.login(username, password)).thenReturn(5);
        result = dataModel.logInExternalDatabase(username, password);
        Assert.assertEquals(5, result);
        Assert.assertEquals(null, dataModel.getUsername());
    }

    public void testLogInInitNotCalled() throws Exception {
        try {
            dataModel.setUsername(null); //clean singleton from previous data having been set
            String username = "test";
            String password = "test";
            //simulate externalDatabaseHandler being null, which would be the result of init()
            //not having been called
            when(externalDatabaseHandler.login(username, password)).
                    thenThrow(new NullPointerException());
            dataModel.logInExternalDatabase(username, password);
            fail("Expected NullPointerException not thrown!");
        } catch (NullPointerException ignore) {

        }
    }

    public void testLogOutSuccessful() throws Exception {
        String username = "test";
        dataModel.setUsername(username); //set singleton to fake logged-in state
        when(externalDatabaseHandler.logout()).thenReturn(true);
        Assert.assertTrue(dataModel.logOutExternalDatabase());
        Assert.assertNull(dataModel.getUsername());
    }

    public void testLogOutUnsuccessful() throws Exception {
        String username = "test";
        dataModel.setUsername(username); //set singleton to fake logged-in state
        when(externalDatabaseHandler.logout()).thenReturn(false);
        Assert.assertFalse(dataModel.logOutExternalDatabase());
        Assert.assertEquals(username, dataModel.getUsername());
    }

    public void testReadFestivalInfoUpdateFestivalSynchronised() throws Exception {
        long id = 0;
        String festival1Name = "Non updated festival";
        String festival2Name = "Updated festival";
        Festival festival1 = festival(id, festival1Name);
        Festival festival2 = festival(id, festival2Name);
        when(internalDatabaseHandler.getFestival(id)).thenReturn(festival1);
        when(externalDatabaseHandler.readFestivalInfo(id)).thenReturn(festival2);
        Festival result = dataModel.readFestivalInfo(id, true);
        if (result == null) {
            fail("Returned value is null");
        } else {
            Assert.assertEquals(festival2Name, result.getName());
        }
        verify(internalDatabaseHandler).editFestival(id, festival2Name,
                null, null, null, null, null, null, null, null, null);
    }

    public void testWriteFestivalInfoOnlineWrongUsername() throws Exception {
        String username = "wrong";
        String usernameInFest = "right";
        String festName = "testFest";
        int id = 0;
        dataModel.setUsername(username);
        Festival festival = festival(id, festName);
        festival.setOwner(usernameInFest);
        when(internalDatabaseHandler.getFestival(0)).thenReturn(festival);
        assertEquals(-1, dataModel.writeFestivalInfo(festival, true));

        verify(internalDatabaseHandler, never()).addFestival(festival);
        verify(externalDatabaseHandler, never()).writeFestivalInfo(festival.getName(),
                festival.getDescription(), festival.getCountry(), festival.getCity(),
                festival.getAddress(), festival.getGenre(), festival.getPrices(),
                festival.isOfficial());
        verify(internalDatabaseHandler, never()).editFestival(festival.getId(), festival.getName(),
                festival.getDescription(), festival.getCountry(), festival.getCity(),
                festival.getAddress(), festival.getGenre(), festival.getPrices(),
                festival.getOwner(), festival.isOfficial(), festival.getVotes());
        verify(externalDatabaseHandler, never()).updateFestivalInfo(festival.getExternalId(),
                festival.getName(), festival.getDescription(), festival.getCountry(),
                festival.getCity(), festival.getAddress(), festival.getGenre(),
                festival.getPrices(), festival.isOfficial());
    }

    public void testWriteFestivalInfoOnlineNewFestSuccessful() throws Exception {
        String username = "test";
        long id = 0;
        String festName = "testFest";
        Festival newFestival = festival(id, festName);
        dataModel.setUsername(username);
        newFestival.setOwner(username);
        when(internalDatabaseHandler.getFestival(newFestival.getId())).thenReturn(null);
        when(internalDatabaseHandler.addFestival(newFestival)).thenReturn(id);
        when(externalDatabaseHandler.readFestivalInfo(newFestival.getExternalId())).thenReturn(null);
        when(externalDatabaseHandler.writeFestivalInfo(newFestival.getName(),
                newFestival.getDescription(), newFestival.getCountry(), newFestival.getCity(),
                newFestival.getAddress(), newFestival.getGenre(), newFestival.getPrices(),
                newFestival.isOfficial())).thenReturn(true);
        Assert.assertEquals(id, dataModel.writeFestivalInfo(newFestival, true));
        verify(internalDatabaseHandler).addFestival(newFestival);
        verify(externalDatabaseHandler).writeFestivalInfo(newFestival.getName(),
                newFestival.getDescription(), newFestival.getCountry(), newFestival.getCity(),
                newFestival.getAddress(), newFestival.getGenre(), newFestival.getPrices(),
                newFestival.isOfficial());
    }

    public void testWriteFestivalInfoOnlineNewFestServerError() throws Exception {
        String username = "test";
        long id = 0;
        String festName = "testFest";
        Festival newFestival = festival(id, festName);
        dataModel.setUsername(username);
        newFestival.setOwner(username);
        when(internalDatabaseHandler.getFestival(newFestival.getId())).thenReturn(null);
        when(internalDatabaseHandler.addFestival(newFestival)).thenReturn(id);
        when(externalDatabaseHandler.readFestivalInfo(newFestival.getExternalId())).thenReturn(null);
        when(externalDatabaseHandler.writeFestivalInfo(newFestival.getName(),
                newFestival.getDescription(), newFestival.getCountry(), newFestival.getCity(),
                newFestival.getAddress(), newFestival.getGenre(), newFestival.getPrices(),
                newFestival.isOfficial())).thenReturn(false);
        Assert.assertEquals(-1, dataModel.writeFestivalInfo(newFestival, true));
        verify(internalDatabaseHandler, never()).addFestival(newFestival);
        verify(externalDatabaseHandler).writeFestivalInfo(newFestival.getName(),
                newFestival.getDescription(), newFestival.getCountry(), newFestival.getCity(),
                newFestival.getAddress(), newFestival.getGenre(), newFestival.getPrices(),
                newFestival.isOfficial());
    }

    public void testWriteFestivalInfoOnlineUpdateSuccessful() throws Exception {
        String username = "test";
        long id = 0;
        String festName = "testFest";
        String festNameNew = "testFestUpdated";
        Festival oldFestival = festival(id, festName);
        Festival newFestival = festival(id, festNameNew);
        dataModel.setUsername(username);
        oldFestival.setOwner(username);
        newFestival.setOwner(username);
        when(internalDatabaseHandler.getFestival(newFestival.getId())).thenReturn(oldFestival);
        when(externalDatabaseHandler.readFestivalInfo(newFestival.getExternalId())).
                thenReturn(oldFestival);
        when(externalDatabaseHandler.updateFestivalInfo(oldFestival.getExternalId(),
                newFestival.getName(), newFestival.getDescription(), newFestival.getCountry(),
                newFestival.getCity(), newFestival.getAddress(), newFestival.getGenre(),
                newFestival.getPrices(), newFestival.isOfficial())).thenReturn(true);
        Assert.assertEquals(id, dataModel.writeFestivalInfo(newFestival, true));
        verify(internalDatabaseHandler).editFestival(newFestival.getId(), newFestival.getName(),
                newFestival.getDescription(), newFestival.getCountry(), newFestival.getCity(),
                newFestival.getAddress(), newFestival.getGenre(), newFestival.getPrices(),
                newFestival.getOwner(), newFestival.isOfficial(), newFestival.getVotes());
        verify(externalDatabaseHandler).updateFestivalInfo(newFestival.getExternalId(),
                newFestival.getName(), newFestival.getDescription(), newFestival.getCountry(),
                newFestival.getCity(), newFestival.getAddress(), newFestival.getGenre(),
                newFestival.getPrices(), newFestival.isOfficial());
    }


    public void testWriteFestivalInfoUpdateServerError() throws Exception {
        String username = "test";
        long id = 0;
        String festName = "testFest";
        String festNameNew = "testFestUpdated";
        Festival oldFestival = festival(id, festName);
        Festival newFestival = festival(id, festNameNew);
        dataModel.setUsername(username);
        oldFestival.setOwner(username);
        newFestival.setOwner(username);
        when(externalDatabaseHandler.readFestivalInfo(newFestival.getExternalId())).
                thenReturn(oldFestival);
        when(externalDatabaseHandler.updateFestivalInfo(oldFestival.getExternalId(),
                newFestival.getName(), newFestival.getDescription(), newFestival.getCountry(),
                newFestival.getCity(), newFestival.getAddress(), newFestival.getGenre(),
                newFestival.getPrices(), newFestival.isOfficial())).thenReturn(false);
        Assert.assertEquals(-1, dataModel.writeFestivalInfo(newFestival, true));
        verify(internalDatabaseHandler, never()).editFestival(newFestival.getId(), newFestival.getName(),
                newFestival.getDescription(), newFestival.getCountry(), newFestival.getCity(),
                newFestival.getAddress(), newFestival.getGenre(), newFestival.getPrices(),
                newFestival.getOwner(), newFestival.isOfficial(), newFestival.getVotes());
        verify(externalDatabaseHandler).updateFestivalInfo(newFestival.getExternalId(),
                newFestival.getName(), newFestival.getDescription(), newFestival.getCountry(),
                newFestival.getCity(), newFestival.getAddress(), newFestival.getGenre(),
                newFestival.getPrices(), newFestival.isOfficial());
    }

    public void testReadConcertInfoOnlineSuccessful() throws Exception {
        long fest = 0;
        long concertId = 0;
        String artist = "The Testers";
        String artistUpdated = "The Testers Online";
        Festival festival = festival(fest, "testFest");
        Concert concert = concert(concertId, festival, artist);
        Concert concertUpdated = concert(concertId, festival, artistUpdated);
        when(internalDatabaseHandler.getFestival(fest)).thenReturn(festival);
        when(internalDatabaseHandler.getConcert(festival, concert.getId())).thenReturn(concert);
        when(externalDatabaseHandler.readConcertInfo(festival, concert.getId())).thenReturn(concertUpdated);
        Concert result = dataModel.readConcertInfo(fest, concertId, true);
        Assert.assertNotNull(result);
        Assert.assertEquals(artistUpdated, result.getArtist());
    }

    public void testWriteConcertInfoNewSuccessful() throws Exception {
        long fest = 0;
        long concertId = 0;
        String artist = "The Testers";
        Festival festival = festival(fest, "testFest");
        Concert concert = concert(concertId, festival, artist);
        when(internalDatabaseHandler.getConcert(festival, concertId)).thenReturn(null);
        when(internalDatabaseHandler.addConcert(concert)).thenReturn(concertId);
        when(externalDatabaseHandler.writeConcertInfo(concertId, concert.getArtist(),
                concert.getStage(), concert.getDay(),
                concert.getStart(), concert.getEnd())).thenReturn(true);
        Assert.assertTrue(dataModel.writeConcertInfo(concert, true));
        verify(internalDatabaseHandler).addConcert(concert);
        verify(externalDatabaseHandler).writeConcertInfo(concertId, concert.getArtist(),
                concert.getStage(), concert.getDay(),
                concert.getStart(), concert.getEnd());
    }

    public void testWriteConcertInfoNewServerError() throws Exception {
        long fest = 0;
        long concertId = 0;
        String artist = "The Testers";
        Festival festival = festival(fest, "testFest");
        Concert concert = concert(concertId, festival, artist);
        when(internalDatabaseHandler.getConcert(festival, concertId)).thenReturn(null);
        when(externalDatabaseHandler.writeConcertInfo(concertId, concert.getArtist(),
                concert.getStage(), concert.getDay(),
                concert.getStart(), concert.getEnd())).thenReturn(false);
        Assert.assertFalse(dataModel.writeConcertInfo(concert, true));
        verify(internalDatabaseHandler, never()).addConcert(concert);
    }

    public void testWriteConcertInfoUpdateSuccessful() throws Exception {
        long fest = 0;
        long concertId = 0;
        String artist = "The Testers";
        String artistUpdated = "The Testers Updated";
        Festival festival = festival(fest, "testFest");
        Concert concert = concert(concertId, festival, artist);
        Concert concertUpdated = concert(concertId, festival, artistUpdated);
        when(internalDatabaseHandler.getConcert(festival, concertId)).thenReturn(concert);
        when(externalDatabaseHandler.readConcertInfo(festival, concertId)).thenReturn(concert);
        when(externalDatabaseHandler.updateConcertInfo(concertId, concertUpdated.getArtist(),
                null, null, null, null)).thenReturn(true);
        Assert.assertTrue(dataModel.writeConcertInfo(concertUpdated, true));
        verify(internalDatabaseHandler).editConcert(concertUpdated.getId(),
                concertUpdated.getExternalId(),
                null, concertUpdated.getArtist(), null, null, null, null, null);
        verify(externalDatabaseHandler).updateConcertInfo(concertId, concertUpdated.getArtist(),
                null, null, null, null);
    }

    public void testWriteConcertInfoUpdateServerError() throws Exception {
        long fest = 0;
        long concertId = 0;
        String artist = "The Testers";
        String artistUpdated = "The Testers Updated";
        Festival festival = festival(fest, "testFest");
        Concert concert = concert(concertId, festival, artist);
        Concert concertUpdated = concert(concertId, festival, artistUpdated);
        when(internalDatabaseHandler.getConcert(festival, concertId)).thenReturn(concert);
        when(externalDatabaseHandler.readConcertInfo(festival, concertId)).thenReturn(concert);
        when(externalDatabaseHandler.updateConcertInfo(concertId, concertUpdated.getArtist(),
                concertUpdated.getStage(), concertUpdated.getDay(),
                concertUpdated.getStart(), concertUpdated.getEnd())).thenReturn(false);
        Assert.assertFalse(dataModel.writeConcertInfo(concertUpdated, true));
        verify(internalDatabaseHandler, never()).editConcert(concertUpdated.getId(),
                null, null, concertUpdated.getArtist(), null, null, null, null, null);
        verify(externalDatabaseHandler).updateConcertInfo(concertId, concertUpdated.getArtist(),
                null, null,
                null, null);
    }

    public void testSynchroniseDontWriteToExternalFestival() throws Exception {
        String internalFestivalName = "TestFest";
        String externalFestivalName = "TestFestOnline";
        int id = 0;
        Festival internalFestival = festival(id, internalFestivalName);
        Festival externalFestival = festival(id, externalFestivalName);
        when(internalDatabaseHandler.getFestivals()).thenReturn(new Festival[]{internalFestival});
        when(externalDatabaseHandler.readFestivalConcerts(internalFestival)).thenReturn(new Concert[0]);
        when(externalDatabaseHandler.readFestivalInfo(id)).thenReturn(externalFestival);
        Assert.assertTrue(dataModel.synchronise(false));
        verify(internalDatabaseHandler).editFestival(id, externalFestivalName, null, null,
                null, null, null, null, null, null, null);
        verify(externalDatabaseHandler, never()).updateFestivalInfo(id, internalFestivalName, null, null,
                null, null, null, null, null);
    }

    public void testSynchroniseDontWriteToExternalConcert() throws Exception {
        String festivalName = "TestFest";
        int festivalID = 0;
        Festival festival = festival(festivalID, festivalName);
        String artist = "The Testers";
        int stageInternal = 0;
        int stageExternal = 1;
        int concertID = 0;
        Concert internalConcert = concert(concertID, festival, artist);
        Concert externalConcert = concert(concertID, festival, artist);
        internalConcert.setStage(stageInternal);
        externalConcert.setStage(stageExternal);
        when(internalDatabaseHandler.getFestivals()).thenReturn(new Festival[]{festival});
        when(externalDatabaseHandler.readFestivalInfo(festivalID)).thenReturn(festival);
        when(externalDatabaseHandler.readFestivalConcerts(festival)).thenReturn(new Concert[]{externalConcert});
        when(internalDatabaseHandler.getConcert(festival, concertID)).thenReturn(internalConcert);
        Assert.assertTrue(dataModel.synchronise(false));
        verify(internalDatabaseHandler).editConcert(concertID, null, null, null, stageExternal,
                null, null, null, null);
        verify(externalDatabaseHandler, never()).updateConcertInfo(concertID, null, stageExternal,
                null, null, null);
    }

    public void testSynchroniseDontWriteToExternalServerError() throws Exception {
        String internalFestivalName = "TestFest";
        int id = 0;
        Festival internalFestival = festival(id, internalFestivalName);
        when(internalDatabaseHandler.getFestivals()).thenReturn(new Festival[]{internalFestival});
        when(internalDatabaseHandler.getConcerts(internalFestival)).thenReturn(new Concert[0]);
        when(externalDatabaseHandler.readFestivalInfo(id)).thenReturn(null);
        Assert.assertFalse(dataModel.synchronise(false));
        verify(internalDatabaseHandler, never()).editFestival(id, internalFestivalName, null, null,
                null, null, null, null, null, null, null);
        verify(externalDatabaseHandler, never()).updateFestivalInfo(id, internalFestivalName,
                null, null,
                null, null, null, null, null);
    }

    public void testSynchroniseWriteToExternalFestival() throws Exception {
        String internalFestivalName = "TestFest";
        String externalFestivalName = "TestFestOnline";
        int id = 0;
        Festival internalFestival = festival(id, internalFestivalName);
        Festival externalFestival = festival(id, externalFestivalName);
        when(internalDatabaseHandler.getFestivals()).thenReturn(new Festival[]{internalFestival});
        when(externalDatabaseHandler.readFestivalConcerts(internalFestival)).thenReturn(new Concert[0]);
        when(externalDatabaseHandler.readFestivalInfo(id)).thenReturn(externalFestival);
        when(externalDatabaseHandler.updateFestivalInfo(id, internalFestivalName, null, null,
                null, null, null, null, null)).thenReturn(true);
        Assert.assertTrue(dataModel.synchronise(true));
        verify(internalDatabaseHandler).editFestival(id, externalFestivalName, null, null,
                null, null, null, null, null, null, null);
        verify(externalDatabaseHandler, never()).updateFestivalInfo(id, internalFestivalName, null, null,
                null, null, null, null, null);
    }

    public void testSynchroniseWriteToExternalConcert() throws Exception {
        String festivalName = "TestFest";
        int festivalID = 0;
        String owner = "owner";
        Festival festival = festival(festivalID, festivalName);
        festival.setOwner(owner);
        dataModel.setUsername(owner);
        String artist = "The Testers";
        int stageInternal = 0;
        int stageExternal = 1;
        int concertID = 0;
        Concert internalConcert = concert(concertID, festival, artist);
        Concert externalConcert = concert(concertID, festival, artist);
        internalConcert.setStage(3);
        externalConcert.setStage(stageExternal);
        internalConcert.setStage(stageInternal);
        when(internalDatabaseHandler.getFestivals()).thenReturn(new Festival[]{festival});
        when(externalDatabaseHandler.readFestivalInfo(festivalID)).thenReturn(festival);
        when(externalDatabaseHandler.readFestivalConcerts(festival)).thenReturn(new Concert[]{externalConcert});
        when(externalDatabaseHandler.readConcertInfo(festival, concertID)).thenReturn(externalConcert);
        when(externalDatabaseHandler.updateConcertInfo(concertID, null, stageInternal,
                null, null, null)).thenReturn(true);
        when(internalDatabaseHandler.getConcert(festival, concertID)).thenReturn(internalConcert);
        Assert.assertTrue(dataModel.synchronise(true));
        verify(internalDatabaseHandler, never()).editConcert(concertID, null, null, null, stageExternal,
                null, null, null, null);
        verify(externalDatabaseHandler).updateConcertInfo(concertID, null, stageInternal,
                null, null, null);
    }

    public void testSynchroniseWriteToExternalServerError() throws Exception {
        String internalFestivalName = "TestFest";
        String externalFestivalName = "TestFestOnline";
        int id = 0;
        String owner = "owner";
        Festival externalFestival = festival(id, externalFestivalName);
        externalFestival.setOwner(owner);
        Festival internalFestival = festival(id, internalFestivalName);
        internalFestival.setOwner(owner);
        dataModel.setUsername(owner);
        when(internalDatabaseHandler.getFestivals()).thenReturn(new Festival[]{internalFestival});
        when(externalDatabaseHandler.readFestivalConcerts(internalFestival)).thenReturn(new Concert[0]);
        when(externalDatabaseHandler.readFestivalInfo(id)).thenReturn(externalFestival);
        when(externalDatabaseHandler.updateFestivalInfo(id, internalFestivalName, null, null,
                null, null, null, null, null)).thenReturn(false);
        Assert.assertFalse(dataModel.synchronise(true));
        verify(internalDatabaseHandler, never()).editFestival(id, internalFestivalName, null, null,
                null, null, null, null, null, null, null);
        verify(externalDatabaseHandler).updateFestivalInfo(id, internalFestivalName,
                null, null,
                null, null, null, null, null);
    }


    private Festival festival(long id, String name) {
        return new Festival(id, id, name, "", "", "", "", "", "", "", false, 0);
    }

    private Concert concert(long id, Festival festival, String artist) {
        return new Concert(id, id, festival, artist, 0, 0, new Date(), new Date(), false);
    }
}