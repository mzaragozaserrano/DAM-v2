package com.miguelzaragozaserrano.dam.v2.presentation.utils

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import kotlin.math.abs


class ImageTouchHelper :
    View.OnTouchListener {

    enum class SwipeDirection {
        TOP_TO_BOTTOM, BOTTOM_TO_TOP, LEFT_TO_RIGHT, RIGHT_TO_LEFT
    }

    private var rootLayout: View? = null
    private var layoutToShowHide: View? = null
    private var gestureDetector: GestureDetector? = null
    private var swipeDirections: MutableList<SwipeDirection>? = null
    private var functionOnLongClickListener: (() -> Unit)? = null
    private var functionOnRemoveImage: (() -> Unit)? = null

    @SuppressLint("ClickableViewAccessibility")
    fun initialize(
        rootLayout: View,
        layoutToShowHide: View?,
        swipeDirections: MutableList<SwipeDirection>,
        maxSwipeDistance: Int = 1,
        functionOnLongClickListener: () -> Unit,
        functionOnRemoveImage: () -> Unit
    ) {
        val gestureListener = GestureListener()
        gestureDetector = GestureDetector(rootLayout.context, gestureListener)
        this.rootLayout = rootLayout
        this.layoutToShowHide = layoutToShowHide
        this.swipeDirections = swipeDirections
        gestureListener.maxSwipeDistance = maxSwipeDistance
        this.rootLayout?.setOnTouchListener(this)
        this.functionOnLongClickListener = functionOnLongClickListener
        this.functionOnRemoveImage = functionOnRemoveImage
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector?.onTouchEvent(event) == true
    }

    inner class GestureListener : SimpleOnGestureListener() {
        var maxSwipeDistance = 1
        private val swipeVelocityThreshold = 1

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            super.onSingleTapUp(e)
            functionOnLongClickListener?.invoke()
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > maxSwipeDistance && abs(velocityX) > swipeVelocityThreshold) {
                        if (diffX > 0) {
                            onSwipeLeftToRight()
                        } else {
                            onSwipeRightToLeft()
                        }
                    }
                    return true
                } else if (abs(diffY) > maxSwipeDistance && abs(velocityY) > swipeVelocityThreshold) {
                    if (diffY > 0) {
                        onSwipeTopToBottom()
                    } else {
                        onSwipeBottomToTop()
                    }
                }
                return true
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return false
        }
    }

    fun onSwipeLeftToRight() {
        val isVisible = layoutToShowHide?.visibility == View.VISIBLE
        if (swipeDirections?.contains(SwipeDirection.LEFT_TO_RIGHT) == true && isVisible || swipeDirections?.contains(
                SwipeDirection.RIGHT_TO_LEFT
            ) == true && !isVisible
        )
            toggleViewVisibilityWithAnimation(SwipeDirection.LEFT_TO_RIGHT)
    }

    fun onSwipeRightToLeft() {
        val isVisible =
            layoutToShowHide?.visibility == View.VISIBLE
        if (swipeDirections?.contains(SwipeDirection.RIGHT_TO_LEFT) == true && isVisible || swipeDirections?.contains(
                SwipeDirection.LEFT_TO_RIGHT
            ) == true && !isVisible
        )
            toggleViewVisibilityWithAnimation(SwipeDirection.RIGHT_TO_LEFT)
    }

    fun onSwipeBottomToTop() {
        val isVisible =
            layoutToShowHide?.visibility == View.VISIBLE
        if (swipeDirections?.contains(SwipeDirection.BOTTOM_TO_TOP) == true && isVisible || swipeDirections?.contains(
                SwipeDirection.TOP_TO_BOTTOM
            ) == true && !isVisible
        )
            toggleViewVisibilityWithAnimation(SwipeDirection.BOTTOM_TO_TOP)
    }

    fun onSwipeTopToBottom() {
        val isVisible =
            layoutToShowHide?.visibility == View.VISIBLE
        if (swipeDirections?.contains(SwipeDirection.TOP_TO_BOTTOM) == true && isVisible || swipeDirections?.contains(
                SwipeDirection.BOTTOM_TO_TOP
            ) == true && !isVisible
        )
            toggleViewVisibilityWithAnimation(SwipeDirection.TOP_TO_BOTTOM)
    }

    private fun toggleViewVisibilityWithAnimation(swipeDirection: SwipeDirection) {
        val currentVisibility = layoutToShowHide?.visibility
        var deltaVal =
            if (swipeDirection == SwipeDirection.LEFT_TO_RIGHT || swipeDirection == SwipeDirection.TOP_TO_BOTTOM) 1000 else -1000
        if (currentVisibility == View.GONE) {
            deltaVal = -deltaVal
        }
        val fromXDelta =
            if (currentVisibility == View.VISIBLE || swipeDirection == SwipeDirection.TOP_TO_BOTTOM || swipeDirection == SwipeDirection.BOTTOM_TO_TOP) 0 else deltaVal
        val toXDelta =
            if (currentVisibility == View.GONE || swipeDirection == SwipeDirection.TOP_TO_BOTTOM || swipeDirection == SwipeDirection.BOTTOM_TO_TOP) 0 else deltaVal
        val fromYDelta =
            if (currentVisibility == View.VISIBLE || swipeDirection == SwipeDirection.LEFT_TO_RIGHT || swipeDirection == SwipeDirection.RIGHT_TO_LEFT) 0 else deltaVal
        val toYDelta =
            if (currentVisibility == View.GONE || swipeDirection == SwipeDirection.LEFT_TO_RIGHT || swipeDirection == SwipeDirection.RIGHT_TO_LEFT) 0 else deltaVal
        val animation: Animation = TranslateAnimation(
            fromXDelta.toFloat(),
            toXDelta.toFloat(),
            fromYDelta.toFloat(),
            toYDelta.toFloat()
        )

        animation.duration = 500
        layoutToShowHide?.startAnimation(animation)
        layoutToShowHide?.visibility =
            if (toXDelta == 0 && toYDelta == 0) View.VISIBLE else View.GONE
        functionOnRemoveImage?.invoke()
    }
}