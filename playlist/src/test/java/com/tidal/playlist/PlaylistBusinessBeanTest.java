package com.tidal.playlist;

import com.google.inject.Inject;
import com.tidal.playlist.dao.PlaylistDaoBean;
import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;
import com.tidal.playlist.service.impl.PlaylistBusinessBean;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.AssertJUnit.assertTrue;

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
        List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(uuid, userId, trackList, 5, new Date());
        //Pull the playlist facts after - But from the returned playListTracks.
        int trackSizeAfter = playListTracks.size();
        Set<PlayListTrack> playListTracksSet = new HashSet<PlayListTrack>(playListTracks);
        float durationAfter = PlaylistDaoBean.calculateDurationForTrackPlayList(playListTracksSet);
        // Compare facts before and after.
        assertTrue(playListTracks.size() > 0);
        assertTrue(trackSizeBefore <= trackSizeAfter);
        assertTrue(durationBefore <= durationAfter);

    }

    public List<Track> populateTrackList() {
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
}