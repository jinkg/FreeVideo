package com.yalin.freevideo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.yalin.freevideo.R;
import com.yalin.freevideo.navigation.AppNavigationViewAsDrawerImpl;
import com.yalin.freevideo.navigation.NavigationModel.NavigationItemEnum;
import com.yalin.freevideo.util.ImageLoader;
import com.yalin.freevideo.util.LogUtil;

/**
 * YaLin
 * 2016/12/9.
 */

public abstract class BaseActivity extends AppCompatActivity implements
        AppNavigationViewAsDrawerImpl.NavigationDrawerStateListener {
    private static final String TAG = "BaseActivity";

    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    private AppNavigationViewAsDrawerImpl mAppNavigationViewAsDrawer;

    private Toolbar mToolbar;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
    }

    private void trySetupSwipeRefresh() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.flat_button_text);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestDataRefresh();
                }
            });
        }
    }

    protected NavigationItemEnum getSelfNavDrawerItem() {
        return NavigationItemEnum.INVALID;
    }

    @Override
    public void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        // Nothing to do
    }

    @Override
    public void onNavDrawerSlide(float offset) {
    }

    @Override
    public void onBackPressed() {
        if (mAppNavigationViewAsDrawer.isNavDrawerOpen()) {
            mAppNavigationViewAsDrawer.closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAppNavigationViewAsDrawer = new AppNavigationViewAsDrawerImpl(new ImageLoader(this), this);
        mAppNavigationViewAsDrawer.activityReady(this, getSelfNavDrawerItem());

        if (getSelfNavDrawerItem() != NavigationItemEnum.INVALID) {
            setToolbarForNavigation();
        }

        trySetupSwipeRefresh();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            LogUtil.w(TAG, "No view with ID main_content to fade in.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
        }
        return super.onOptionsItemSelected(item);
    }

    protected void requestDataRefresh() {

    }

    public Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                mToolbar.setNavigationContentDescription(getResources().getString(R.string
                        .navdrawer_description_a11y));
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    private void setToolbarForNavigation() {
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(R.drawable.ic_hamburger);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAppNavigationViewAsDrawer.showNavigation();
                }
            });
        }
    }

    protected void setToolbarAsUp(View.OnClickListener clickListener) {
        // Initialise the toolbar
        getToolbar();

        mToolbar.setNavigationIcon(R.drawable.ic_up);
        mToolbar.setNavigationContentDescription(R.string.close_and_go_back);
        mToolbar.setNavigationOnClickListener(clickListener);
    }
}
