package com.scott.swipe;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Mu Li
 * Date: 2017/11/9
 * Version: 1.0
 * Description:
 */

public class SwipeLayout extends FrameLayout implements SwipeContainer, ViewOffsetHelper.OnViewOffsetListener {

    public static final String TAG = "SwipeLayout";

    static final String WIDGET_PACKAGE_NAME;

    static {
        final Package pkg = SwipeLayout.class.getPackage();
        WIDGET_PACKAGE_NAME = pkg != null ? pkg.getName() : null;
    }

    static final Class<?>[] CONSTRUCTOR_PARAMS = new Class<?>[]{
            Context.class,
            AttributeSet.class
    };

    static final ThreadLocal<Map<String, Constructor<EndSwipeHandler>>> sConstructors =
            new ThreadLocal<>();

    public static final int ITEM_TYPE_DEFAULT = ItemType.NONE_ITEM;

    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    public static final int INVALID_POINTER = -1;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    public int mActivePointerId = INVALID_POINTER;

    /**
     * The current value of the {@link SwipeOrientation}, the default value is {@link SwipeOrientation#HORIZONTAL}
     *
     * @see SwipeOrientation
     */
    private int mSwipeOrientation;

    private ViewOffsetHelper mViewOffsetHelper;

    /**
     * The start view that set {@link ItemType#SWIPE_VIEW}. when swipe the layout, the view is following the finger.
     */
    private View mSwipeItem;

    /**
     * The start menu that set {@link ItemType#START_MENU}. when swipe to end, the start view is showing.
     */
    private View mStartMenu;

    /**
     * The end menu that set {@link ItemType#END_MENU}. when swipe to start, the end view is showing.
     */
    private View mEndMenu;

    /**
     * The flag of swipe main item
     */
    private boolean mIsBeingDragged;

    /**
     * The x coordinate of last effect touch point
     */
    private int mLastMotionX;
    /**
     * The y coordinate of last effect touch point
     */
    private int mLastMotionY;

    private int mTouchSlop;

