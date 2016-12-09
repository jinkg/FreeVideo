package com.yalin.freevideo.explore;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yalin.freevideo.archframework.Model;
import com.yalin.freevideo.archframework.QueryEnum;
import com.yalin.freevideo.archframework.UserActionEnum;
import com.yalin.freevideo.explore.data.MovieData;

import java.util.List;

/**
 * YaLin
 * 2016/12/9.
 */

public class VideoExploreModel implements Model<VideoExploreModel.VideoExploreQueryEnum,
        VideoExploreModel.VideoExploreUserActionEnum> {

    private List<MovieData> mMovies;

    @Override
    public VideoExploreQueryEnum[] getQueries() {
        return VideoExploreQueryEnum.values();
    }

    @Override
    public VideoExploreUserActionEnum[] getUserActions() {
        return VideoExploreUserActionEnum.values();
    }

    @Override
    public void deliverUserAction(VideoExploreUserActionEnum action, @Nullable Bundle args, UserActionCallback callback) {

    }

    @Override
    public void requestData(VideoExploreQueryEnum query, DataQueryCallback callback) {
    }

    @Override
    public void cleanUp() {

    }

    public List<MovieData> getMovies() {
        return mMovies;
    }

    public enum VideoExploreQueryEnum implements QueryEnum {
        VIDEOS(0x1, new String[]{});

        private int id;
        private String[] projection;

        VideoExploreQueryEnum(int id, String[] projection) {
            this.id = id;
            this.projection = projection;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return projection;
        }
    }

    public enum VideoExploreUserActionEnum implements UserActionEnum {
        RE_LOAD(1);

        private int id;

        VideoExploreUserActionEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
