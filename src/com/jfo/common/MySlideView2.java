//package com.jfo.common;
package com.jfo.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class MySlideView2 extends ViewGroup {

    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mTouchSlop;
    private float mDensityScale;
    private int mScreenWidth;
    private int mTotalWidth;
    private OnScrollListener mOnScrollListener;
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private int mCurrChildIndex = -1;

    public interface OnScrollListener {
        public static int SCROLL_STATE_IDLE = 0;
        public static int SCROLL_STATE_TOUCH_SCROLL = 1;
        public static int SCROLL_STATE_FLING = 2;

        public void onScrollStateChanged(MySlideView2 view, int scrollState, int childIndex);
        public void onScroll(MySlideView2 view, int delta);
    }
    
    public MySlideView2(Context context) {
        this(context, null);
    }

    public MySlideView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySlideView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mDensityScale = getContext().getResources().getDisplayMetrics().density;
        
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        screenWidth = screenWidth < screenHeight ? screenWidth : screenHeight;
        mScreenWidth = screenWidth;
    }
    
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    public void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            mCurrChildIndex = getScrollX() / mScreenWidth;
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(this, newState, mCurrChildIndex);
                mLastScrollState = newState;
            }
        }
    }
    
    public void scrollToPreviousChild() {
        int index = getCurrentChildIndex();
        if (index > 0) {
            setCurrentChildIndex(mCurrChildIndex - 1);
        }
    }

    public void scrollToNextChild() {
        int index = getCurrentChildIndex();
        if (index >= 0 && index < getChildCount() - 1) {
            setCurrentChildIndex(mCurrChildIndex + 1);
        }
    }

    public void scrollToChild(int index) {
        if (index >= 0 && index < getChildCount() - 1) {
            setCurrentChildIndex(index);
        }
    }

    public int getCurrentChildIndex() {
        return mCurrChildIndex;
    }

    public void setCurrentChildIndex(int index) {
        if (index >=0 && index < getChildCount()) {
            if (mCurrChildIndex != index) {
                mCurrChildIndex = index;
                scrollTo(mScreenWidth * mCurrChildIndex, 0);
            }
        }
    }

    public void reset() {
        removeAllViews();
        scrollTo(0, 0);
    }

    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        // Find out how big everyone wants to be
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            width += child.getMeasuredWidth();
            height = Math.max(height, child.getMeasuredHeight());
        }
        mTotalWidth = width;
        
        if (count > 0 && mCurrChildIndex == -1)
            mCurrChildIndex = 0;
        
//        scrollTo(mLeftBarWidth, 0);
        
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child
                        .getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }


//        layoutChildren();
//
//        if( mSpringModel ) {// AplyLai@ F0X.B-2728 Should do bounce if necessary when onLayout 
//            int overstep = getOverstep();
//            if(overstep != 0) {
//                if (mBounceRunnable == null) {
//                    mBounceRunnable = new BounceRunnable();
//                  }
//                 reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
//                   mBounceRunnable.start(overstep);
//            }
//        }
//        // AplyLai@ F0X.B-2728 Should do bounce if necessary when onLayout
//        mInLayout = false;

    
    
