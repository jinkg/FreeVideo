package com.yalin.freevideo.explore;

import com.yalin.freevideo.explore.data.MovieData;

import java.util.Arrays;
import java.util.List;

/**
 * YaLin
 * 2016/12/9.
 */

public class StubVideoExploreModel extends VideoExploreModel {
    private List<MovieData> mMovies;

    @Override
    public void requestData(VideoExploreQueryEnum query, DataQueryCallback callback) {
        mMovies = Arrays.asList(
                new MovieData("萨利机长",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-12-01/13164b5c88cf7c48852f18a8324a4eb1.jpg",
                        5700000L),
                new MovieData("会计刺客",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-12-09/b5fc707407ceead673d63bcd76be4559.jpg",
                        5700000L),
                new MovieData("单身日记",
                        "http://img.diannao1.com/d/file/html/gndy/jddy/2016-12-04/c6ff649a50fff4e8a63be4b183407ab1.jpg",
                        5700000L),
                new MovieData("豪勇七蛟龙",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-12-08/5c564f141255d1d5c21e5c596cee4902.jpg",
                        36000),
                new MovieData("佩小姐的奇幻城堡",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-12-07/57020b3c70656408c030c9e17e4dc72c.jpg",
                        5700000L),
                new MovieData("圆梦巨人",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-11-13/bffa57362a60a4f8aef6da4878966371.jpg",
                        5700000L),
                new MovieData("宾虚",
                        "http://img.23juqing.com/d/file/html/gndy/dyzz/2016-11-29/dd9bc9bb4cacee5480b9757e31de8248.jpg",
                        5700000L),
                new MovieData("但丁密码",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-12-01/8734d0a38e0e1f3d175efeea19a5f771.jpg",
                        5700000L),
                new MovieData("捉迷藏",
                        "http://tu.23juqing.com/d/file/html/gndy/dyzz/2016-12-06/626a993b136a615f9072bdfe17b6ca5f.jpg",
                        5700000L));

        for (MovieData movieData : mMovies) {
            movieData.setVideoUri("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd");
        }
        callback.onModelUpdated(this, query);
    }

    public List<MovieData> getMovies() {
        return mMovies;
    }
}
