package com.scott.swipe

import android.support.annotation.LayoutRes
import android.support.test.InstrumentationRegistry
import android.support.test.filters.FlakyTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.scott.swipe.test.SwipeLayoutTestActivity
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.hamcrest.core.Is
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [SwipeLayout].
 */
@RunWith(AndroidJUnit4::class)
class SwipeLayoutTest {
    @JvmField
    @Rule
    val activityRule = ActivityTestRule(SwipeLayoutTestActivity::class.java)

    @Test
    @FlakyTest
    fun testLoadFromLayoutXml() {
        val swipeLayout = createSwipeLayout(R.layout.activity_simple)

        assertNotNull(swipeLayout)
        val swipeView = swipeLayout.findViewById<View>(R.id.simple_swipe_view)
        assertThat((swipeView.layoutParams as SwipeLayout.LayoutParams).itemType, Is.`is`(ItemType.SWIPE_VIEW))

        assertNotNull(swipeView)
        val startMenu = swipeLayout.findViewById<View>(R.id.simple_start_menu)
        assertNotNull(startMenu)
        assertThat((startMenu.layoutParams as SwipeLayout.LayoutParams).itemType, Is.`is`(ItemType.START_MENU))

        val endMenu = swipeLayout.findViewById<View>(R.id.simple_end_menu)
        assertNotNull(endMenu)
        assertThat((endMenu.layoutParams as SwipeLayout.LayoutParams).itemType, Is.`is`(ItemType.END_MENU))

        assertThat(swipeLayout.swipeOrientation, Is.`is`(SwipeOrientation.HORIZONTAL))
        assertThat(swipeLayout.endSwipeHandler.javaClass.simpleName, Is.`is`(SwipeSnapHandler::class.java.simpleName))
    }

    @Test
    @FlakyTest
    fun testLoadFromCode() {
        val swipeLayout = SwipeLayout(activityRule.activity)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        swipeLayout.layoutParams = layoutParams
        val startMenu = createView(ItemType.START_MENU)
        val endMenu = createView(ItemType.END_MENU)
        val swipeView = createView(ItemType.SWIPE_VIEW)
        swipeLayout.addView(startMenu)
        swipeLayout.addView(endMenu)
        swipeLayout.addView(swipeView)

    }

    @Test
    @FlakyTest
    fun testOpenAndCloseStartMenuHorizontal() {
        val swipeLayout = createSwipeLayout(R.layout.activity_simple)
        swipeLayout.openStartMenu(false)
        assertEquals(swipeLayout.startMenu?.right, swipeLayout.swipeView?.left)
        swipeLayout.closeStartView(false)
        assertEquals(0, swipeLayout.startMenu?.left)


        swipeLayout.openEndMenu(false)
        assertEquals(swipeLayout.endMenu?.left, swipeLayout.swipeView?.right)
        swipeLayout.closeEndView(false)
        assertEquals(0, swipeLayout.startMenu?.left)
    }


    private fun createView(@ItemType itemType: Int): View {
        val view = View(activityRule.activity)
        val layoutParams = SwipeLayout.LayoutParams(SwipeLayout.LayoutParams.MATCH_PARENT, 400)
        layoutParams.itemType = itemType
        view.layoutParams = layoutParams
        return view
    }

    @Throws(Throwable::class)
    private fun createSwipeLayout(@LayoutRes activityLayoutResId: Int,
                                  configuration: Configuration = Configuration.EMPTY): SwipeLayout {
        val activity = activityRule.activity
        activityRule.runOnUiThread {
            activity.setContentView(activityLayoutResId)
            val swipeLayout = activity.findViewById<SwipeLayout>(R.id.swipe_layout)
            configuration.apply(swipeLayout)
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        return activity.findViewById<View>(R.id.swipe_layout) as SwipeLayout
    }

    private interface Configuration {

        fun apply(swipeLayout: SwipeLayout)

        companion object {
            val EMPTY: Configuration = object : Configuration {
                override fun apply(swipeLayout: SwipeLayout) = Unit
            }
        }
    }
}