//    protected void layoutChildren() {
//        if( mSpringModel ) {// AplyLai@ F0X.B-2728 Should do bounce if necessary when onLayout 
//            if ( mTouchMode == TOUCH_MODE_SCROLL || mTouchMode == TOUCH_MODE_FLING ) {
//                mOverstepSyncValue = getOverstep();
//                // Stop bouncing
//                if ( mOverstepSyncValue != 0 && mTouchMode == TOUCH_MODE_FLING && mBounceRunnable!=null) {
//                    removeCallbacks(mBounceRunnable);
//                    if (mTouchMode == TOUCH_MODE_FLING) {
//                        mTouchMode = TOUCH_MODE_REST;
//                        int overstep = getOverstep();
//                        if (overstep != 0) {
//                            offsetChildrenTopAndBottom(-overstep);
//                        }
//                    }
//                    mOverstepSyncValue = 0;
//                }
//            }
//        }
//    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return super.onInterceptTouchEvent(ev);
    }
    

    
    private static final int TOUCH_MODE_REST = -1;
    private static final int TOUCH_MODE_DOWN = 0;
    private static final int TOUCH_MODE_TAP = 1;
    private static final int TOUCH_MODE_DONE_WAITING = 2;
    private static final int TOUCH_MODE_SCROLL = 3;
    private static final int TOUCH_MODE_FLING = 4;

    private int mTouchMode = TOUCH_MODE_REST;
    
    private static final int INVALID_POINTER = -1;
    private int mActivePointerId = INVALID_POINTER;
    private int mMotionX;
    private int mMotionY;

    private VelocityTracker mVelocityTracker;
    private FlingRunnable mFlingRunnable;
    private BrakeRunnable mBrakeRunnable;
    private BounceRunnable mBounceRunnable;

    private boolean mBounceEffect = true; 
    private boolean mSpringModel = true;

    private int mBounceDurationScaler = 0;
    private int mBounceDuration = 1200;
    private int mBrakeDecelerateTime = 70;

    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = ev.getPointerId(0);
                final int x = (int) ev.getX();
                final int y = (int) ev.getY();
                mTouchMode = TOUCH_MODE_DOWN;
                
                mMotionX = x;
                mMotionY = y;
                
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex == -1)
                    break;
                final int x = (int) ev.getX(pointerIndex);
                int deltaX = x - mMotionX;
                
                switch (mTouchMode) {
                    case TOUCH_MODE_DOWN:
                        // Check if we have moved far enough that it looks more like a
                        // scroll than a tap
                        if (!startScrollIfNeeded(deltaX))
                            break;
                        else;
                            // pass by 
                    case TOUCH_MODE_SCROLL:
                        trackMotionScroll(-deltaX, 0);
                        mMotionX = x;
                        break;
                }
                
                break;
            }
            case MotionEvent.ACTION_UP: {
                switch (mTouchMode) {
                    case TOUCH_MODE_DOWN:
                    case TOUCH_MODE_TAP:
                    case TOUCH_MODE_DONE_WAITING:
                        mTouchMode = TOUCH_MODE_REST;
                        if (!performClick())
                            ((View) this.getParent()).performClick();
                        slideToScreenBound();
                        break;
                    case TOUCH_MODE_SCROLL: {
                        final VelocityTracker velocityTracker = mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                        final int initialVelocity = (int) velocityTracker.getXVelocity();
                        
                        int overstep = getOverstep();
                        if ( overstep != 0 ) {
                            // scrolling released, but overstepped. Don't care about velocity.  Bounce back.
                            if ( mBounceRunnable == null) {
                                mBounceRunnable = new BounceRunnable();
                            }
                            mBounceRunnable.start(overstep);
                        } else {
                            if (Math.abs(initialVelocity) > mMinimumVelocity) {
                                int scrollX = getScrollX();
                                int remain = scrollX % mScreenWidth;
                                int distance;
                                if (initialVelocity < 0) // sroll content left
                                    distance = mScreenWidth - remain;
                                else
                                    distance = -remain;

                                if (mFlingRunnable == null) {
                                    mFlingRunnable = new FlingRunnable();
                                }
//                                mFlingRunnable.start(-initialVelocity);
                                mFlingRunnable.startScroll(distance, 600);
                            } else {
                                slideToScreenBound();
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                slideToScreenBound();
                break;
            }
        }
        
        return true;
    }
    
    private boolean startScrollIfNeeded(int deltaY) {
        final int distance = Math.abs(deltaY);
        if (distance > mTouchSlop) {
//            createScrollingCache();
            mTouchMode = TOUCH_MODE_SCROLL;
//            mMotionCorrection = deltaY;
//            requestDisallowInterceptTouchEvent(true);
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
            return true;
        }
        return false;
    }
    
    /**
     * Track a motion scroll
     *
     * @param deltaX Amount to offset mMotionView. This is the accumulated delta since the motion
     *        began. Positive numbers mean the user's finger is moving down the screen.
     * @param incrementalDeltaX Change in deltaY from the previous event.
     * @return true if we're already at the beginning/end of the list and have nothing to do.
     */
    public boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }

        scrollBy(deltaX, 0);
        if (mOnScrollListener != null)
            mOnScrollListener.onScroll(this, deltaX);
        
        int scrollX = getScrollX();
        
        return scrollX <= 0 || scrollX >= mTotalWidth - mScreenWidth;
    }
    
    private int getOverstep() {
        int scrollX = getScrollX();
        int overstep = 0;
        if (scrollX < 0)
            overstep = -scrollX;
        else if (scrollX > mTotalWidth - mScreenWidth)
            overstep = mTotalWidth - mScreenWidth - scrollX;
        return overstep;
    }


