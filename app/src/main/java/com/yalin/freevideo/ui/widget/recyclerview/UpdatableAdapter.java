package com.yalin.freevideo.ui.widget.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * YaLin
 * 2016/12/9.
 */

public abstract class UpdatableAdapter<T, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    public abstract void update(@NonNull T updatedData);
}
