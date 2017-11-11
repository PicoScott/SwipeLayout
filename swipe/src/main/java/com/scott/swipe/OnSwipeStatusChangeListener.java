package com.scott.swipe;

/**
 * Interface definition for a callback to be invoked when a SwipeLayout open or close.
 */
public interface OnSwipeStatusChangeListener {

    /**
     * Called when opened the swipe layout start, the open position according to {@link SwipeOrientation}
     */
    void onOpenStart();

    /**
     * Called when opened the swipe layout end, the open position according to {@link SwipeOrientation}
     */
    void onOpenEnd();

    /**
     * Called when closed the swipe layout start, the open position according to {@link SwipeOrientation}
     */
    void onCloseStart();

    /**
     * Called when closed the swipe layout end, the open position according to {@link SwipeOrientation}
     */
    void onCloseEnd();
}
