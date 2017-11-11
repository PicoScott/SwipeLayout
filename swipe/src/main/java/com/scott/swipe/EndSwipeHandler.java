package com.scott.swipe;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Interaction handler plugin for {@link SwipeLayout}.
 * <p>A handler implements the interface handle the EndEvent of {@link SwipeLayout}
 *
 * @see SwipeSnapHandler
 */
public abstract class EndSwipeHandler {

    /**
     * Default constructor for instantiating EndSwipeHandler.
     */
    public EndSwipeHandler() {
    }

    /**
     * Default constructor for inflating Behaviors from layout. The Behavior will have
     * the opportunity to parse specially defined layout parameters.
     *
     * @param context The Context the EndSwipeHandler is running in, through which it can
     *                access the resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the EndSwipeHandler
     */
    public EndSwipeHandler(Context context, AttributeSet attrs) {

    }

    /**
     * The method is invoked to that {@link SwipeLayout} received touch events and swiped {@link ItemType#SWIPE_VIEW}.
     *
     * @param swipeLayout      who invoke the method
     * @param viewOffsetHelper The {@link ViewOffsetHelper} contains {@link ItemType#SWIPE_VIEW}
     */
    public abstract void onEndSwipe(SwipeLayout swipeLayout, ViewOffsetHelper viewOffsetHelper);

    /**
     * @return {@code true} the Handler is running, otherwise the handler is not running
     */
    public abstract boolean isFinish();

    /**
     * Finish the end swipe.
     */
    public abstract void finish();


    /**
     * Release holding resources.
     */
    public abstract void release();
}
