package com.scott.swipe;

import android.support.annotation.FloatRange;

/**
 * Interface definition for a callback to be invoked when a SwipeItem swiped.
 */

public interface OnSwipeRangeChangeListener {

    /**
     * Called when SwipeItem swipe.dividend is the swipe menu offset,
     * and divisor is the opened view length(if {@link SwipeOrientation} set {@link SwipeOrientation#HORIZONTAL},
     * length is start or end item's width, otherwise height).
     *
     * @param range Value from [-1, 1] indicating the offset according to the organic position,
     *              [-1, 0] indicating the start menu from open to close.
     *              [0, 1] indicating the end menu from close to open.
     */
    void onSwipeRangeChanged(@FloatRange(from = -1.0f, to = 1.0f) float range);
}