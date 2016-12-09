package com.yalin.freevideo.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yalin.freevideo.archframework.PresenterImpl;
import com.yalin.freevideo.archframework.UpdatableView;
import com.yalin.freevideo.navigation.NavigationModel.NavigationItemEnum;
import com.yalin.freevideo.navigation.NavigationModel.NavigationQueryEnum;
import com.yalin.freevideo.navigation.NavigationModel.NavigationUserActionEnum;
import com.yalin.freevideo.util.ActivityUtils;

/**
 * YaLin
 * 2016/12/9.
 */

public abstract class AppNavigationViewAbstractImpl implements
        UpdatableView<NavigationModel, NavigationQueryEnum, NavigationUserActionEnum>,
        AppNavigationView {

    private UserActionListener mUserActionListener;

    protected Activity mActivity;

    protected NavigationItemEnum mSelfItem;

    @Override
    public void displayData(NavigationModel model, NavigationQueryEnum query) {
        switch (query) {
            case LOAD_ITEMS:
                displayNavigationItems(model.getItems());
                break;
        }
    }

    @Override
    public void displayErrorMessage(NavigationQueryEnum query) {
        switch (query) {
            case LOAD_ITEMS:
                // No error message displayed
                break;
        }
    }

    @Override
    public void activityReady(Activity activity, NavigationItemEnum self) {
        mActivity = activity;
        mSelfItem = self;

        setUpView();

        NavigationModel model = new NavigationModel(getContext());
        PresenterImpl presenter = new PresenterImpl(model, this,
                NavigationUserActionEnum.values(), NavigationQueryEnum.values());
        presenter.loadInitialQueries();
        addListener(presenter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateNavigationItems() {
        mUserActionListener.onUserAction(NavigationUserActionEnum.RELOAD_ITEMS, null);
    }

    @Override
    public abstract void displayNavigationItems(NavigationItemEnum[] items);

    @Override
    public abstract void setUpView();

    @Override
    public abstract void showNavigation();

    @Override
    public void itemSelected(NavigationItemEnum item) {
        switch (item) {
            default:
                if (item.getClassToLaunch() != null) {
                    ActivityUtils.createBackStack(mActivity,
                            new Intent(mActivity, item.getClassToLaunch()));
                    if (item.finishCurrentActivity()) {
                        mActivity.finish();
                    }
                }
        }
    }

    @Override
    public void displayUserActionResult(NavigationModel model,
                                        NavigationUserActionEnum userAction, boolean success) {
        switch (userAction) {
            case RELOAD_ITEMS:
                displayNavigationItems(model.getItems());
                break;
        }
    }

    @Override
    public Uri getDataUri(NavigationQueryEnum query) {
        return null;
    }

    @Override
    public Context getContext() {
        return mActivity;
    }

    @Override
    public void addListener(UserActionListener listener) {
        mUserActionListener = listener;
    }
}
