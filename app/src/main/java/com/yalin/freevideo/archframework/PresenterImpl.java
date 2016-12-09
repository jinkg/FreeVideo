package com.yalin.freevideo.archframework;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yalin.freevideo.util.LogUtil;

/**
 * YaLin
 * 2016/12/9.
 */

public class PresenterImpl implements Presenter, UpdatableView.UserActionListener {
    private static final String TAG = "PresenterImpl";

    @Nullable
    private UpdatableView[] mUpdatableViews;

    private Model mModel;

    private QueryEnum[] mInitialQueriesToLoad;

    private UserActionEnum[] mValidUserActions;

    /**
     * Use this constructor if this Presenter controls one view only.
     */
    public PresenterImpl(Model model, UpdatableView view, UserActionEnum[] validUserActions,
                         QueryEnum[] initialQueries) {
        this(model, new UpdatableView[]{view}, validUserActions, initialQueries);
    }

    /**
     * Use this constructor if this Presenter controls more than one view.
     */
    public PresenterImpl(Model model, @Nullable UpdatableView[] views, UserActionEnum[] validUserActions,
                         QueryEnum[] initialQueries) {
        mModel = model;
        if (views != null) {
            mUpdatableViews = views;
            for (UpdatableView mUpdatableView : mUpdatableViews) {
                mUpdatableView.addListener(this);
            }
        } else {
            LogUtil.e(TAG, "Creating a PresenterImpl with null View");
        }
        mValidUserActions = validUserActions;
        mInitialQueriesToLoad = initialQueries;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadInitialQueries() {
        // Load data queries if any.
        if (mInitialQueriesToLoad != null && mInitialQueriesToLoad.length > 0) {
            for (QueryEnum aMInitialQueriesToLoad : mInitialQueriesToLoad) {
                mModel.requestData(aMInitialQueriesToLoad, new Model.DataQueryCallback() {
                    @Override
                    public void onModelUpdated(Model model, QueryEnum query) {
                        if (mUpdatableViews != null) {
                            for (UpdatableView mUpdatableView : mUpdatableViews) {
                                mUpdatableView.displayData(model, query);
                            }
                        } else {
                            LogUtil.e(TAG, "loadInitialQueries(), cannot notify a null view!");
                        }
                    }

                    @Override
                    public void onError(QueryEnum query) {
                        if (mUpdatableViews != null) {
                            for (UpdatableView mUpdatableView : mUpdatableViews) {
                                mUpdatableView.displayErrorMessage(query);
                            }
                        } else {
                            LogUtil.e(TAG, "loadInitialQueries(), cannot notify a null view!");
                        }
                    }

                });
            }
        } else {
            // No data query to load, update the view.
            if (mUpdatableViews != null) {
                for (UpdatableView mUpdatableView : mUpdatableViews) {
                    mUpdatableView.displayData(mModel, null);
                }
            } else {
                LogUtil.e(TAG, "loadInitialQueries(), cannot notify a null view!");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onUserAction(UserActionEnum action, @Nullable Bundle args) {
        boolean isValid = false;
        if (mValidUserActions != null && action != null) {
            for (UserActionEnum mValidUserAction : mValidUserActions) {
                if (mValidUserAction.getId() == action.getId()) {
                    isValid = true;
                }
            }
        }
        if (isValid) {
            mModel.deliverUserAction(action, args, new Model.UserActionCallback() {

                @Override
                public void onModelUpdated(Model model, UserActionEnum userAction) {
                    if (mUpdatableViews != null) {
                        for (UpdatableView mUpdatableView : mUpdatableViews) {
                            mUpdatableView.displayUserActionResult(model, userAction, true);
                        }
                    } else {
                        LogUtil.e(TAG, "onUserAction(), cannot notify a null view!");
                    }
                }

                @Override
                public void onError(UserActionEnum userAction) {
                    if (mUpdatableViews != null) {
                        for (UpdatableView mUpdatableView : mUpdatableViews) {
                            mUpdatableView.displayUserActionResult(null, userAction, false);
                        }
                        // User action not understood by model, even though the presenter understands

                        // it.
                        LogUtil.e(TAG, "Model doesn't implement user action " + userAction.getId() +
                                ". Have you forgotten to implement this UserActionEnum in your " +
                                "model," +
                                " or have you called setValidUserActions on your presenter with a " +
                                "UserActionEnum that it shouldn't support?");
                    } else {
                        LogUtil.e(TAG, "onUserAction(), cannot notify a null view!");
                    }
                }
            });
        } else {
            if (mUpdatableViews != null) {
                for (UpdatableView mUpdatableView : mUpdatableViews) {
                    mUpdatableView.displayUserActionResult(null, action, false);
                }
                // User action not understood.
                throw new RuntimeException(
                        "Invalid user action " + (action != null ? action.getId() : null) +
                                ". Have you called setValidUserActions on your presenter, with all " +

                                "the UserActionEnum you want to support?");
            } else {
                LogUtil.e(TAG, "onUserAction(), cannot notify a null view!");
            }
        }
    }
}
