package com.scott.swipe;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * These indicate the type of {@link SwipeLayout}'s child view
 */

@IntDef({ItemType.NONE_ITEM, ItemType.SWIPE_VIEW, ItemType.START_MENU, ItemType.END_MENU})
@Retention(RetentionPolicy.SOURCE)
public @interface ItemType {

    /**
     * The flag indicate none type of  {@link SwipeLayout}'s child
     */
    int NONE_ITEM = -1;

    /**
     * The flag indicate swipe view of {@link SwipeLayout}'s child, the view drag while {@link SwipeLayout} receive touch event.
     */
    int SWIPE_VIEW = 0;

    /**
     * The flag indicate start menu of {@link SwipeLayout}'s child, the item will show while the start menu opened.
     */
    int START_MENU = 1;


    /**
     * The flag indicate end menu of {@link SwipeLayout}'s child, the item will show while the start menu opened.
     */
    int END_MENU = 2;
}
