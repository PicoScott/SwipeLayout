package com.scott.swipe;


import android.view.View;

/**
 * An interface that has the common behavior as the flex container such as {@link SwipeLayout}
 */
public interface SwipeContainer {


    /**
     * Sets the given swipe orientation to the swipe container
     *
     * @param orientation the swipe orientation
     * @see SwipeOrientation
     */
    void setSwipeOrientation(@SwipeOrientation int orientation);

    /**
     * @return the swipe orientation of the swipe container
     * @see SwipeOrientation
     */
    @SwipeOrientation
    int getSwipeOrientation();


    /**
     * Adds the view to the swipe container.
     *
     * @param view the view to be added
     */
    void addView(View view);

    /**
     * Adds the view to the specified index of the swipe container.
     *
     * @param view  the view to be added
     * @param index the index for the view to be added
     */
    void addView(View view, int index);


    /**
     * Removes all the views contained in the swipe container.
     */
    void removeAllViews();

    /**
     * Removes the view at the specified index.
     *
     * @param index the index from which the view is removed.
     */
    void removeViewAt(int index);


    /**
     * @return the top padding of the swipe container.
     */
    int getPaddingTop();

    /**
     * @return the left padding of the swipe container.
     */
    int getPaddingLeft();

    /**
     * @return the right padding of the swipe container.
     */
    int getPaddingRight();

    /**
     * @return the bottom padding of the swipe container.
     */
    int getPaddingBottom();

    /**
     * @return the start padding of this view depending on its resolved layout direction.
     */
    int getPaddingStart();

    /**
     * @return the end padding of this view depending on its resolved layout direction.
     */
    int getPaddingEnd();

    /**
     * @return The child view that item type is {@link ItemType#SWIPE_VIEW}
     */
    View getSwipeView();

    /**
     * @return The child view that item type is {@link ItemType#START_MENU}
     */
    View getStartMenu();

    /**
     * @return The child view that item type is {@link ItemType#END_MENU}
     */
    View getEndMenu();

    EndSwipeHandler getEndSwipeHandler();

    void setEndSwipeHandler(EndSwipeHandler endSwipeHandler);
}
