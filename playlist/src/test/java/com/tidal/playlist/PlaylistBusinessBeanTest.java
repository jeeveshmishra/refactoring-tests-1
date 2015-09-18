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

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author: eivind.hognestad@wimpmusic.com
 * Date: 15.04.15
 * Time: 14.32
 */
@Guice(modules = TestBusinessModule.class)
public class PlaylistBusinessBeanTest {

    String uuid = UUID.randomUUID().toString();
    int userId = 1;

    @Inject
    PlaylistBusinessBean playlistBusinessBean;

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    /**
     * This method tests scenario of addition of tracks to a playlist.
     * Thus it asserts:
     * (1) tracks count - before and after
     * (2) play list duration - before and after
     * @throws Exception
     */
    @Test
    public void testAddTracks() throws Exception {
        List<Track> trackList = populateTrackList();
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

    /**
     * This method tests scenario of addition of more than 500 songs to the play list.
     * Thus it must throw exception with proper method.
     * @throws Exception
     */
    @Test(expectedExceptions = PlaylistException.class)
    public void testAddTracksToOverLimit() throws Exception {
        List<Track> trackList = populateTrackListToTestException();
        // add new tracks
        TrackPlayList trackPlayListAfterAdd = playlistBusinessBean.addTracks(uuid, userId, trackList, 5, new Date());
        fail("Playlist cannot have more than 500 tracks");
    }

    /**
     * This method tests scenario of removal of tracks from play list where
     * no of songs to be removed > no of songs within the playlist.
     * @throws Exception
     */
    @Test(expectedExceptions = PlaylistException.class)
    public void testRemoveTracksBeyondLimit() throws Exception {
        List<Track> trackList = populateTrackListToTestException();
        // remove tracks
        playlistBusinessBean.removeTracks(uuid, userId, trackList, new Date());
        fail("Playlist has less tracks count than to be deleted");
    }

    /**
     * This method tests scenario of removal of tracks from play list and subsequently
     * checks for the :
     * (1) No. of tracks - before and after deletion (but has code for addition of tracks too
     * because then we would know which song we added and which is deleted.)
     * (2) Also the tracks duration subsequently - before and after deletion.
     * @throws Exception
     */
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
        assertFalse(containsTrack(playListTracksPostDeletion.getPlayListTracks(), trackList));
    }

    /**
     * Ideally this method must be in Util class but for sake of clarity in test cases, I have kept it here.
     * For reusability purpose, this can be moved.
     * @param playListTracks
     * @param trackList
     * @return
     */
    private boolean containsTrack(Set<PlayListTrack> playListTracks, List<Track> trackList) {
        boolean hasTrack = false;
        for(PlayListTrack playListTrack:playListTracks){
            for(Track track:trackList){
                if((playListTrack.getTrack().equals(track))){
                    hasTrack = true;
                    return hasTrack;
                } else{
                    continue;
                }
            }
        }
        return hasTrack;
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