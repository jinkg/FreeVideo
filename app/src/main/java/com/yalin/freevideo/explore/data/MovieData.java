package com.yalin.freevideo.explore.data;

import android.net.Uri;

/**
 * YaLin
 * 2016/12/9.
 */

public class MovieData {
    private String mMovieName;
    private String mCoverUrl;
    private long mTimeLong;

    private String mVideoUri;

    public MovieData(String movieName, String coverUrl, long timeLong) {
        this.mMovieName = movieName;
        this.mCoverUrl = coverUrl;
        this.mTimeLong = timeLong;
    }

    public void setVideoUri(String videoUri) {
        this.mVideoUri = videoUri;
    }

    public String getMovieName() {
        return mMovieName;
    }

    public String getCoverUrl() {
        return mCoverUrl;
    }

    public long getTimeLong() {
        return mTimeLong;
    }

    public Uri getVideoUri() {
        return Uri.parse(mVideoUri);
    }
}
