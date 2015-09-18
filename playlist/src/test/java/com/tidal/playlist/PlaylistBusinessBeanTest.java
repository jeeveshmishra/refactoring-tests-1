package com.tidal.playlist;

import com.google.inject.Inject;
import com.tidal.playlist.dao.PlaylistDaoBean;
import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;
import com.tidal.playlist.exception.PlaylistException;
import com.tidal.playlist.service.impl.PlaylistBusinessBean;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author: eivind.hognestad@wimpmusic.com
 * Date: 15.04.15
 * Time: 14.32
 */
@Guice(modules = TestBusinessModule.class)
public class PlaylistBusinessBeanTest {

    @Inject
    PlaylistBusinessBean playlistBusinessBean;

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddTracks() throws Exception {
        List<Track> trackList = populateTrackList();
        String uuid = UUID.randomUUID().toString();
        int userId = 1;
        PlaylistDaoBean playlistDaoBean = new PlaylistDaoBean();
        //Pull the PlayList facts before addup.
        TrackPlayList trackPlayListBefore = playlistDaoBean.getPlaylistByUUID(uuid, userId);
        int trackSizeBefore = trackPlayListBefore.getPlayListTracksSize();
        float durationBefore = trackPlayListBefore.getDuration();
        int noOfTracksBefore = trackPlayListBefore.getNrOfTracks();
        // add new tracks
        TrackPlayList trackPlayListAfterAdd = playlistBusinessBean.addTracks(uuid, userId, trackList, 5, new Date());
        //Pull the playlist facts after - But from the returned playListTracks.
        int trackSizeAfter = trackPlayListAfterAdd.getPlayListTracks().size();
        float durationAfter = PlaylistDaoBean.calculateDurationForTrackPlayList(trackPlayListAfterAdd.getPlayListTracks());
        // Compare facts before and after.
        assertTrue(trackSizeAfter > 0);
        assertTrue(trackSizeBefore <= trackSizeAfter);
        assertTrue(durationBefore <= durationAfter);

    }

    @Test(expectedExceptions = PlaylistException.class)
    public void testAddTracksToOverLimit() throws Exception {
        List<Track> trackList = populateTrackListToTestException();
        String uuid = UUID.randomUUID().toString();
        int userId = 1;
        PlaylistDaoBean playlistDaoBean = new PlaylistDaoBean();
        // add new tracks
        TrackPlayList trackPlayListAfterAdd = playlistBusinessBean.addTracks(uuid, userId, trackList, 5, new Date());
        fail("Playlist cannot have more than 500 tracks");
    }

    @Test(expectedExceptions = PlaylistException.class)
    public void testRemoveTracksBeyondLimit() throws Exception {
        List<Track> trackList = populateTrackListToTestException();
        String uuid = UUID.randomUUID().toString();
        int userId = 1;
        PlaylistDaoBean playlistDaoBean = new PlaylistDaoBean();
        // remove tracks
        TrackPlayList trackPlayListAfterAdd = playlistBusinessBean.removeTracks(uuid, userId, trackList, new Date());
        fail("Playlist has less tracks count than to be deleted");
    }

    @Test
    public void testRemoveTracks() throws Exception {
        List<Track> trackList = populateTrackList();
        String uuid = UUID.randomUUID().toString();
        int userId = 1;
        PlaylistDaoBean playlistDaoBean = new PlaylistDaoBean();
        //Pull the PlayList facts before addup.
        TrackPlayList trackPlayListBefore = playlistDaoBean.getPlaylistByUUID(uuid, userId);
        int trackSizeBefore = trackPlayListBefore.getPlayListTracksSize();
        float durationBefore = trackPlayListBefore.getDuration();
        int noOfTracksBefore = trackPlayListBefore.getNrOfTracks();
        // add new tracks
        TrackPlayList playListTracks = playlistBusinessBean.addTracks(uuid, userId, trackList, 5, new Date());
        //Pull the playlist facts after - But from the returned playListTracks.
        int trackSizeAfterAddition = playListTracks.getPlayListTracks().size();
        float durationAfterAddition = PlaylistDaoBean.calculateDurationForTrackPlayList(playListTracks.getPlayListTracks());
        // Compare facts before and after.
        assertTrue(trackSizeAfterAddition > 0);
        assertTrue(trackSizeBefore <= trackSizeAfterAddition);
        assertTrue(durationBefore <= durationAfterAddition);
        //Now call deletion or removal
        TrackPlayList playListTracksPostDeletion = playlistBusinessBean.removeTracks(uuid, userId, trackList, new Date());
        int trackSizeAfterDeletion = playListTracksPostDeletion.getPlayListTracks().size();
        float durationAfterDeletion = PlaylistDaoBean.calculateDurationForTrackPlayList(playListTracksPostDeletion.getPlayListTracks());
        // Compare facts before and after.
        assertTrue(trackSizeAfterDeletion > 0);
        assertTrue(trackSizeAfterDeletion <= trackSizeAfterAddition);
        assertTrue(durationAfterDeletion <= durationAfterAddition);
    }



    private List<Track> populateTrackList() {
        List<Track> trackList = new ArrayList<Track>();

        Track track = new Track();
        track.setArtistId(4);
        track.setTitle("A brand new track");
        track.setTrackNumberIdx(1);
        track.setId(76868);
        track.setDuration(30*4);

        trackList.add(track);
        return trackList;
    }

    private List<Track> populateTrackListToTestException() {
        List<Track> trackList = new ArrayList<Track>();

        for (int i = 0; i < 500; i++) {
            Track track = new Track();
            track.setArtistId(i);
            track.setTitle("A brand new track " + i);
            track.setTrackNumberIdx(i);
            track.setId(76868+i);
            track.setDuration(30*4);

            trackList.add(track);
        }
        return trackList;
    }
}