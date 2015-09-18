package com.tidal.playlist.service.interfaces;

import com.tidal.playlist.data.PlayListTrack;
import com.tidal.playlist.data.Track;
import com.tidal.playlist.data.TrackPlayList;
import com.tidal.playlist.exception.PlaylistException;

import java.util.Date;
import java.util.List;

/**
 * Created by jeevmish on 17.09.2015.
 */
public interface PlaylistBusinessIntf {
    public TrackPlayList addTracks(String uuid, int userId, List<Track> tracksToAdd, int toIndex,
                                         Date lastUpdated) throws PlaylistException;

    public TrackPlayList removeTracks(String uuid, int userId, List<Track> tracksToDelete,
                                            Date lastUpdated) throws PlaylistException;
}
