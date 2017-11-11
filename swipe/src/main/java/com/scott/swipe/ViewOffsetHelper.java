package com.scott.swipe;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.Scroller;

/**
 * Utility helper for moving a {@link android.view.View} around using
 * {@link android.view.View#offsetLeftAndRight(int)} and
 * {@link android.view.View#offsetTopAndBottom(int)}.
 */
class ViewOffsetHelper {

    /**
     * The default duration of scroll
     */
    private static final int DEFAULT_DURATION = 250;

    private final View mView;

    /**
     * The original top of holding view
     */
    private int mLayoutTop;

    /**
     * The original left of holding view
     */
    private int mLayoutLeft;

    private SwipeRunnable mSwipeRunnable;
    private Scroller mScroller;
    private OnViewOffsetListener mOnViewOffsetListener;

    public ViewOffsetHelper(View view) {
        mView = view;
        resetData();
        mScroller = new Scroller(view.getContext());
        mSwipeRunnable = new SwipeRunnable(this);
    }


    public void resetData() {
        mLayoutTop = mView.getTop();
        mLayoutLeft = mView.getLeft();
    }

    /**
     * Set the top and bottom offset for this {@link ViewOffsetHelper}'s view.
     *
     * @param offset the offset in vertical.
     */
    public void offsetTopAndBottom(int offset) {
        if (offset != 0) {
            ViewCompat.offsetTopAndBottom(mView, offset);
            if (mOnViewOffsetListener != null) {
                mOnViewOffsetListener.onViewOffset(this, offset);
            }
        }
    }

    /**
     * Set the left and right offset for this {@link ViewOffsetHelper}'s view.
     *
     * @param offset the offset in horizontal.
     */
    public void offsetLeftAndRight(final int offset) {
        if (offset != 0) {
            ViewCompat.offsetLeftAndRight(mView, offset);
            if (mOnViewOffsetListener != null) {
                mOnViewOffsetListener.onViewOffset(this, offset);
            }
        }
    }

    /**
     * Offset the view according to the top.
     *
     * @param top the target position of top.
     */
    public void offsetByTop(int top) {
        int offset = top - mView.getTop();
        if (offset != 0) {
            offsetTopAndBottom(offset);
        }
    }

    /**
     * Offset the view according to the left
     *
     * @param left the target position of left
     */
    public void offsetByLeft(int left) {
        int offset = left - mView.getLeft();
        if (offset != 0) {
            offsetLeftAndRight(offset);
        }
    }

    /**
     * @return The total value of vertical offset
     */

    public int getTopAndBottomOffset() {
        return mView.getTop() - mLayoutTop;
    }

    /**
     * @return The total value of horizontal offset
     */
    public int getLeftAndRightOffset() {
        return mView.getLeft() - mLayoutLeft;
    }

    public View getView() {
        return mView;
    }

    /**
     * @return The value of view's original left.
     */
    public int getLayoutTop() {
        return mLayoutTop;
    }

    /**
     * @return The value of view's original left.
     */
    public int getLayoutLeft() {
        return mLayoutLeft;
    }

    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * The scroll will use the default value of 250 milliseconds for the
     * duration.The method invoke {@link Scroller#startScroll(int, int, int, int, int)}
     *
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *               numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *               will scroll the content up.
     * @param dx     Horizontal distance to travel. Positive numbers will scroll the
     *               content to the left.
     * @param dy     Vertical distance to travel. Positive numbers will scroll the
     *               content up.
     */
    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, DEFAULT_DURATION);
    }

    /**
     * Start scrolling by providing a starting point, the distance to travel,
     * and the duration of the scroll.The method invoke {@link Scroller#startScroll(int, int, int, int, int)}
     *
     * @param startX   Starting horizontal scroll offset in pixels. Positive
     *                 numbers will scroll the content to the left.
     * @param startY   Starting vertical scroll offset in pixels. Positive numbers
     *                 will scroll the content up.
     * @param dx       Horizontal distance to travel. Positive numbers will scroll the
     *                 content to the left.
     * @param dy       Vertical distance to travel. Positive numbers will scroll the
     *                 content up.
     * @param duration Duration of the scroll in milliseconds.
     */
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        mScroller.startScroll(startX, startY, dx, dy, duration);
        if (mScroller.computeScrollOffset()) {
            ViewCompat.postOnAnimation(mView, mSwipeRunnable);
        } else {
            onSwipeFinished();
        }
    }

    /**
     * Returns whether the viewOffsetHelper has finished scrolling.
     *
     * @return True if the viewOffsetHelper has finished scrolling, false otherwise.
     */
    public boolean isFinish() {
        return mScroller != null && mScroller.isFinished();
    }

    /**
     * Finish the scrolling
     */
    public void finish() {
        if (mScroller != null) {
            mScroller.forceFinished(true);
        }
    }

    public void setOnViewOffsetListener(OnViewOffsetListener onViewOffsetListener) {
        mOnViewOffsetListener = onViewOffsetListener;
    }

    private void onSwipeFinished() {
    }

    private class SwipeRunnable implements Runnable {
        private ViewOffsetHelper mViewOffsetHelper;

        SwipeRunnable(ViewOffsetHelper viewOffsetHelper) {
            mViewOffsetHelper = viewOffsetHelper;
        }

        @Override
        public void run() {
            if (mViewOffsetHelper != null && mViewOffsetHelper.getView() != null && mScroller != null) {
                if (mScroller.computeScrollOffset()) {
                    mViewOffsetHelper.offsetByLeft(mScroller.getCurrX());
                    mViewOffsetHelper.offsetByTop(mScroller.getCurrY());
                    // Post ourselves so that we run on the next animation
                    ViewCompat.postOnAnimation(mViewOffsetHelper.getView(), this);
                } else {
                    onSwipeFinished();
                }
            }
        }
    }

    interface OnViewOffsetListener {
        void onViewOffset(ViewOffsetHelper viewOffsetHelper, int offset);
    }
}