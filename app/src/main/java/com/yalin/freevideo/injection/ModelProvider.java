package com.yalin.freevideo.injection;

import android.app.LoaderManager;
import android.content.Context;
import android.net.Uri;

import com.yalin.freevideo.archframework.Model;
import com.yalin.freevideo.explore.VideoExploreModel;

/**
 * YaLin
 * 2016/12/9.
 */

public class ModelProvider {

    private static VideoExploreModel stubVideoExploreModel;

    public static VideoExploreModel provideVideoExploreModel(Uri videoUri, Context context,
                                                             LoaderManager loaderManager) {
        if (stubVideoExploreModel != null) {
            return stubVideoExploreModel;
        }
        return new VideoExploreModel();
    }

    public static void setStubModel(Model model) {
        if (model instanceof VideoExploreModel) {
            stubVideoExploreModel = (VideoExploreModel) model;
        }
    }
}
