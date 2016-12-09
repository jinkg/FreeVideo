package com.yalin.freevideo.archframework;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * YaLin
 * 2016/12/9.
 */

public interface UpdatableView<M, Q extends QueryEnum, UA extends UserActionEnum> {
    void displayData(M model, Q query);

    void displayErrorMessage(Q query);

    void displayUserActionResult(M model, UA userAction, boolean success);

    Uri getDataUri(Q query);

    Context getContext();

    void addListener(UserActionListener listener);

    interface UserActionListener<UA extends UserActionEnum> {
        void onUserAction(UA action, @Nullable Bundle args);
    }
}
