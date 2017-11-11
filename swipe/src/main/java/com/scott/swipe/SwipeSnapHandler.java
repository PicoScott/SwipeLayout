package com.scott.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Author: scott
 * Date: 2017/11/15
 * Version: 1.0
 * Description:
 */

public class SwipeSnapHandler extends EndSwipeHandler {

    @SuppressWarnings("UnusedDeclaration")
    public static final String TAG = "SwipeSnapHandler";
    private static final float STATUS_CHANGE_DEFAULT_FRACTION = 0.5f;

    private ViewOffsetHelper mViewOffsetHelper;
    private int mStatusType;
    private float mStatusValue;

    public SwipeSnapHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
        TypedValue statusTypeValue = typedArray.peekValue(R.styleable.SwipeLayout_swipeStatusPosition);
        if (statusTypeValue != null) {
            mStatusType = statusTypeValue.type;
            if (mStatusType == TypedValue.TYPE_DIMENSION) {
                mStatusValue = typedArray.getDimensionPixelOffset(R.styleable.SwipeLayout_swipeStatusPosition, 0);
            } else if (mStatusType == TypedValue.TYPE_FLOAT) {
                mStatusValue = typedArray.getFloat(R.styleable.SwipeLayout_swipeStatusPosition, 0f);
            }
        }
        typedArray.recycle();
    }

    @Override
    public void onEndSwipe(SwipeLayout swipeLayout, ViewOffsetHelper viewOffsetHelper) {
        mViewOffsetHelper = viewOffsetHelper;
        int scrollDistance;
        switch (swipeLayout.getSwipeOrientation()) {
            case SwipeOrientation.HORIZONTAL:
                scrollDistance = onEndSwipeHorizontal(swipeLayout, viewOffsetHelper);
                viewOffsetHelper.startScroll(viewOffsetHelper.getView().getLeft(), viewOffsetHelper.getView().getTop(), scrollDistance, 0);
                break;
            case SwipeOrientation.VERTICAL:
                scrollDistance = onEndSwipeVertical(swipeLayout, viewOffsetHelper);
                viewOffsetHelper.startScroll(viewOffsetHelper.getView().getLeft(), viewOffsetHelper.getView().getTop(), 0, scrollDistance);
                break;
        }
    }

    private int onEndSwipeHorizontal(SwipeLayout swipeLayout, ViewOffsetHelper viewOffsetHelper) {
        int offset = viewOffsetHelper.getLeftAndRightOffset();
        int distance;
        if (offset > 0) {
            distance = swipeLayout.getStartMenuLength();
        } else {
            distance = swipeLayout.getEndMenuLength();
        }
        return getScrollDistance(swipeLayout.getContext(), offset, distance);
    }

    private int onEndSwipeVertical(SwipeLayout swipeLayout, ViewOffsetHelper viewOffsetHelper) {
        int offset = viewOffsetHelper.getTopAndBottomOffset();
        int distance;
        if (offset > 0) {
            distance = swipeLayout.getStartMenuLength();
        } else {
            distance = swipeLayout.getEndMenuLength();

        }
        return getScrollDistance(swipeLayout.getContext(), offset, distance);
    }

    /**
     * @param offset menu opened length
     * @param length default value of operated menu length
     * @return swipe view auto scroll length.
     */
    private int getScrollDistance(Context context, int offset, int length) {
        int scrollDistance = -offset;
        if (offset > 0) {
            // the start view opened, if opened length great than status change length,auto open ,otherwise auto close
            int statusChangeDx = getStatusChangeLength(context, length);
            if (offset > statusChangeDx) {
                scrollDistance = length - offset;
            }
        } else {
            //the end view opened, if opened length great than status change length,auto open ,otherwise auto close
            int statusChangeDx = getStatusChangeLength(context, length);

            if (-offset > statusChangeDx) {
                scrollDistance = -length - offset;
            }
        }
        return scrollDistance;
    }

    /**
     * @param context provider DisplayMetrics
     * @param length  the length of opened view along swipe orientation.
     * @return menu status change position
     */
    private int getStatusChangeLength(Context context, int length) {
        int statusChangeLength;
        if (mStatusType == TypedValue.TYPE_FLOAT) {
            statusChangeLength = Math.round(length * mStatusValue);
        } else if (mStatusType == TypedValue.TYPE_DIMENSION) {
            statusChangeLength = Math.round(mStatusValue);
        } else {
            statusChangeLength = Math.round(length * STATUS_CHANGE_DEFAULT_FRACTION);
        }
        return statusChangeLength;
    }

    @Override
    public boolean isFinish() {
        return mViewOffsetHelper == null || mViewOffsetHelper.isFinish();
    }

    @Override
    public void finish() {
        mViewOffsetHelper.finish();
    }

    @Override
    public void release() {
        mViewOffsetHelper = null;
    }
}