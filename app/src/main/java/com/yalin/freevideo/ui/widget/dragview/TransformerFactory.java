package com.yalin.freevideo.ui.widget.dragview;

import android.view.View;

/**
 * YaLin
 * 2016/12/12.
 * <p>
 * Factory created to provide Transformer implementations like ResizeTransformer o
 * ScaleTransformer.
 */
public class TransformerFactory {
    public Transformer getTransformer(final boolean resize, final View view, final View parent) {
        Transformer transformer;
        if (resize) {
            transformer = new ResizeTransformer(view, parent);
        } else {
            transformer = new ScaleTransformer(view, parent);
        }
        return transformer;
    }
}
