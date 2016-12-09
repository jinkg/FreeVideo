package com.yalin.freevideo.explore.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yalin.freevideo.player.PlayerActivity;

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

    public Intent buildIntent(Context context) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.setData(Uri.parse(mVideoUri))
                .setAction(PlayerActivity.ACTION_VIEW);
        return intent;
    }
}
