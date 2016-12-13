package com.yalin.freevideo.explore.data;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

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

    public MovieData(String movieName, String coverUrl, long timeLong, String videoUri) {
        this(movieName, coverUrl, timeLong);
        mVideoUri = videoUri;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MovieData) {
            return TextUtils.equals(mVideoUri, ((MovieData) obj).mVideoUri);
        }
        return super.equals(obj);
    }

    public Intent buildPlayIntent(Activity activity) {
        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.setData(Uri.parse(mVideoUri));
        intent.setAction(PlayerActivity.ACTION_VIEW);
        return intent;
    }
}