    private OnSwipeStatusChangeListener mStatusChangeListener;
    private OnSwipeRangeChangeListener mRangeChangeListener;
    private EndSwipeHandler mEndSwipeHandler;

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
        mSwipeOrientation = typedArray.getInt(R.styleable.SwipeLayout_swipeOrientation, SwipeOrientation.HORIZONTAL);
        String name = typedArray.getString(R.styleable.SwipeLayout_swipeHandler);
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.swipeSnapHandler);
        }
        mEndSwipeHandler = parseBehavior(context, attrs, name);
        typedArray.recycle();

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    static EndSwipeHandler parseBehavior(Context context, AttributeSet attrs, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        final String fullName;
        if (name.startsWith(".")) {
            // Relative to the app package. Prepend the app package name.
            fullName = context.getPackageName() + name;
        } else if (name.indexOf('.') >= 0) {
            // Fully qualified package name.
            fullName = name;
        } else {
            // Assume stock behavior in this package (if we have one)
            fullName = !TextUtils.isEmpty(WIDGET_PACKAGE_NAME)
                    ? (WIDGET_PACKAGE_NAME + '.' + name)
                    : name;
        }

        try {
            Map<String, Constructor<EndSwipeHandler>> constructors = sConstructors.get();
            if (constructors == null) {
                constructors = new HashMap<>();
                sConstructors.set(constructors);
            }
            Constructor<EndSwipeHandler> c = constructors.get(fullName);
            if (c == null) {
                final Class<EndSwipeHandler> clazz = (Class<EndSwipeHandler>) Class.forName(fullName, true, context.getClassLoader());
                c = clazz.getConstructor(CONSTRUCTOR_PARAMS);
                c.setAccessible(true);
                constructors.put(fullName, c);
            }
            return c.newInstance(context, attrs);
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate Handler subclass " + fullName, e);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        /*
         * The layout holds these references to quick access.
         */
        mSwipeItem = getChildByItemType(ItemType.SWIPE_VIEW);
        mViewOffsetHelper = new ViewOffsetHelper(mSwipeItem);
        mViewOffsetHelper.setOnViewOffsetListener(this);
        mStartMenu = getChildByItemType(ItemType.START_MENU);
        mEndMenu = getChildByItemType(ItemType.END_MENU);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //fix MeasureSpec in recycler view.
        super.onMeasure(getChildMeasureSpec(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), 0, getLayoutParams().width), heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        /*
        * Shortcut the most recurring case: the user is in the dragging
        * state and he is moving his finger.  We want to intercept this
        * motion.
        */
        if ((action == MotionEvent.ACTION_MOVE) && (mIsBeingDragged)) {
            return true;
        }

        if (super.onInterceptTouchEvent(ev)) {
            return true;
        }
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = (int) ev.getX();
                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:

                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = ev.findPointerIndex(activePointerId);
                if (pointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + activePointerId
                            + " in onInterceptTouchEvent");
                    break;
                }

                final int distance = getDistanceBySwipeOrientation(ev);
                if (distance > mTouchSlop) {
                    mIsBeingDragged = true;
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                if (getChildCount() == 0) {
                    return false;
                }
                if ((mIsBeingDragged)) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (!mEndSwipeHandler.isFinish()) {
                    mEndSwipeHandler.finish();
                }
                // Remember where the motion event started
                mActivePointerId = event.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int activePointerIndex = event.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }

                final int y = (int) event.getY(activePointerIndex);
                final int x = (int) event.getX(activePointerIndex);
                int deltaX = x - mLastMotionX;
                int deltaY = y - mLastMotionY;

                if (!mIsBeingDragged && getDistanceBySwipeOrientation(event) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaY > 0) {
                        deltaX -= mTouchSlop;
                        deltaY -= mTouchSlop;
                    } else {
                        deltaX += mTouchSlop;
                        deltaY += mTouchSlop;
                    }
                }
                if (mIsBeingDragged) {
                    offsetSwipeItem(deltaX, deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                mEndSwipeHandler.onEndSwipe(this, mViewOffsetHelper);
                break;
        }
        /*
         * Record the last index to calculate dx, dy.
         */
        if (mIsBeingDragged) {
            mLastMotionX = (int) event.getX();
            mLastMotionY = (int) event.getY();
        }
        return true;
    }

    private int getDistanceBySwipeOrientation(MotionEvent event) {
        int distance = 0;
        switch (mSwipeOrientation) {
            case SwipeOrientation.HORIZONTAL:
                distance = (int) Math.abs(event.getX() - mLastMotionX);
                break;
            case SwipeOrientation.VERTICAL:
                distance = (int) Math.abs(event.getY() - mLastMotionY);
                break;
        }
        return distance;
    }

    private void offsetSwipeItem(int deltaX, int deltaY) {
        switch (mSwipeOrientation) {
            case SwipeOrientation.HORIZONTAL:
                offsetSwipeItemHorizontal(deltaX);
                break;
            case SwipeOrientation.VERTICAL:
                offsetSwipeItemVertical(deltaY);
                break;
        }
    }

    private void offsetSwipeItemHorizontal(int deltaX) {
        if (getSwipeView() != null) {
            int swipeViewOffset = mViewOffsetHelper.getLeftAndRightOffset();
            int targetOffset = swipeViewOffset + deltaX;
            if (targetOffset > 0) {
                if (getStartMenu() != null && ((LayoutParams) getStartMenu().getLayoutParams()).mMenuEnable) {
                    if (targetOffset < getStartMenuLength()) {

                    } else {
                        //StartMenu end effect
                        deltaX = 0;
                    }
                } else {
                    //swipe view start edge effect
                    deltaX = 0;
                }
            } else if (targetOffset < 0) {
                if (getEndMenu() != null && ((LayoutParams) getEndMenu().getLayoutParams()).isMenuEnable()) {
                    if (-targetOffset < getEndMenuLength()) {

                    } else {
                        //end menu start edge effect
                        deltaX = 0;
                    }
                } else {
                    //swipe view end edge effect
                    deltaX = 0;
                }
            }
            mViewOffsetHelper.offsetLeftAndRight(deltaX);
        }
    }

    private void offsetSwipeItemVertical(int deltaY) {
        if (getSwipeView() != null) {
            int swipeViewOffset = mViewOffsetHelper.getTopAndBottomOffset();
            int targetOffset = swipeViewOffset + deltaY;
            if (targetOffset > 0) {
                if (getStartMenu() != null && ((LayoutParams) getStartMenu().getLayoutParams()).mMenuEnable) {
                    if (targetOffset < getStartMenuLength()) {

                    } else {
                        //StartMenu end effect
                        deltaY = 0;
                    }
                } else {
                    //swipe view start edge effect
                    deltaY = 0;
                }
            } else if (targetOffset < 0) {
                if (getEndMenu() != null && ((LayoutParams) getEndMenu().getLayoutParams()).isMenuEnable()) {
                    if (-targetOffset < getEndMenuLength()) {

                    } else {
                        //end menu start edge effect
                        deltaY = 0;
                    }
                } else {
                    //swipe view end edge effect
                    deltaY = 0;
                }
            }
            mViewOffsetHelper.offsetTopAndBottom(deltaY);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }


    @Override
    public void setSwipeOrientation(@SwipeOrientation int orientation) {
        mSwipeOrientation = orientation;
    }

    @SwipeOrientation
    @Override
    public int getSwipeOrientation() {
        return mSwipeOrientation;
    }

    /**
     * @return The end item that flag set {@link ItemType#SWIPE_VIEW}
     * @see ItemType
     */
    @Nullable
    @Override
    public View getSwipeView() {
        return mSwipeItem;
    }

    /**
     * @return The end item that flag set {@link ItemType#START_MENU}
     * @see ItemType
     */
    @Nullable
    @Override
    public View getStartMenu() {
        return mStartMenu;
    }

    /**
     * @return The end item that flag set {@link ItemType#END_MENU}
     * @see ItemType
     */
    @Nullable
    @Override
    public View getEndMenu() {
        return mEndMenu;
    }

    private View getChildByItemType(@ItemType int itemType) {
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            if (layoutParams instanceof LayoutParams) {
                if (itemType == ((LayoutParams) layoutParams).getItemType()) {
                    return child;
                }
            }
        }
        return null;
    }

    @Override
    public EndSwipeHandler getEndSwipeHandler() {
        return mEndSwipeHandler;
    }

    @Override
    public void setEndSwipeHandler(EndSwipeHandler endSwipeHandler) {
        mEndSwipeHandler = endSwipeHandler;
    }

    public boolean isStartMenuOpen() {
        if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
            return mViewOffsetHelper.getLeftAndRightOffset() == getStartMenuLength();
        } else {
            return mViewOffsetHelper.getTopAndBottomOffset() == getStartMenuLength();
        }
    }

    public boolean isStartMenuClose() {
        return isMenuClosed();
    }

    private boolean isMenuClosed() {
        if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
            return mViewOffsetHelper.getLeftAndRightOffset() == 0;
        } else {
            return mViewOffsetHelper.getTopAndBottomOffset() == 0;
        }
    }

    public boolean isEndMenuOpen() {
        if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
            return mViewOffsetHelper.getLeftAndRightOffset() == getEndMenuLength();
        } else {
            return mViewOffsetHelper.getTopAndBottomOffset() == getEndMenuLength();
        }
    }

    public boolean isEndMenuClose() {
        return isMenuClosed();
    }

    /**
     * open start menu
     *
     * @param animate {@code true} open start menu smoothly, otherwise immediately.
     */
    public void openStartMenu(boolean animate) {
        if (isEndMenuOpen()) {
            return;
        }
        if (mViewOffsetHelper != null) {
            if (!mViewOffsetHelper.isFinish()) {
                mViewOffsetHelper.finish();
            }
            if (animate) {
                if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
                    mViewOffsetHelper.startScroll(mSwipeItem.getLeft(), mSwipeItem.getTop(), getStartMenuLength(), 0);
                } else {
                    mViewOffsetHelper.startScroll(mSwipeItem.getLeft(), mSwipeItem.getTop(), 0, getStartMenuLength());
                }
            } else {
                if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
                    mViewOffsetHelper.offsetLeftAndRight(getStartMenuLength());
                } else {
                    mViewOffsetHelper.offsetTopAndBottom(getStartMenuLength());
                }
            }
        }
    }

    /**
     * close start menu
     *
     * @param animate {@code true} close start menu smoothly, otherwise immediately.
     */
    public void closeStartView(boolean animate) {
        closeMenu(animate);
    }

    private void closeMenu(boolean animate) {
        if (isEndMenuClose()) {
            return;
        }
        if (mViewOffsetHelper != null) {
            if (!mViewOffsetHelper.isFinish()) {
                mViewOffsetHelper.finish();
            }
            if (animate) {
                mViewOffsetHelper.startScroll(mSwipeItem.getLeft(), mSwipeItem.getTop(), -mViewOffsetHelper.getLeftAndRightOffset(), -mViewOffsetHelper.getTopAndBottomOffset());
            } else {
                mViewOffsetHelper.offsetByLeft(mViewOffsetHelper.getLayoutLeft());
                mViewOffsetHelper.offsetByTop(mViewOffsetHelper.getLayoutTop());
            }
        }
    }

    /**
     * open end menu
     *
     * @param animate {@code true} open start menu smoothly, otherwise open immediately.
     */
    public void openEndMenu(boolean animate) {
        if (isEndMenuOpen()) {
            return;
        }
        if (mViewOffsetHelper != null) {
            if (!mViewOffsetHelper.isFinish()) {
                mViewOffsetHelper.finish();
            }
            if (animate) {
                if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
                    mViewOffsetHelper.startScroll(mSwipeItem.getLeft(), mSwipeItem.getTop(), -getEndMenuLength(), 0);
                } else {
                    mViewOffsetHelper.startScroll(mSwipeItem.getLeft(), mSwipeItem.getTop(), 0, -getEndMenuLength());
                }
            } else {
                if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
                    mViewOffsetHelper.offsetByLeft(-getEndMenuLength());
                } else {
                    mViewOffsetHelper.offsetByTop(-getEndMenuLength());
                }
            }
        }
    }

    /**
     * close end menu
     *
     * @param animate {@code true} close start menu smoothly, otherwise close immediately.
     */
    public void closeEndView(boolean animate) {
        closeMenu(animate);
    }

    /**
     * Register a callback to be invoked when swipe view is swiped.
     *
     * @param statusChangeListener The callback that will run
     */
    public void setStatusChangeListener(OnSwipeStatusChangeListener statusChangeListener) {
        mStatusChangeListener = statusChangeListener;
    }

    /**
     * Register a callback to be invoked when swipe view status changed
     *
     * @param rangeChangeListener The callback that will run
     */
    public void setRangeChangeListener(OnSwipeRangeChangeListener rangeChangeListener) {
        mRangeChangeListener = rangeChangeListener;
    }

    @Override
    public void onViewOffset(ViewOffsetHelper viewOffsetHelper, int offset) {
        notifyRangeChanged(viewOffsetHelper);
        notifyStatusChanged(viewOffsetHelper, offset);
    }

    private void notifyStatusChanged(ViewOffsetHelper viewOffsetHelper, int offset) {
        if (mStatusChangeListener == null) {
            return;
        }

        final int leftAndRightOffset = viewOffsetHelper.getLeftAndRightOffset();
        final int topAndBottomOffset = viewOffsetHelper.getTopAndBottomOffset();

        if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
            if (offset > 0) {// touch from left to right
                if (viewOffsetHelper.getView().getLeft() == viewOffsetHelper.getLayoutLeft()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Close end");
                    }
                    mStatusChangeListener.onCloseEnd();
                }
                if (leftAndRightOffset == getStartMenuLength()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Open start");
                    }
                    mStatusChangeListener.onOpenStart();
                }
            } else {// touch from right to left
                if (viewOffsetHelper.getView().getLeft() == viewOffsetHelper.getLayoutLeft()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Close start");
                    }
                    mStatusChangeListener.onCloseStart();
                }
                if (-leftAndRightOffset == getEndMenuLength()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Open end");
                    }
                    mStatusChangeListener.onOpenEnd();
                }
            }
        } else {
            if (offset > 0) {//touch from top to bottom
                if (viewOffsetHelper.getView().getTop() == viewOffsetHelper.getLayoutTop()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Close end");
                    }
                    mStatusChangeListener.onCloseEnd();
                }

                if (topAndBottomOffset == getStartMenuLength()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Open start");
                    }
                    mStatusChangeListener.onOpenStart();
                }

            } else {// touch from bottom to top
                if (viewOffsetHelper.getView().getTop() == viewOffsetHelper.getLayoutTop()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Close start");
                    }
                    mStatusChangeListener.onCloseStart();
                }
                if (-topAndBottomOffset == getEndMenuLength()) {
                    if (Config.DEBUG) {
                        Log.d(TAG, "Open end");
                    }
                    mStatusChangeListener.onOpenEnd();
                }
            }
        }
    }

    private void notifyRangeChanged(ViewOffsetHelper viewOffsetHelper) {
        if (mRangeChangeListener == null) {
            return;
        }
        final int leftAndRightOffset = viewOffsetHelper.getLeftAndRightOffset();
        final int topAndBottomOffset = viewOffsetHelper.getTopAndBottomOffset();

        if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
            if (leftAndRightOffset > 0) {
                mRangeChangeListener.onSwipeRangeChanged(leftAndRightOffset * 1.0f / getStartMenuLength());
            } else {
                mRangeChangeListener.onSwipeRangeChanged(leftAndRightOffset * 1.0f / getEndMenuLength());
            }
        } else {
            if (topAndBottomOffset > 0) {
                mRangeChangeListener.onSwipeRangeChanged(topAndBottomOffset * 1.0f / getStartMenuLength());
            } else {
                mRangeChangeListener.onSwipeRangeChanged(topAndBottomOffset * 1.0f / getEndMenuLength());

            }
        }
    }

    /**
     * @return the length of start menu.
     * if swipe orientation is {@link SwipeOrientation#HORIZONTAL}, length is {@link #getStartMenu()#getWidth()},
     * if swipe orientation is {@link SwipeOrientation#VERTICAL}, length is {@link #getStartMenu()#getHeight()}.
     */
    public int getStartMenuLength() {
        return getMenuLength(mStartMenu);
    }

    /**
     * @return the length of end menu.
     * if swipe orientation is {@link SwipeOrientation#HORIZONTAL}, length is {@link #getEndMenu()#getWidth()},
     * if swipe orientation is {@link SwipeOrientation#VERTICAL}, length is {@link #getEndMenu()#getHeight()}.
     */
    public int getEndMenuLength() {
        return getMenuLength(mEndMenu);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mEndSwipeHandler != null) {
            mEndSwipeHandler.release();
            mEndSwipeHandler = null;
        }
    }

    private int getMenuLength(View menu) {
        int length = 0;
        if (mSwipeOrientation == SwipeOrientation.HORIZONTAL) {
            if (menu != null) {
                length = menu.getWidth();
            }
        } else {
            if (menu != null) {
                length = menu.getHeight();
            }
        }
        return length;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        private int mItemType;
        private boolean mMenuEnable;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout_Layout);
            mItemType = typedArray.getInt(R.styleable.SwipeLayout_Layout_layout_itemType, ITEM_TYPE_DEFAULT);
            mMenuEnable = typedArray.getBoolean(R.styleable.SwipeLayout_Layout_layout_menuEnable, true);
            typedArray.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @RequiresApi(value = Build.VERSION_CODES.KITKAT)
        public LayoutParams(LayoutParams source) {
            super(source);

            mItemType = source.mItemType;
        }

        public int getItemType() {
            return mItemType;
        }

        public void setItemType(int itemType) {
            mItemType = itemType;
        }

        public boolean isMenuEnable() {
            return mMenuEnable;
        }

        public void setMenuEnable(boolean menuEnable) {
            mMenuEnable = menuEnable;
        }
    }
}