//    private void createScrollingCache() {
//        if (mScrollingCacheEnabled && !mCachingStarted) {
//            setChildrenDrawnWithCacheEnabled(true);
//            setChildrenDrawingCacheEnabled(true);
//            mCachingStarted = true;
//        }
//    }
//
//    private void clearScrollingCache() {
//        if (mClearScrollingCache == null) {
//            mClearScrollingCache = new Runnable() {
//                public void run() {
//                    if (mCachingStarted) {
//                        mCachingStarted = false;
//                        setChildrenDrawnWithCacheEnabled(false);
//                        if ((mPersistentDrawingCache & PERSISTENT_SCROLLING_CACHE) == 0) {
//                            setChildrenDrawingCacheEnabled(false);
//                        }
//                        if (!isAlwaysDrawnWithCacheEnabled()) {
//                            invalidate();
//                        }
//                    }
//                }
//            };
//        }
//        post(mClearScrollingCache);
//    }

    private int getBoundDistance() {
        int scrollX = getScrollX();
        int screen = scrollX / mScreenWidth;
        int remain = scrollX % mScreenWidth;
        int distance;
        if (remain < mScreenWidth / 2)
            distance = -remain;
        else
            distance = mScreenWidth - remain;
        
        return distance;
    }
    
    private void slideToScreenBound() {
        int distance = getBoundDistance();
        if ( mBounceRunnable == null ) {
            mBounceRunnable = new BounceRunnable();
        }
        mBounceRunnable.start(distance);
    }
    
    /**
     * Responsible for fling behavior. Use {@link #start(int)} to
     * initiate a fling. Each frame of the fling is handled in {@link #run()}.
     * A FlingRunnable will keep re-posting itself until the fling is done.
     *
     */
    private class FlingRunnable implements Runnable {
        /**
         * Tracks the decay of a fling scroll
         */
        private final Scroller mScroller;

        /**
         * Y value reported by mScroller on the previous fling
         */
        private int mLastFlingX;
        private int mInitVelocity;

        FlingRunnable() {
            mScroller = new Scroller(getContext());
        }

        void start(int initialVelocity) {
            int initialX = initialVelocity < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            mScroller.fling(initialX, 0, initialVelocity, 0,
                    0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
            mTouchMode = TOUCH_MODE_FLING;
            mInitVelocity = initialVelocity;
            
            post(this);
        }

        /**
         * 
         * @param distance Horizontal distance to travel. Positive numbers will scroll the content to the left.
         * @param duration
         */
        void startScroll(int distance, int duration) {
            int initialX = distance < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            mScroller.startScroll(initialX, 0, distance, 0, duration);
            mTouchMode = TOUCH_MODE_FLING;
            post(this);
        }

        private void endFling() {
            mTouchMode = TOUCH_MODE_REST;

            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
//            clearScrollingCache();

            removeCallbacks(this);
        }

        private void endFlingIfNeeded() {
            if (getBoundDistance() != 0)
                slideToScreenBound();
            else
                endFling();
        }
        
        public void run() {
            switch (mTouchMode) {
                case TOUCH_MODE_FLING: {
                    final Scroller scroller = mScroller;
                    boolean more = scroller.computeScrollOffset();
                    final int x = scroller.getCurrX();
    
                    // Flip sign to convert finger direction to list items direction
                    // (e.g. finger moving down means list is moving towards the top)
                    int delta = mLastFlingX - x;
    
                    // Pretend that each frame of a fling scroll is a touch scroll
                    if (delta > 0) {
                        // List is moving towards the top. Use first view as mMotionPosition
    
                        // Don't fling more than 1 screen
                        delta = Math.min(getWidth() - getPaddingRight() - getPaddingLeft() - 1, delta);
                    } else {
                        // List is moving towards the bottom. Use last view as mMotionPosition
    
                        // Don't fling more than 1 screen
                        delta = Math.max(-(getWidth() - getPaddingRight() - getPaddingLeft() - 1), delta);
                    }

                    final boolean atEnd = trackMotionScroll(-delta, -delta);
    
                    if (more && !atEnd) {
                        invalidate();
                        mLastFlingX = x;
                        post(this);
                        /* for sping mode begin */
                    } else if (atEnd) {
                        if (mBounceEffect && mSpringModel) {
                            int overrun = getOverstep();
//                            overrun = 0;
                            int leftVelocity = mInitVelocity
                                    * (mScroller.getDuration() - mScroller.timePassed())
                                    / mScroller.getDuration();
                            if (mBrakeRunnable == null) {
                                mBrakeRunnable = new BrakeRunnable();
                            }
                            
                            if (overrun != 0)
                                mBrakeRunnable.start(leftVelocity,
                                        (getWidth() - getPaddingRight() - getPaddingLeft() - 1) / 2,
                                        overrun);
                            else
                                endFlingIfNeeded();
                        } else {
                            endFlingIfNeeded();
                        }
                    }
                    else
                        endFlingIfNeeded();
                    break;
                }
                default:
                    return;
            }

        }
    }

    /* for spring mode begin */
    private class BounceRunnable implements Runnable {
        private Scroller mScroller;
        /**
         * Y value reported by mScroller on the previous fling
         */
        private int mLastFlingX;

        public BounceRunnable() {
            mScroller = new Scroller(getContext());
        }
        
        /**
         * 
         * @param distance distance > 0: bounce back to the left end;
         *              distance < 0: bounce back to the right end.
         */
        public void start(int distance) {
            int duration = Math.abs(distance)*mBounceDurationScaler + mBounceDuration;

            int startX = distance < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = startX;
            mTouchMode = TOUCH_MODE_FLING;

            mScroller.startScroll(startX, 0, distance, 0, duration);
            if (false) Log.w("AbsListView", "Bounce Start: distance=" + distance);
            
            post(this);
        }

        private void endFling() {
            mTouchMode = TOUCH_MODE_REST;
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
//            clearScrollingCache();
        }

        public void run() {
            if (mTouchMode != TOUCH_MODE_FLING) {
                return;
            }

            if (getChildCount() == 0) {
                endFling();
                return;
            }

            Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();

            int delta = mLastFlingX - x;
            
            if (false) Log.w("AbsListView", "Bounce Step: delta="+delta + " pos=" + getOverstep());
            
//            mBlockLayoutRequests = true;
//            invokeOnItemScrollListener();
//            offsetChildrenTopAndBottom(delta);
            trackMotionScroll(-delta, 0);
            invalidate();
//            mBlockLayoutRequests = false;

            if (more) {
                mLastFlingX = x;
                post(this);
            } else {
                endFling();
            }
        }
    }

    private class BrakeRunnable implements Runnable, Interpolator {
        /**
         * Tracks the decay of a fling scroll
         */
        private Scroller mScroller;
        int mOverrun;
        /**
         * Y value reported by mScroller on the previous fling
         */
        private int mLastFlingX;
        private float mPassedTimeRatio;

        public BrakeRunnable() {
            mScroller = new Scroller(getContext(), this);
        }

        public float getInterpolation(float x) {
            // shm deceleration
            x+=mPassedTimeRatio;
            return (float)Math.sin(x*Math.PI/2);
        }

        public void start(int velocity, int maxOffset, int overrun) {

            boolean positive = velocity > 0 ? true : false;

            velocity = Math.abs(velocity);
            
            /* shm: 2(PI)r / V = T = 4t, t=mBrakeDecelerateTime, v=initial velocity, 
             *      r = 4tV/2(PI) = 2tV/(PI)
             */
            
            int distance = (int) (2 * mBrakeDecelerateTime * velocity / Math.PI/1000);
            if ( distance > maxOffset ) {
                // scale down velocity to fit desired t
                velocity = velocity * distance / maxOffset;
                distance = maxOffset;
            }

            overrun = Math.abs(overrun);
            if ( overrun * 2 > distance ) {
                overrun = distance /2;
            }

            if ( positive ) {
                mOverrun = overrun; // first delta modifier
            }
            else {
                mOverrun = -overrun; // first delta modifier
            }

            mPassedTimeRatio = (float)(Math.asin((float)overrun / distance)*2/Math.PI);
            
            if (!positive) {
                distance = -distance;
            }

            // deceleration
            int startX = distance < 0 ? Integer.MAX_VALUE : 0;
            
            mLastFlingX = startX;
            mTouchMode = TOUCH_MODE_FLING;

            mScroller.startScroll(startX, 0, distance, 0, mBrakeDecelerateTime);
            
            if (false) Log.w("AbsListView", "Brake Start: distance=" + distance 
                                + " overrun=" + overrun + " passTime=" + mPassedTimeRatio);
            post(this);
        }

        private void endFling() {
            mTouchMode = TOUCH_MODE_REST;
            reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
//            clearScrollingCache();
        }

        public void run() {
            if (mTouchMode != TOUCH_MODE_FLING) {
                return;
            }

            if (getChildCount() == 0) {
                endFling();
                return;
            }

            Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();

            int delta = mLastFlingX - x;
            if (false) Log.w("AbsListView", "Brake Step: delta="+delta + " pos=" + getOverstep());
            
            if ( mOverrun != 0) {
                delta += mOverrun;
                mOverrun = 0;
            }
            
//            mBlockLayoutRequests = true;
//            invokeOnItemScrollListener();
//            offsetChildrenTopAndBottom(delta);
            trackMotionScroll(-delta, 0);
            invalidate();
//            mBlockLayoutRequests = false;

            if (more) {
                mLastFlingX = x;
                post(this);
            } else {
                /* sliding back */
                if ( mBounceRunnable == null ) {
                    mBounceRunnable = new BounceRunnable();
                }
                mBounceRunnable.start(getOverstep());
            }
        }
    }


}
