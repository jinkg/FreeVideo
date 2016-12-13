package com.yalin.freevideo.explore;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.yalin.freevideo.R;
import com.yalin.freevideo.explore.data.MovieData;
import com.yalin.freevideo.navigation.NavigationModel;
import com.yalin.freevideo.player.VideoPlayerFragment;
import com.yalin.freevideo.ui.BaseActivity;

public class VideoExploreActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {

    private VideoPlayerFragment mVideoPlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_explore);

        mVideoPlayerFragment = (VideoPlayerFragment) getFragmentManager()
                .findFragmentById(R.id.explore_video_player);
        getFragmentManager().beginTransaction()
                .hide(mVideoPlayerFragment)
                .commit();
    }

    @Override
    protected NavigationModel.NavigationItemEnum getSelfNavDrawerItem() {
        return NavigationModel.NavigationItemEnum.EXPLORE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        Toolbar toolbar = getToolbar();
        toolbar.inflateMenu(R.menu.video_explore_menu);
        toolbar.setOnMenuItemClickListener(this);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                break;
            case R.id.menu_refresh:
                break;
        }
        return false;
    }

    public void playVideo(MovieData movieData) {
        mVideoPlayerFragment.playVideo(movieData);
    }

    @Override
    public void onBackPressed() {
        if (!mVideoPlayerFragment.handleBackPress()) {
            super.onBackPressed();
        }
    }
}
