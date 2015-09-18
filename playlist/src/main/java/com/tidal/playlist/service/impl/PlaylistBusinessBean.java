package com.tidal.playlist.service.impl;

import com.google.inject.Inject;
import com.tidal.playlist.com.tidal.playlist.util.Action;
import com.tidal.playlist.com.tidal.playlist.util.PlayListUtil;
import com.tidal.playlist.dao.PlaylistDaoBean;
import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;
import com.tidal.playlist.exception.PlaylistException;
import com.tidal.playlist.service.interfaces.PlaylistBusinessIntf;
import com.tidal.playlist.service.validation.PlaylistBusinessValidations;

import java.util.*;

/**
 * @author: eivind.hognestad@wimpmusic.com
 * Date: 15.04.15
 * Time: 12.45
 */
public class PlaylistBusinessBean implements PlaylistBusinessIntf {

    private PlaylistDaoBean playlistDaoBean;
    private PlayListUtil playListUtil;
    private PlaylistBusinessValidations validations;

    @Inject
    public PlaylistBusinessBean(PlaylistDaoBean playlistDaoBean, PlayListUtil playListUtil, PlaylistBusinessValidations validations){
        this.playlistDaoBean = playlistDaoBean;
        this.playListUtil = playListUtil;
        this.validations = validations;

    }

    public TrackPlayList addTracks(String uuid, int userId, List<Track> tracksToAdd,
                                         int toIndex,
                                  Date lastUpdated) throws PlaylistException {

        try {

            TrackPlayList playList = playlistDaoBean.getPlaylistByUUID(uuid, userId);
            if(playList == null){
                throw new PlaylistException("Could not find the PlayList");
            }
            /*
            Extracted the relevant details of PlayList in local variables from the
            performance ,re-usability, thread-safety and debug aspect.
             */
            int currentTracksCount = playList.getNrOfTracks();
            int sizeOfTracksToBeAdded = tracksToAdd.size();
            int playListTrackSize = playList.getPlayListTracksSize();
            validations.validateNewTrackListLimit(currentTracksCount, sizeOfTracksToBeAdded, Action.ADD);
            toIndex = playListUtil.alignIndex(toIndex, playListTrackSize);

            // In My View this is already take care in alignIndex Method.
            if (!validations.validateIndexes(toIndex, currentTracksCount)) {
                return playList;
            }

            Set<PlayListTrack> originalSet = playList.getPlayListTracks();
            List<PlayListTrack> original;
            if (originalSet == null || originalSet.size() == 0)
                original = new ArrayList<PlayListTrack>();
            else
                original = new ArrayList<PlayListTrack>(originalSet);

            //merge new tracks with original tracks, thereafter rearrange and reindex.
            List<PlayListTrack> refreshedPlayListTracks =
                    playListUtil.getFreshlyMergedAndIndexedPlayListTracks(tracksToAdd, toIndex, lastUpdated,
                            playList, original);
            //refresh playlist.
            playList = playListUtil.refreshPlayList(playList, refreshedPlayListTracks);

            return playList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new PlaylistException(e.getMessage());
        }
    }



    public TrackPlayList removeTracks(String uuid, int userId, List<Track> tracksToDelete,
                                  Date lastUpdated) throws PlaylistException {

        try {

            TrackPlayList playList = playlistDaoBean.getPlaylistByUUID(uuid, userId);
            if(playList == null){
                throw new PlaylistException("Could not find the PlayList");
            }
            /*
            Extracted the relevant details of PlayList in local variables from the
            performance ,re-usability, thread-safety and debug aspect.
             */
            int currentTracksCount = playList.getNrOfTracks();
            int sizeOfTracksToBeDeleted = tracksToDelete.size();
            int playListTrackSize = playList.getPlayListTracksSize();

            validations.validateNewTrackListLimit(currentTracksCount, sizeOfTracksToBeDeleted, Action.DELETE);

            Set<PlayListTrack> originalSet = playList.getPlayListTracks();
            List<PlayListTrack> original;
            if (originalSet == null || originalSet.size() == 0)
                original = new ArrayList<PlayListTrack>();
            else
                original = new ArrayList<PlayListTrack>(originalSet);


            List<PlayListTrack> refreshedPlayListTracks =
                    playListUtil.getPlayListTracksPostDeletion(tracksToDelete, lastUpdated,
                            playList, original);

            playList = playListUtil.refreshPlayList(playList, refreshedPlayListTracks);

            return playList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new PlaylistException(e.getMessage());
        }
    }

}
