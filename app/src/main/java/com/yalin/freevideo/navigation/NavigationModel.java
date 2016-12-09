package com.yalin.freevideo.navigation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yalin.freevideo.BuildConfig;
import com.yalin.freevideo.R;
import com.yalin.freevideo.about.AboutActivity;
import com.yalin.freevideo.archframework.Model;
import com.yalin.freevideo.archframework.QueryEnum;
import com.yalin.freevideo.archframework.UserActionEnum;
import com.yalin.freevideo.debug.DebugActivity;
import com.yalin.freevideo.explore.VideoExploreActivity;
import com.yalin.freevideo.settings.SettingsActivity;

/**
 * YaLin
 * 2016/12/9.
 */

public class NavigationModel implements Model<NavigationModel.NavigationQueryEnum,
        NavigationModel.NavigationUserActionEnum> {

    private Context mContext;

    private NavigationItemEnum[] mItems;

    public NavigationModel(Context context) {
        mContext = context;
    }

    public NavigationItemEnum[] getItems() {
        return mItems;
    }

    @Override
    public NavigationQueryEnum[] getQueries() {
        return NavigationQueryEnum.values();
    }

    @Override
    public NavigationUserActionEnum[] getUserActions() {
        return NavigationUserActionEnum.values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deliverUserAction(NavigationUserActionEnum action,
                                  @Nullable Bundle args, UserActionCallback callback) {
        switch (action) {
            case RELOAD_ITEMS:
                mItems = null;
                populateNavigationItems();
                callback.onModelUpdated(this, action);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void requestData(NavigationQueryEnum query, DataQueryCallback callback) {
        switch (query) {
            case LOAD_ITEMS:
                if (mItems != null) {
                    callback.onModelUpdated(this, query);
                } else {
                    populateNavigationItems();
                    callback.onModelUpdated(this, query);
                }
        }
    }

    private void populateNavigationItems() {
        boolean debug = BuildConfig.DEBUG;

        NavigationItemEnum[] items = NavigationConfig.COMMON_ITEMS_AFTER_CUSTOM;
        if (debug) {
            items = NavigationConfig.appendItem(items, NavigationItemEnum.DEBUG);
        }

        mItems = items;
    }

    @Override
    public void cleanUp() {
        mContext = null;
    }

    public enum NavigationItemEnum {
        EXPLORE(R.id.explore_nav_item, R.string.navdrawer_item_explore,
                R.drawable.ic_navview_explore, VideoExploreActivity.class, true),
        SETTINGS(R.id.settings_nav_item, R.string.navdrawer_item_settings, R.drawable.ic_navview_settings,
                SettingsActivity.class),
        ABOUT(R.id.about_nav_item, R.string.navdrawer_item_about, R.drawable.ic_about,
                AboutActivity.class),
        DEBUG(R.id.debug_nav_item, R.string.navdrawer_item_debug, R.drawable.ic_navview_settings,
                DebugActivity.class),

        INVALID(12, 0, 0, null);

        private int id;
        private int titleResource;
        private int iconResource;
        private Class classToLaunch;
        private boolean finishCurrentActivity;

        NavigationItemEnum(int id, int titleResource, int iconResource, Class classToLaunch) {
            this(id, titleResource, iconResource, classToLaunch, false);
        }

        NavigationItemEnum(int id, int titleResource, int iconResource, Class classToLaunch,
                           boolean finishCurrentActivity) {
            this.id = id;
            this.titleResource = titleResource;
            this.iconResource = iconResource;
            this.classToLaunch = classToLaunch;
            this.finishCurrentActivity = finishCurrentActivity;
        }

        public int getId() {
            return id;
        }

        public int getTitleResource() {
            return titleResource;
        }

        public int getIconResource() {
            return iconResource;
        }

        public Class getClassToLaunch() {
            return classToLaunch;
        }

        public boolean finishCurrentActivity() {
            return finishCurrentActivity;
        }

        public static NavigationItemEnum getById(int id) {
            NavigationItemEnum[] values = NavigationItemEnum.values();
            for (NavigationItemEnum value : values) {
                if (value.getId() == id) {
                    return value;
                }
            }
            return INVALID;
        }
    }

    public enum NavigationQueryEnum implements QueryEnum {
        LOAD_ITEMS(0);

        private int id;

        NavigationQueryEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public String[] getProjection() {
            return new String[0];
        }
    }

    public enum NavigationUserActionEnum implements UserActionEnum {
        RELOAD_ITEMS(0);

        private int id;

        NavigationUserActionEnum(int id) {
            this.id = id;
        }

        @Override
        public int getId() {
            return id;
        }
    }
}
