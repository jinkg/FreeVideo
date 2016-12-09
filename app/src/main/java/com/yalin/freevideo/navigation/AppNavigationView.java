package com.yalin.freevideo.navigation;

import android.app.Activity;

/**
 * YaLin
 * 2016/12/9.
 */

public interface AppNavigationView {
    void activityReady(Activity activity, NavigationModel.NavigationItemEnum self);

    void setUpView();

    void updateNavigationItems();

    void displayNavigationItems(NavigationModel.NavigationItemEnum[] items);

    void itemSelected(NavigationModel.NavigationItemEnum item);

    void showNavigation();
}
