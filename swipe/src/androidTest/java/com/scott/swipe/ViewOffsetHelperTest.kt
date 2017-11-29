package com.scott.swipe

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.scott.swipe.test.SwipeLayoutTestActivity
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Author: Mu Li
 * Date: 2017/11/29
 * Version: 1.0
 * Description:
 */
@RunWith(AndroidJUnit4::class)
class ViewOffsetHelperTest {

    @JvmField
    @Rule
    val activityRule = ActivityTestRule(SwipeLayoutTestActivity::class.java)
    private lateinit var viewOffsetHelper: ViewOffsetHelper

    @Before
    fun setUp() {
        val view = View(activityRule.activity)
        val layoutParams = SwipeLayout.LayoutParams(100, 100)
        view.layoutParams = layoutParams
        viewOffsetHelper = ViewOffsetHelper(view)
    }

    @Test
    @Throws(Throwable::class)
    fun testGetLayoutLeftAndRight() {
        viewOffsetHelper.view.offsetLeftAndRight(10)
        assertEquals(0, viewOffsetHelper.layoutLeft)

        val view = View(activityRule.activity)
        val layoutParams = SwipeLayout.LayoutParams(100, 100)
        view.offsetTopAndBottom(100)
        view.offsetLeftAndRight(100)
        view.layoutParams = layoutParams
        viewOffsetHelper = ViewOffsetHelper(view)

        assertEquals(100, viewOffsetHelper.layoutLeft)
    }


    @Test
    @Throws(Throwable::class)
    fun testGetLayoutTopAndBottom() {
        viewOffsetHelper.view.offsetTopAndBottom(10)
        assertEquals(0, viewOffsetHelper.layoutTop)

        val view = View(activityRule.activity)
        val layoutParams = SwipeLayout.LayoutParams(100, 100)
        view.offsetTopAndBottom(100)
        view.offsetLeftAndRight(100)
        view.layoutParams = layoutParams
        viewOffsetHelper = ViewOffsetHelper(view)

        assertEquals(100, viewOffsetHelper.layoutTop)
    }

    @Test
    @Throws(Throwable::class)
    fun testGetLeftAndRightOffset() {
        viewOffsetHelper.view.offsetLeftAndRight(10)
        assertEquals(10, viewOffsetHelper.leftAndRightOffset)
        viewOffsetHelper.view.offsetLeftAndRight(-30)
        assertEquals(-20, viewOffsetHelper.leftAndRightOffset)
    }

    @Test
    @Throws(Throwable::class)
    fun testGetOffsetTopAndBottom() {
        viewOffsetHelper.view.offsetTopAndBottom(10)
        assertEquals(10, viewOffsetHelper.topAndBottomOffset)
        viewOffsetHelper.view.offsetTopAndBottom(-30)
        assertEquals(-20, viewOffsetHelper.topAndBottomOffset)
    }

    @Test
    @Throws(Throwable::class)
    fun testOffsetLeftAndTop() {
        viewOffsetHelper.offsetLeftAndRight(1)
        assertEquals(1, viewOffsetHelper.leftAndRightOffset)
        viewOffsetHelper.offsetLeftAndRight(-1)
        assertEquals(0, viewOffsetHelper.leftAndRightOffset)
    }

    @Test
    @Throws(Throwable::class)
    fun testOffsetTopAndBottom() {
        viewOffsetHelper.offsetTopAndBottom(10)
        assertEquals(10, viewOffsetHelper.topAndBottomOffset)
        viewOffsetHelper.offsetTopAndBottom(-20)
        assertEquals(-10, viewOffsetHelper.topAndBottomOffset)
    }

    @Test
    @Throws(Throwable::class)
    fun testOffsetByLeft() {
        viewOffsetHelper.offsetByLeft(100)
        assertEquals(100, viewOffsetHelper.leftAndRightOffset)
        viewOffsetHelper.offsetByLeft(-200)
        assertEquals(-200, viewOffsetHelper.leftAndRightOffset)
    }

    @Test
    @Throws(Throwable::class)
    fun testOffsetByTop() {
        viewOffsetHelper.offsetByTop(100)
        assertEquals(100, viewOffsetHelper.topAndBottomOffset)
        viewOffsetHelper.offsetByTop(-200)
        assertEquals(-200, viewOffsetHelper.topAndBottomOffset)
    }

    @Test
    @Throws(Throwable::class)
    fun testFinish() {
        viewOffsetHelper.startScroll(viewOffsetHelper.view.left, viewOffsetHelper.view.top, 100, 100)
        assert(!viewOffsetHelper.isFinish)
        viewOffsetHelper.finish()
        assert(viewOffsetHelper.isFinish)
    }

    @Test
    @Throws(Throwable::class)
    fun testStartScroll() {
        viewOffsetHelper.startScroll(viewOffsetHelper.view.left, viewOffsetHelper.view.top, 100, 100)
        viewOffsetHelper.view.postDelayed({
            assertEquals(100, viewOffsetHelper.leftAndRightOffset)
            assertEquals(100, viewOffsetHelper.topAndBottomOffset)
            assert(viewOffsetHelper.isFinish)
        }, 250)
    }
}