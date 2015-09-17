package com.tidal.playlist.dao;

/**
 * @author: eivind.hognestad@wimpmusic.com
 * Date: 15.04.15
 * Time: 13.07
 */

import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.SharingLevel;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;

import java.util.*;

/**
 * Class faking the data layer, and returning fake playlists
 */
public class PlaylistDaoBean {

    /**
     * Returning fake TrackPlaylist
     *
     * @param uuid
     * @param userId
     * @return
     */
    public TrackPlayList getPlaylistByUUID(String uuid, int userId) {
        TrackPlayList trackPlayList = new TrackPlayList();

        trackPlayList.setDeleted(false);
        trackPlayList.setDescription("The mother of all playlists");
        trackPlayList.setDuration((float) 0);
        trackPlayList.setId(49834);
        trackPlayList.setLastUpdated(new Date());
        trackPlayList.setNrOfTracks(376);
        trackPlayList.setPlayListName("Collection of great songs");
        Set<PlayListTrack> playListTracks = getPlaylistTracks();
        trackPlayList.setDuration(calculateDurationForTrackPlayList(playListTracks));
        trackPlayList.setPlayListTracks(playListTracks);
        trackPlayList.setUserId(userId);
        trackPlayList.setSharingLevel(SharingLevel.PUBLIC);
        trackPlayList.setUuid(uuid);

        return trackPlayList;
    }

    public static float calculateDurationForTrackPlayList(Set<PlayListTrack> playListTracks) {
        float tempDuration = 0;
        for(PlayListTrack playListTrack:playListTracks){
            tempDuration = tempDuration + playListTrack.getTrack().getDuration();
        }
        return tempDuration;
    }

    private static Set<PlayListTrack> getPlaylistTracks() {

        Set<PlayListTrack> playListTracks = new HashSet<PlayListTrack>();
        for (int i = 0; i < 376; i++) {
            PlayListTrack playListTrack = new PlayListTrack();
            playListTrack.setDateAdded(new Date());
            playListTrack.setDescription("A description" + (i+1));
            playListTrack.setId(i + 1);
            playListTrack.setIndex(i);
            playListTrack.setSharingLevel(SharingLevel.PUBLIC);
            playListTrack.setTrack(getTrack());
            playListTracks.add(playListTrack);

        }

        return playListTracks;
    }

    public static Track getTrack() {
        Random randomGenerator = new Random();

        Track track = new Track();
        track.setArtistId(randomGenerator.nextInt(10000));
        track.setDuration(60 * 3);

        int trackNumber = randomGenerator.nextInt(15);
        track.setTitle("Track no: " + trackNumber);
        track.setTrackNumberIdx(trackNumber);

        return track;
    }
}
