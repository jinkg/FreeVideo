package com.yalin.freevideo.navigation;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.yalin.freevideo.R;
import com.yalin.freevideo.navigation.NavigationModel.NavigationItemEnum;
import com.yalin.freevideo.util.ImageLoader;
import com.yalin.freevideo.util.LogUtil;
import com.yalin.freevideo.util.SettingsUtils;
import com.yalin.freevideo.util.UIUtils;

/**
 * YaLin
 * 2016/12/9.
 */

public class AppNavigationViewAsDrawerImpl extends AppNavigationViewAbstractImpl
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "AppNavigationViewAsDraw";

    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;

    private DrawerLayout mDrawerLayout;

    private Runnable mDeferredOnDrawerClosedRunnable;

    private NavigationView mNavigationView;

    private Handler mHandler;

    private ImageLoader mImageLoader;

    private NavigationDrawerStateListener mNavigationDrawerStateListener;

    public AppNavigationViewAsDrawerImpl(ImageLoader imageLoader,
                                         NavigationDrawerStateListener navigationDrawerStateListener) {
        mImageLoader = imageLoader;
        mNavigationDrawerStateListener = navigationDrawerStateListener;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        NavigationItemEnum item = NavigationItemEnum.getById(menuItem.getItemId());
        onNavDrawerItemClicked(item);
        return true;
    }

    @Override
    public void displayNavigationItems(NavigationItemEnum[] items) {
        createNavDrawerItems(items);
        setSelectedNavDrawerItem(mSelfItem);
    }

    @Override
    public void setUpView() {
        mHandler = new Handler();

        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        // setup the status bar color to be colorPrimaryDark from the theme
        mDrawerLayout.setStatusBarBackgroundColor(
                UIUtils.getThemeColor(mActivity, R.attr.colorPrimaryDark,
                        R.color.theme_primary_dark));

        mNavigationView = (NavigationView) mActivity.findViewById(R.id.nav_view);

        if (mSelfItem == NavigationModel.NavigationItemEnum.INVALID) {
            // do not show a nav drawer
            mDrawerLayout = null;
            return;
        }

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (mDeferredOnDrawerClosedRunnable != null) {
                    mDeferredOnDrawerClosedRunnable.run();
                    mDeferredOnDrawerClosedRunnable = null;
                }
                mNavigationDrawerStateListener.onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mNavigationDrawerStateListener.onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                mNavigationDrawerStateListener.onNavDrawerStateChanged(isNavDrawerOpen(),
                        newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mNavigationDrawerStateListener.onNavDrawerSlide(slideOffset);
            }
        });

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // When the user runs the app for the first time, we want to land them with the
        // navigation drawer open. But just the first time.
        if (!SettingsUtils.isFirstRunProcessComplete(mActivity)) {
            // first run of the app starts with the nav drawer open
            SettingsUtils.markFirstRunProcessesDone(mActivity, true);
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void showNavigation() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void createNavDrawerItems(NavigationItemEnum[] items) {
        if (mNavigationView != null) {
            Menu menu = mNavigationView.getMenu();
            for (NavigationItemEnum item1 : items) {
                MenuItem item = menu.findItem(item1.getId());
                if (item != null) {
                    item.setVisible(true);
                    item.setIcon(item1.getIconResource());
                    item.setTitle(item1.getTitleResource());
                } else {
                    LogUtil.e(TAG, "Menu Item for navigation item with title " +
                            (item1.getTitleResource() != 0 ? mActivity.getResources().getString(
                                    item1.getTitleResource()) : "") + "not found");
                }
            }

            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void setSelectedNavDrawerItem(NavigationItemEnum item) {
        if (mNavigationView != null && item != NavigationItemEnum.INVALID) {
            mNavigationView.getMenu().findItem(item.getId()).setChecked(true);
        }
    }

    public void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void onNavDrawerItemClicked(final NavigationItemEnum item) {
        if (item == mSelfItem) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (isSpecialItem(item)) {
            itemSelected(item);
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    itemSelected(item);
                }
            }, NAVDRAWER_LAUNCH_DELAY);
        }

        setSelectedNavDrawerItem(item);

        View mainContent = mActivity.findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private boolean isSpecialItem(NavigationItemEnum item) {
        return item == NavigationItemEnum.SETTINGS;
    }

    public interface NavigationDrawerStateListener {

        void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating);

        void onNavDrawerSlide(float slideOffset);
    }
}
