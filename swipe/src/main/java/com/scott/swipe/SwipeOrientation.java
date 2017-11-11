package com.scott.swipe;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * SwipeOrientation control swipe directions.
 */
@IntDef({SwipeOrientation.HORIZONTAL, SwipeOrientation.VERTICAL})
@Retention(RetentionPolicy.SOURCE)
public @interface SwipeOrientation {

    /**
     * The flag indicate that {@link SwipeLayout} can swipe along x axis.
     */
    int HORIZONTAL = 0;

    /**
     * The flag indicate that {@link SwipeLayout} can swipe along y axis
     */
    int VERTICAL = 1;
}
