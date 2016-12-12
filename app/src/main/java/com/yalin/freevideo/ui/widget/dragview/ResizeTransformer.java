package com.yalin.freevideo.ui.widget.dragview;

import android.view.View;
import android.widget.RelativeLayout;

/**
 * YaLin
 * 2016/12/12.
 * <p>
 * Transformer extension created to resize the view instead of scale it as the other
 * implementation does. This implementation is based on change the LayoutParams.
 */
public class ResizeTransformer extends Transformer {

    private final RelativeLayout.LayoutParams layoutParams;

    ResizeTransformer(View view, View parent) {
        super(view, parent);
        layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
    }

    /**
     * Changes view scale using view's LayoutParam.
     *
     * @param verticalDragOffset used to calculate the new size.
     */
    @Override
    public void updateScale(float verticalDragOffset) {
        layoutParams.width = (int) (getOriginalWidth() * (1 - verticalDragOffset / getXScaleFactor()));
        layoutParams.height = (int) (getOriginalHeight() * (1 - verticalDragOffset / getYScaleFactor()));

        getView().setLayoutParams(layoutParams);
    }


    /**
     * Changes X view position using layout() method.
     *
     * @param verticalDragOffset used to calculate the new X position.
     */
    @Override
    public void updatePosition(float verticalDragOffset) {
        int right = getViewRightPosition(verticalDragOffset);
        int left = right - layoutParams.width;
        int top = getView().getTop();
        int bottom = top + layoutParams.height;

        getView().layout(left, top, right, bottom);
    }


    /**
     * @return true if the right position of the view plus the right margin is equals to the parent
     * width.
     */
    @Override
    public boolean isViewAtRight() {
        return getView().getRight() + getMarginRight() == getParentView().getWidth();
    }

    /**
     * @return true if the bottom position of the view plus the margin right is equals to
     * the parent view height.
     */
    @Override
    public boolean isViewAtBottom() {
        return getView().getBottom() + getMarginBottom() == getParentView().getHeight();
    }

    /**
     * @return true if the left position of the view is to the right of the seventy five percent of
     * the parent view width.
     */
    @Override
    public boolean isNextToRightBound() {
        return (getView().getLeft() - getMarginRight()) > getParentView().getWidth() * 0.75;
    }

    /**
     * @return true if the left position of the view is to the left of the twenty five percent of
     * the parent width.
     */
    @Override
    public boolean isNextToLeftBound() {
        return (getView().getLeft() - getMarginRight()) < getParentView().getWidth() * 0.05;
    }

    /**
     * Uses the Y scale factor to calculate the min possible height.
     */
    @Override
    public int getMinHeightPlusMargin() {
        return (int) (getOriginalHeight() * (1 - 1 / getYScaleFactor()) + getMarginBottom());
    }

    /**
     * Uses the X scale factor to calculate the min possible width.
     */
    @Override
    public int getMinWidthPlusMarginRight() {
        return (int) (getOriginalWidth() * (1 - 1 / getXScaleFactor()) + getMarginRight());
    }

    /**
     * Calculate the current view right position for a given verticalDragOffset.
     *
     * @param verticalDragOffset used to calculate the new right position.
     */

    private int getViewRightPosition(float verticalDragOffset) {
        return (int) ((getOriginalWidth()) - getMarginRight() * verticalDragOffset);
    }
}
