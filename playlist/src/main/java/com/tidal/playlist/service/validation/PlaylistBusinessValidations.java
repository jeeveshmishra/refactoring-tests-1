package com.tidal.playlist.service.validation;

import com.tidal.playlist.com.tidal.playlist.util.Action;
import com.tidal.playlist.exception.PlaylistException;

/**
 * Created by jeevmish on 17.09.2015.
 */
public class PlaylistBusinessValidations {
    /**
     * This generic Validation method validates condition:
     * (1) We do not allow > 500 tracks in new playlists
     * (2) We Cannot delete tracks more than in playlist.
     * @param currentTracksCount
     * @param tracksCount
     */
    public void validateNewTrackListLimit(int currentTracksCount, int tracksCount,
                                          Action action) {

        if (Action.ADD.name().equals(action.name())) {
            if (currentTracksCount + tracksCount > 500) {
                throw new PlaylistException("Playlist cannot have more than " + 500 + " tracks");
            }
        } else if(Action.REMOVE.name().equalsIgnoreCase(action.name()) ||
                Action.DELETE.name().equalsIgnoreCase(action.name()) ||
                Action.ERASE.name().equalsIgnoreCase(action.name())){
            if (currentTracksCount - tracksCount < 0) {
                throw new PlaylistException("Playlist has less tracks count than to be deleted");
            }
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
    public boolean validateIndexes(int toIndex, int tracksCountNow) {
        return toIndex >= 0 && toIndex <= tracksCountNow;
    }

}
