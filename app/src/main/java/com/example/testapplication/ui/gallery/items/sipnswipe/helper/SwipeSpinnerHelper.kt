package com.example.testapplication.ui.gallery.items.sipnswipe.helper

import android.animation.TimeInterpolator
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.math.MathUtils
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeSpinnerHelper private constructor(val recyclerView: RecyclerView) {
    private var mScrollCallbacks: ScrollCallbacks? = null
    private var mInitXY = 0f
    private var xyDifference = 0f
    private var mDownTime: Long = 0
    private var mTouchSlop = 0
    private var mDraggingUp = true
    private val mPagerSnapHelper = PagerSnapHelper()
    private val mScrollByRunnable: Runnable = object : Runnable {
        override fun run() {
            val translation =
                if (isVertical) recyclerView.translationY else recyclerView.translationX
            val fraction = MathUtils.clamp(Math.abs(translation / dragThreshold), 0f, 1f)
            val scroll = (0f - translation * Math.pow(fraction.toDouble(), SCROLL_EXPONENT)).toInt()
            if (Math.abs(scroll) > 0 && (isVertical && recyclerView.canScrollVertically(scroll) || !isVertical && recyclerView.canScrollHorizontally(
                    scroll
                ))
            ) {
                if (mScrollCallbacks != null) mScrollCallbacks!!.onScrolled((if (translation > 0) 1f else -1f) * fraction)
                recyclerView.scrollBy(if (isVertical) 0 else scroll, if (isVertical) scroll else 0)
            }
            recyclerView.post(this)
        }
    }

    init {
        recyclerView.post(object : Runnable {
            override fun run() {
                if (recyclerView.layoutManager !is LinearLayoutManager) throw RuntimeException("Layout manager should not be null and should be instance of LinearLayoutManager")
                mPagerSnapHelper.attachToRecyclerView(recyclerView)
                val viewConfiguration = ViewConfiguration.get(
                    recyclerView.context
                )
                mTouchSlop = viewConfiguration.scaledTouchSlop
                recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        return handleTouchEvent(e)
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                        handleTouchEvent(e)
                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                })
            }
        })
    }

    /**
     * Use this method to register callbacks to scrolling and
     * reset events. See [ScrollCallbacks]
     *
     * @param scrollCallbacks the callbacks object that you want to register
     */
    fun setScrollCallbacks(scrollCallbacks: ScrollCallbacks?) {
        mScrollCallbacks = scrollCallbacks
    }

    private val linearLayoutManager: LinearLayoutManager?
        private get() = recyclerView.layoutManager as LinearLayoutManager?
    val isVertical: Boolean
        /**
         * Convenience method to check if attached linear layout manager
         * is vertical or horizontal. This potentially throws NullPointerException
         *
         * @return if LinearLayoutManager is vertical
         */
        get() = linearLayoutManager!!.orientation == RecyclerView.VERTICAL

    /**
     * This is used in both onInterceptTouchEvent and onTouchEvent
     * because of the RecyclerView.OnItemTouchListener works. If
     * you were to create a class extending RecyclerView, you can
     * use this only in onTouchEvent, and return true to touch interception
     */
    private fun handleTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xyDifference = 0f
                //for a lousy fling detection
                mDownTime = System.currentTimeMillis()
                mInitXY = if (isVertical) event.rawY else event.rawX
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val currentXY = if (isVertical) event.rawY else event.rawX
                val translation = currentXY - mInitXY.also { xyDifference = it }
                if (Math.abs(xyDifference) > mTouchSlop) {
                    recyclerView.removeCallbacks(mScrollByRunnable)
                    mDraggingUp = currentXY < mInitXY
                    val interpolatedProgress = getInterpolatedProgress(translation, mDraggingUp)
                    if (isVertical) recyclerView.translationY =
                        interpolatedProgress else recyclerView.translationX =
                        interpolatedProgress
                    val direction = (-translation + recyclerView.translationY).toInt()
                    if (isVertical && recyclerView.canScrollVertically(direction) || !isVertical && recyclerView.canScrollHorizontally(
                            direction
                        )
                    ) recyclerView.post(mScrollByRunnable)
                    return true
                }
                return false
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                recyclerView.removeCallbacks(mScrollByRunnable)
                val isFling =
                    System.currentTimeMillis() - mDownTime <= 250 && Math.abs(xyDifference) > mTouchSlop
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?


                //not using fling detection would result in displacement
                //that is not large enough to move the item (due to interpolation)
                //hence we use try to detect fling and manually shift the item
                if (isFling) {
                    val position = linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
                    var view = linearLayoutManager.findViewByPosition(position)
                    if (view == null) view = recyclerView
                    var widthHeight = if (isVertical) view.height else view.width
                    if (!mDraggingUp) widthHeight *= -1
                    val finalWidthHeight = widthHeight
                    recyclerView.post(object : Runnable {
                        override fun run() {
                            recyclerView.smoothScrollBy(
                                if (isVertical) 0 else finalWidthHeight,
                                if (isVertical) finalWidthHeight else 0
                            )
                        }
                    })
                } else {
                    snapToItem()
                }

                //using spring animation after flinging is distracting
                //hence we opt for eye-friendly DecelerateInterpolator
                if (Math.abs(if (isVertical) recyclerView.translationY else recyclerView.translationX) < dragThreshold / 2f) recyclerView.animate()
                    .setInterpolator(
                        LogDecelerateInterpolator.LOG_DECELERATE_INTERPOLATOR
                    ).translationX(0f).translationY(0f).start() else {
                    val springAnim = SpringAnimation(
                        recyclerView,
                        if (isVertical) DynamicAnimation.TRANSLATION_Y else DynamicAnimation.TRANSLATION_X,
                        0f
                    )
                    springAnim.spring.stiffness = 500f
                    springAnim.start()
                }
                if (mScrollCallbacks != null) mScrollCallbacks!!.onResetScroll()
                xyDifference = 0f
                return true
            }
        }
        return true
    }

    /**
     * Manual invocation of SnapHelper since it doesn't work
     * when using [RecyclerView.scrollBy]; it
     * only works with touch gestures, which is not the case here.
     */
    private fun snapToItem() {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
        var id =
            if (mDraggingUp) linearLayoutManager!!.findLastCompletelyVisibleItemPosition() else linearLayoutManager!!.findFirstCompletelyVisibleItemPosition()
        if (id == -1) id =
            if (mDraggingUp) linearLayoutManager.findLastVisibleItemPosition() else linearLayoutManager.findFirstVisibleItemPosition()
        if (id == -1) id =
            if (mDraggingUp) linearLayoutManager.findFirstCompletelyVisibleItemPosition() else linearLayoutManager.findLastCompletelyVisibleItemPosition()
        if (id == -1) id =
            if (mDraggingUp) linearLayoutManager.findFirstVisibleItemPosition() else linearLayoutManager.findLastVisibleItemPosition()
        if (id != -1) {
            val view = linearLayoutManager.findViewByPosition(id)
            if (view != null) {
                val values =
                    mPagerSnapHelper.calculateDistanceToFinalSnap(linearLayoutManager, view)
                if (values != null) recyclerView.smoothScrollBy(
                    values[0],
                    values[1]
                ) else if (DEBUG) Log.w(
                    TAG, "snapToItem: values[] is null"
                )
            } else if (DEBUG) Log.w(TAG, "snapToItem: view is null")
        } else if (DEBUG) Log.w(TAG, "snapToItem: ID is -1")
    }

    /**
     * Interpolate drag action to prevent over-drag beyond threshold
     *
     * @author Plaid@Github
     */
    private fun getInterpolatedProgress(translation: Float, draggingUp: Boolean): Float {
        val dragFraction =
            Math.log10((1 + Math.abs(translation) / dragThreshold).toDouble()).toFloat()
        var dragTo = dragFraction * dragThreshold
        if (draggingUp) {
            dragTo *= -1f
        }
        return dragTo
    }

    private val dragThreshold: Float
        /**
         * @return how many pixel this view can be dragged
         */
        private get() = if (isVertical) recyclerView.height.toFloat() else recyclerView.width * if (isVertical) 1.5f else 0.9f

    /**
     * @author Launcher3
     */
    private class LogDecelerateInterpolator internal constructor(var mBase: Int, var mDrift: Int) :
        TimeInterpolator {
        val mLogScale: Float

        init {
            mLogScale = 1f / computeLog(1f, mBase, mDrift)
        }

        override fun getInterpolation(t: Float): Float {
            return if (java.lang.Float.compare(t, 1f) == 0) 1f else computeLog(
                t,
                mBase,
                mDrift
            ) * mLogScale
        }

        companion object {
            val LOG_DECELERATE_INTERPOLATOR = LogDecelerateInterpolator(80, 0)
            fun computeLog(t: Float, base: Int, drift: Int): Float {
                return -Math.pow(base.toDouble(), -t.toDouble()).toFloat() + 1 + drift * t
            }
        }
    }

    /**
     * Interface that allows detection of scrolling events.
     * You can use directedInterpolationFraction to detect the
     * scrolling direction as well as the amount.
     * directedInterpolationFraction value is between -1 & 1
     */
    interface ScrollCallbacks {
        fun onScrolled(directedInterpolationFraction: Float)
        fun onResetScroll()
    }

    companion object {
        //set to 1 to make scrolling linear; >1 to make it exponential
        private const val SCROLL_EXPONENT = 2.8
        private const val DEBUG = false
        private const val TAG = "SwipeSpinnerHelper"
        fun bindRecyclerView(recyclerView: RecyclerView): SwipeSpinnerHelper {
            return SwipeSpinnerHelper(recyclerView)
        }
    }
}