package com.tidal.playlist;

import com.google.inject.Inject;
import com.tidal.playlist.dao.PlaylistDaoBean;
import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;
import com.tidal.playlist.exception.PlaylistException;

import java.util.*;

/**
 * @author: eivind.hognestad@wimpmusic.com
 * Date: 15.04.15
 * Time: 12.45
 */
public class PlaylistBusinessBean {

    private PlaylistDaoBean playlistDaoBean;

    @Inject
    public PlaylistBusinessBean(PlaylistDaoBean playlistDaoBean){
        this.playlistDaoBean = playlistDaoBean;
    }

    List<PlayListTrack> addTracks(String uuid, int userId, List<Track> tracksToAdd, int toIndex,
                                  Date lastUpdated) throws PlaylistException {

        try {

            TrackPlayList playList = playlistDaoBean.getPlaylistByUUID(uuid, userId);
            /*
            Extracted the relevant details of PlayList in local variables from the
            performance ,re-usability, thread-safety and debug aspect.
             */
            int currentTracksCount = playList.getNrOfTracks();
            int sizeOfTracksToBeAdded = tracksToAdd.size();
            int playListTrackSize = playList.getPlayListTracksSize();
            validateNewTrackListLimit(currentTracksCount, sizeOfTracksToBeAdded);
            toIndex = alignIndex(toIndex, playListTrackSize);

            // In My View this is already take care in alignIndex Method.
            if (!validateIndexes(toIndex, currentTracksCount)) {
                return Collections.EMPTY_LIST;
            }

            Set<PlayListTrack> originalSet = playList.getPlayListTracks();
            List<PlayListTrack> original;
            if (originalSet == null || originalSet.size() == 0)
                original = new ArrayList<PlayListTrack>();
            else
                original = new ArrayList<PlayListTrack>(originalSet);


            List<PlayListTrack> refreshedPlayListTracks =
                    getFreshlyMergedAndIndexedPlayListTracks(tracksToAdd, toIndex, lastUpdated,
                            playList, original);

            playList.getPlayListTracks().clear();
            playList.getPlayListTracks().addAll(refreshedPlayListTracks);
            playList.setNrOfTracks(refreshedPlayListTracks.size());

            return refreshedPlayListTracks;

        } catch (Exception e) {
            e.printStackTrace();
            throw new PlaylistException("Generic error");
        }
    }

    /**
     * This Method will precisely add up the original TrackList with
     * newly selected tracks chosen to be added.
     * @param tracksToAdd
     * @param toIndex
     * @param lastUpdated
     * @param playList
     * @param original
     * @return
     */
    private List<PlayListTrack> getFreshlyMergedAndIndexedPlayListTracks(List<Track> tracksToAdd, int toIndex, Date lastUpdated, TrackPlayList playList, List<PlayListTrack> original) {
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
        return added;
    }

    /**
     * This method reindexes the tracks list.
     * @param original
     */
    private void reIndexPlayTrackList(List<PlayListTrack> original) {
        Collections.sort(original);
        int i = 0;
        for (PlayListTrack track : original) {
            track.setIndex(i++);
        }
    }

    private int alignIndex(int toIndex, int playListTrackSize) {
        // The index is out of bounds, put it in the end of the list.
        if (toIndex > playListTrackSize || toIndex == -1) {
            toIndex = playListTrackSize;
        }
        return toIndex;
    }

    /**
     * This Validation method validates condition:
     * We do not allow > 500 tracks in new playlists
     * @param currentTracksCount
     * @param sizeOfTracksToBeAdded
     */
    private void validateNewTrackListLimit(int currentTracksCount, int sizeOfTracksToBeAdded) {

        if (currentTracksCount +  sizeOfTracksToBeAdded> 500) {
            throw new PlaylistException("Playlist cannot have more than " + 500 + " tracks");
        }
    }

    /**
     * In My View this is not needed but this is my first impression.
     * This method is a kind of extra-precautious check for validating the index
     * where track is to be added must fit in well with in the range of tracks Count now.
     * @param toIndex
     * @param tracksCountNow
     * @return
     */
    private boolean validateIndexes(int toIndex, int tracksCountNow) {
        return toIndex >= 0 && toIndex <= tracksCountNow;
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
}
