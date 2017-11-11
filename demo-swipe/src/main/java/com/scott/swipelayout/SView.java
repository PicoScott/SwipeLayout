package com.scott.swipelayout;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EdgeEffect;

/**
 * Author: Mu Li
 * Date: 2017/11/27
 * Version: 1.0
 * Description:
 */

public class SView extends View {

    private EdgeEffect mEdgeGlowTop;
    private EdgeEffect mEdgeGlowBottom;
    private int mDx;
    private int mDx2;

    public SView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mEdgeGlowTop = new EdgeEffect(context);
        mEdgeGlowBottom = new EdgeEffect(context);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEdgeGlowTop != null) {
            final int scrollY = 0;
            final boolean clipToPadding = false;
            if (!mEdgeGlowTop.isFinished()) {
                final int restoreCount = canvas.save();
                final int width = getWidth();
                final int height = getHeight();
                final float translateX;
                final float translateY;
                translateX = 0;
                translateY = 0;
                canvas.translate(translateX, Math.min(0, scrollY) + translateY);
                mEdgeGlowTop.setSize(width, height);
                if (mEdgeGlowTop.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
            if (!mEdgeGlowBottom.isFinished()) {
                final int restoreCount = canvas.save();
                final int width;
                final int height;
                final float translateX;
                final float translateY;
                width = getWidth();
                height = getHeight();
                translateX = 0;
                translateY = 0;
                canvas.translate(-width + translateX,
                        Math.max(getScrollRange(), scrollY) + height + translateY);
                canvas.rotate(180, width, 0);
                mEdgeGlowBottom.setSize(width, height);
                if (mEdgeGlowBottom.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(restoreCount);
            }
        }
    }

    public int getScrollRange() {
        return getHeight();
    }

    public void pullTop() {
        mDx += 5;
        mEdgeGlowTop.onPull(mDx / getHeight());
        postInvalidate();
    }

    public void pullBottom() {
        mDx2 += 5;
        mEdgeGlowBottom.onPull(mDx2 / getHeight());
        postInvalidate();
    }
}
