package com.android.core.slidepanel;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by tuannx on 12/1/2014.
 */
public class BottomView extends FrameLayout {
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MAX_DURATION_FOR_FLING = 500;
    int mBottomTopOffset;
    private int bottomOffset;
    private boolean isTouchAllow = true;
    private VelocityTracker velocityTracker;
    private float initialY;
    private float lastY;
    private float divTouch;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mFlingDistance;
    private int mActivePointerId;
    private Fillinger fillinger;
    private ISlide slideListener;
    /**
     * max slide distance
     */
    private float maxDistance;


    public BottomView(Context context) {
        super(context);
        init(context);
    }

    public BottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat
                .getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        final float density = context.getResources().getDisplayMetrics().density;
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);

        fillinger = new Fillinger(context);
    }

    public void setTopOffset(int mBottomTopOffset) {
        this.mBottomTopOffset = mBottomTopOffset;
    }

    /* (non-Javadoc)
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!isTouchAllow) {
            return true;
        }
        final int action = ev.getAction();
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(ev);
        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // Remember where the motion event started
                int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                initialY = lastY = ev.getY() + getTop();
                removeCallbacks(fillinger);
                break;
            case MotionEvent.ACTION_MOVE:
                divTouch = ev.getY() + getTop() - lastY;
                lastY = ev.getY() + getTop();
                moveViewByY(divTouch);
                break;

            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) VelocityTrackerCompat.getYVelocity(
                        velocityTracker, mActivePointerId);

                onTouchUp(initialVelocity);
                velocityTracker.recycle();

                break;
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.recycle();
                return super.onTouchEvent(ev);

        }

        return true;
    }

    private void onTouchUp(int initialVelocity) {
        Log.d("", "ontouchup initialVelocity " + initialVelocity);
        maxDistance = getHeight() - mBottomTopOffset;
        if (initialVelocity < 0) {
            //up
//               float divY =mBottomTopOffset - getTop();
            fillinger.startScroll(getTop(), mBottomTopOffset, maxDistance, MAX_DURATION_FOR_FLING);
            Log.d("", "startScroll " + getTop() + " endY " + mBottomTopOffset + " velocity " + initialVelocity);

        } else if (initialVelocity > 0) {
            //down
            float divY = getHeight() - getTop();
            fillinger.startScroll(getTop(), getHeight() - bottomOffset, maxDistance, MAX_DURATION_FOR_FLING);
            Log.d("", "startScroll " + getTop() + " endY " + (getHeight() - bottomOffset) + " velocity " + initialVelocity);
        } else {
            //no velocity,  slide by position
            float divTopY = mBottomTopOffset - getTop();
            float divBottomY = getHeight() - getTop();
            if (Math.abs(divTopY) >= maxDistance / 2) {
                fillinger.startScroll(getTop(), getHeight() - bottomOffset, maxDistance, MAX_DURATION_FOR_FLING);
                Log.d("", "startScroll " + getTop() + " endY " + (getHeight() - bottomOffset) + " velocity " + initialVelocity);
            } else {
                fillinger.startScroll(getTop(), mBottomTopOffset, maxDistance, MAX_DURATION_FOR_FLING);
                Log.d("", "startScroll " + getTop() + " endY " + mBottomTopOffset + " velocity " + initialVelocity);
            }
        }
    }

    private void moveViewByY(float divTouch) {
        Log.d("", "moveViewByY divTouch " + divTouch);
        int viewTop = getTop() + (int) divTouch;
        layout(getLeft(), viewTop, getRight(), viewTop + getHeight());
        if(slideListener!=null){
            slideListener.onSlide(divTouch);
        }
        Log.d("", "moveViewByY viewTop " + viewTop);
    }

    public void setTouchEnable(boolean enable) {
        this.isTouchAllow = enable;
    }

    public void setBottomOffset(int bottomOffset) {
        this.bottomOffset = bottomOffset;
    }

    public void setSlideListener(ISlide slideListener) {
        this.slideListener = slideListener;
    }

    public class Fillinger implements Runnable {
        private static final String tag = "Fillinger";
        private Scroller mScroller;
        private int lastY;

        public Fillinger(Context context) {
            mScroller = new Scroller(context);
        }

        public void startScroll(float startY, float endY, float distance, int maxDurationForFling) {
            int duration = (int) Math.min(Math.abs((endY - startY)) / distance * maxDurationForFling, maxDurationForFling);
            Log.d("", "startScroll " + startY + " endY " + endY + " duration " + duration);
            lastY = (int) startY;
            if(slideListener!=null){
                slideListener.onStartSlide();
            }
            mScroller.startScroll(0, (int) startY, 0, -(int) (endY - startY), duration);
            post(this);
        }

        @Override
        public void run() {
            boolean more = mScroller.computeScrollOffset();
            int currentY = mScroller.getCurrY();
            final int diffY = lastY - currentY;
            moveViewByY(diffY);
            lastY = currentY;
            Log.d("", "onscroll " + diffY + " currentY " + currentY);
            if (more) {
                post(this);
            }else{
                if(slideListener!=null){
                    slideListener.onSlideFinish();
                }
            }
        }
    }
}