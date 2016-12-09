package com.yalin.freevideo.ui.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * YaLin
 * 2016/12/9.
 */

public class ItemMarginDecoration extends RecyclerView.ItemDecoration {

    private final int mMarginLeft;
    private final int mMarginTop;
    private final int mMarginRight;
    private final int mMarginBottom;

    public ItemMarginDecoration(int marginLeft, int marginTop, int marginRight, int marginBottom) {
        mMarginLeft = marginLeft;
        mMarginTop = marginTop;
        mMarginRight = marginRight;
        mMarginBottom = marginBottom;
    }

    @Override
    public void getItemOffsets(Rect outRect,
                               View view,
                               RecyclerView parent,
                               RecyclerView.State state) {
        outRect.left = mMarginLeft;
        outRect.top = mMarginTop;
        outRect.right = mMarginRight;
        outRect.bottom = mMarginBottom;
    }

}
