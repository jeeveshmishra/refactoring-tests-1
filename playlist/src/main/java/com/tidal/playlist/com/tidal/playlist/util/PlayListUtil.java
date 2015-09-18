package com.tidal.playlist.com.tidal.playlist.util;

import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by jeevmish on 17.09.2015.
 */
public class PlayListUtil {

    /**
     * This Method will precisely add up the original TrackList with
     * newly selected tracks chosen to be added.
     * @param lastUpdated
     * @param playList
     * @param original
     * @return
     */
    public List<PlayListTrack> getPlayListTracksPostDeletion(List<Track> tracksToDelete, Date lastUpdated,
                                                                TrackPlayList playList, List<PlayListTrack> original) {
        List<PlayListTrack> deleted = new ArrayList<PlayListTrack>(tracksToDelete.size());

        for (Track track : tracksToDelete) {
            PlayListTrack playlistTrack = new PlayListTrack();
            playlistTrack.setTrackPlaylist(playList);
            playlistTrack.setTrackArtistId(track.getArtistId());
            playlistTrack.setDateAdded(lastUpdated);
            playlistTrack.setTrack(track);
            playList.setDuration(removeTrackDurationFromPlaylist(playList, track));
            original.remove(playlistTrack);
            deleted.add(playlistTrack);
        }

        reIndexPlayTrackList(original);
        return original;
    }

    public List<PlayListTrack> getFreshlyMergedAndIndexedPlayListTracks(List<Track> tracksToAdd, int toIndex,
                                                                        Date lastUpdated, TrackPlayList playList,
                                                                        List<PlayListTrack> original) {
        List<PlayListTrack> added = new ArrayList<PlayListTrack>(tracksToAdd.size());

        for (Track track : tracksToAdd) {
            PlayListTrack playlistTrack = new PlayListTrack();
            playlistTrack.setTrackPlaylist(playList);
            playlistTrack.setTrackArtistId(track.getArtistId());
            playlistTrack.setDateAdded(lastUpdated);
            playlistTrack.setTrack(track);
            playList.setDuration(addTrackDurationToPlaylist(playList, track));
            original.add(toIndex, playlistTrack);
            added.add(playlistTrack);
            toIndex++;
        }

        reIndexPlayTrackList(original);
        return original;
    }

    /**
     * This method reindexes the tracks list.
     * @param original
     */
    public void reIndexPlayTrackList(List<PlayListTrack> original) {
        Collections.sort(original);
        int i = 0;
        for (PlayListTrack track : original) {
            track.setIndex(i++);
        }
    }

    /**
     *
     * @param playList
     * @param track
     * @return
     */
    private float addTrackDurationToPlaylist(TrackPlayList playList, Track track) {
        return (track != null ? track.getDuration() : 0)
                + (playList != null && playList.getDuration() != null ? playList.getDuration() : 0);
    }

    private float removeTrackDurationFromPlaylist(TrackPlayList playList, Track track) {
        return (playList != null && playList.getDuration() != null ? playList.getDuration() : 0)
         - (track != null ? track.getDuration() : 0);
    }

    /**
     *
     * @param toIndex
     * @param playListTrackSize
     * @return
     */
    public int alignIndex(int toIndex, int playListTrackSize) {
        // The index is out of bounds, put it in the end of the list.
        if (toIndex > playListTrackSize || toIndex == -1) {
            toIndex = playListTrackSize;
        }
        return toIndex;
    }

    /**
     * This method is used for refreshing the linked TrackPlayList
     * @param playList
     * @param refreshedPlayListTracks
     */
    public TrackPlayList refreshPlayList(TrackPlayList playList, List<PlayListTrack> refreshedPlayListTracks) {
        playList.getPlayListTracks().clear();
        for(PlayListTrack playListTrack : refreshedPlayListTracks){
            playList.addPlayListTrack(playListTrack);
        }
        //playList.getPlayListTracks().addAll(refreshedPlayListTracks);
        playList.setNrOfTracks(refreshedPlayListTracks.size());
        return playList;
    }

}
