package com.jfo.common;

import com.libs.utils.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

/**
 * 
 * 1. uncomment "if (xMoved && !yMoved)" if y axis scroll dominated is desired.
 * 2. set mOverstepEffect to false if the first & last children are not allowed
 *    to scroll out of the SlideView's range. (note: when the first child's width
 *    is less then the screen's width, it is *not* out of the SlideView's range 
 *    when its center is aligned with the screen's center) 
 *
 */
public class MySlideView3 extends ViewGroup {
    private final String TAG = MySlideView3.class.getSimpleName();

    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mTouchSlop;
    private float mDensityScale;
    private int mScreenWidth;
    private int mTotalWidth;
    private OnScrollListener mOnScrollListener;
    private int mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;
    private int mCurrChildIndex = 0;
    private int mCurrHalfChildIndex = 0;
    private int mCurrChildX = 0;
    private int mChildInterval = 0;
    private int mVelocity = 150;
    private boolean mFirstLayout = true;

    public interface OnScrollListener {
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_PASS_HALF = 3;
        public static final int SCROLL_STATE_PASS_WHOLE = 4;
        public static final int SCROLL_STATE_SCROLL_OVER_LAST_CHILD = 5;
        public static final int SCROLL_STATE_SCROLL_OVER_FIRST_CHILD = 6;

        public void onScrollStateChanged(MySlideView3 view, int scrollState, int childIndex);
        public void onScroll(MySlideView3 view, int delta);
    }
    
    public MySlideView3(Context context) {
        this(context, null);
    }

    public MySlideView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySlideView3(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mDensityScale = getContext().getResources().getDisplayMetrics().density;
        mChildInterval = (int) (mDensityScale * 16);
        
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

//        screenWidth = screenWidth < screenHeight ? screenWidth : screenHeight;
        mScreenWidth = screenWidth;
    }
    
//    public void setOverScrollEnabled(boolean enable) {
//        mOverScrollEnabled = enable;
//    }
    
    public void setVelocityScale(double percent) {
        mVelocity *= percent;
        mBounceDuration *= percent;
    }
    
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollListener = onScrollListener;
    }

    public OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    public void reportScrollStateChange(int newState) {
        if (newState != mLastScrollState) {
            mLastScrollState = newState;
            if (mOnScrollListener != null) {
                mOnScrollListener.onScrollStateChanged(this, newState, getCurrentChildIndex());
            }
//            if (newState == OnScrollListener.SCROLL_STATE_IDLE)
//                setCurrentChildIndex(getScrollX() / mScreenWidth);
        }
    }
    
    public void reportScrollStatePassHalf(int newState, int index) {
        mLastScrollState = newState;
        if (mOnScrollListener != null && mCurrHalfChildIndex != index) {
            mOnScrollListener.onScrollStateChanged(this, newState, index);
        }
        mCurrHalfChildIndex = index;
    }

    public void reportScrollStatePassWhole(int newState, int index) {
        setCurrentChildIndex(index);
        mLastScrollState = newState;
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(this, newState, index);
        }
    }

    public void scrollToPreviousChild() {
        int index = getCurrentChildIndex();
        if (index > 0) {
            scrollToChild(index - 1);
        }
    }

    public void scrollToNextChild() {
        int index = getCurrentChildIndex();
        if (index >= 0 && index < getChildCount() - 1) {
            scrollToChild(index + 1);
        }
    }

    public void jumpToChild(int index) {
        if (index >= 0 && index < getChildCount()) {
            setCurrentChildIndex(index);
            int width = getChildAt(index).getWidth();
            scrollTo(mCurrChildX + (width - mScreenWidth) / 2, 0);
        }
    }

    public void scrollToChild(int index) {
        if (index >= 0 && index < getChildCount()) {
            final int oldIndex = getCurrentChildIndex();
            final int startPos = getScrollX();
            setCurrentChildIndex(index);
            final int width = getChildAt(index).getWidth();
            final int endPos = mCurrChildX + (width - mScreenWidth) / 2;
            final int distance = endPos - startPos;
            setCurrentChildIndex(oldIndex);

            if (mFlingRunnable == null) {
                mFlingRunnable = new FlingRunnable();
            } else {
                mFlingRunnable.endFling();
            }
            mFlingRunnable.startScroll(distance, mVelocity);
        }
    }

    public int getCurrentChildIndex() {
        return mCurrChildIndex;
    }

    public void setCurrentChildIndex(int index) {
        if (index >=0 && index < getChildCount()) {
//            if (mCurrChildIndex != index) {
                mCurrChildIndex = index;
                mCurrHalfChildIndex = index;
                updateCurrentChildX();
                getChildAt(index).requestFocus();
//            }
        }
    }
    
    private void updateCurrentChildX() {
        int width = 0;
        int currChildIndex = getCurrentChildIndex();
        int interval = getChildInterval();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (i == currChildIndex)
                mCurrChildX = width;
            if (child.getVisibility() != View.GONE)
                width += child.getWidth() + interval;
        }
    }
    
    public void setChildInterval(int interval) {
        mChildInterval = interval;
    }
    
    public int getChildInterval() {
        return mChildInterval;
    }
    
    public void reset() {
        removeAllViews();
        scrollTo(0, 0);
        mLastScrollState = OnScrollListener.SCROLL_STATE_IDLE;
        mTotalWidth = 0;
        mCurrChildIndex = 0;
        mCurrHalfChildIndex = 0;
        mCurrChildX = 0;
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
//            if (i == getCurrentChildIndex())
//                mCurrChildX = width;
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                width += child.getMeasuredWidth();
                height = Math.max(height, child.getMeasuredHeight());
            }
        }
//        mTotalWidth = width;
        
        setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = 0;

        final int interval = getChildInterval();
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (i == getCurrentChildIndex())
                mCurrChildX = childLeft;
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child
                        .getMeasuredHeight());
                childLeft += childWidth + interval;
            }
        }
        mTotalWidth = childLeft - interval;
        if (mFirstLayout) {
            jumpToChild(getCurrentChildIndex());
            mFirstLayout = false;
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
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         */
        final int action = ev.getAction();
        LogUtil.d(TAG, "===>action:" + action + ",mTouchMode:" + mTouchMode);
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchMode != TOUCH_MODE_REST && mTouchMode != TOUCH_MODE_DOWN)) {
            return true;
        }

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final int x = (int) ev.getX(pointerIndex);
                final int y = (int) ev.getY(pointerIndex);
                final int xDiff = (int) Math.abs(x - mMotionX);
                final int yDiff = (int) Math.abs(y - mMotionY);

                final int touchSlop = mTouchSlop;
                boolean xMoved = xDiff > touchSlop;
                boolean yMoved = yDiff > touchSlop;
                
                LogUtil.d(TAG, "xDiff:" + xDiff + ",yDiff:" + yDiff + "(" + touchSlop + ")");

//                if (xMoved && !yMoved) {
                    if (xMoved) {
                        // Scroll if the user moved far enough along the X axis
                        LogUtil.d(TAG, "xMoved...");
                        // call onTouchEvent after getting xMoved & yMoved
                        // since mMotionX & mMotionY will change in onTouchEvent()
                        onTouchEvent(ev);
                        return true;
                    }
//                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                onTouchEvent(ev);
                return false;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchMode = TOUCH_MODE_REST;
                mActivePointerId = INVALID_POINTER;
                
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                return false;
                
//            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
        }

        return false;
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
    private boolean mOverstepEffect = false;

    private int mBounceDurationScaler = 0;
    private int mBounceDuration = 120;
    private int mBrakeDecelerateTime = 70;

    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if (getChildCount() <= 0)
            return true;
        
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
                        
                        int overstep = getOverstep2();
                        if ( overstep != 0 ) {
                            // scrolling released, but overstepped. Don't care about velocity.  Bounce back.
                            if ( mBounceRunnable == null) {
                                mBounceRunnable = new BounceRunnable();
                            }
                            mBounceRunnable.start(overstep);
                        } else {
                            if (Math.abs(initialVelocity) > mMinimumVelocity) {
                                final int distance = getFlingDistance2(initialVelocity);
//                                distance += initialVelocity > 0 ? -16 : 16;
                                if (mFlingRunnable == null) {
                                    mFlingRunnable = new FlingRunnable();
                                }
//                                mFlingRunnable.start(-initialVelocity);
                                mFlingRunnable.startScroll(distance, mVelocity);
                            } else {
                                slideToScreenBound();
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
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
    
    private boolean startScrollIfNeeded(int deltaX) {
        LogUtil.d(TAG, "startScrollIfNeeded:deltaX:" + deltaX + "(" + mTouchSlop + ")");
        final int distance = Math.abs(deltaX);
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



    public class Params {
        final int scrollX = getScrollX();
        final int scrollXCenter = scrollX + mScreenWidth / 2;
        final int currChildIndex = getCurrentChildIndex();
        final int width = getChildAt(currChildIndex).getWidth();
        final int widthLeft = currChildIndex > 0 ? getChildAt(currChildIndex - 1).getWidth() : -1;
        final int widthRight = currChildIndex < getChildCount() - 1 ? 
                getChildAt(currChildIndex + 1).getWidth() : -1;
        
        final int boundLeft = mCurrChildX;
        final int boundRight = mCurrChildX + width;
        final int center = mCurrChildX + width / 2;
//        final int centerLeft = widthLeft < 0 ? Integer.MIN_VALUE : mCurrChildX - widthLeft + widthLeft / 2 - getChildInterval();
//        final int centerRight = widthRight < 0 ? Integer.MAX_VALUE : mCurrChildX + width + widthRight / 2 + getChildInterval();
        final int centerLeft = widthLeft < 0 ? center : mCurrChildX - widthLeft + widthLeft / 2 - getChildInterval();
        final int centerRight = widthRight < 0 ? center : mCurrChildX + width + widthRight / 2 + getChildInterval();
    }
    /**
     * Track a motion scroll
     *
     * @param deltaX Amount to offset mMotionView. This is the accumulated delta since the motion
     *        began. Positive numbers mean the user's finger is moving down the screen.
     * @param incrementalDeltaX Change in deltaX from the previous event.
     * @return true if we're already at the beginning/end of the list and have nothing to do.
     */
    public boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {
        final int childCount = getChildCount();
        if (childCount == 0) {
            return true;
        }

        scrollBy(deltaX, 0);
        
        int overScrollState = OnScrollListener.SCROLL_STATE_IDLE;
        if (!mOverstepEffect) {
            int d = getOverstep2();
            if (d != 0) {
                scrollBy(d, 0);
                deltaX = d + deltaX;
                if (mTouchMode == TOUCH_MODE_SCROLL) {
                    overScrollState = d > 0 ? OnScrollListener.SCROLL_STATE_SCROLL_OVER_FIRST_CHILD
                            : OnScrollListener.SCROLL_STATE_SCROLL_OVER_LAST_CHILD;
                }
            }
        }

        if (mOnScrollListener != null)
            mOnScrollListener.onScroll(this, deltaX);
        
        Params params = new Params();

        int index = -1;
        if (deltaX > 0 && params.scrollXCenter > params.boundRight)
            index = params.currChildIndex + 1;
        else if (deltaX < 0 && params.scrollXCenter < params.boundLeft)
            index = params.currChildIndex - 1;
        else if ((deltaX > 0 && params.scrollXCenter > params.boundLeft) ||
                (deltaX < 0 && params.scrollXCenter < params.boundRight))
            index = params.currChildIndex;
        if (index >= 0 && index < getChildCount() && index != mCurrHalfChildIndex)
            reportScrollStatePassHalf(OnScrollListener.SCROLL_STATE_PASS_HALF, index);

        index = -1;
        if (deltaX > 0 && params.scrollXCenter >= params.centerRight)
            index = params.currChildIndex + 1;
        else if (deltaX < 0 && params.scrollXCenter <= params.centerLeft)
            index = params.currChildIndex - 1;
        if (index >= 0 && index != params.currChildIndex)
            reportScrollStatePassWhole(OnScrollListener.SCROLL_STATE_PASS_WHOLE, index);

        if (overScrollState != OnScrollListener.SCROLL_STATE_IDLE)
            reportScrollStateChange(overScrollState);
        
        return getOverstep2() != 0;
    }
    
    private void tryScrollBy(int deltaX) {
        
    }

//    public boolean trackMotionScroll(int deltaX, int incrementalDeltaX) {
//        final int childCount = getChildCount();
//        if (childCount == 0) {
//            return true;
//        }
//    
//        if (mOnScrollListener != null)
//            mOnScrollListener.onScroll(this, deltaX);
//        
//        scrollBy(deltaX, 0);
//
//        final int scrollX = getScrollX();
//        final int indexCurr = scrollX / mScreenWidth;
//
//        final int indexHalf = scrollX / (mScreenWidth / 2);
//        int index = -1;
//        if (deltaX > 0 && indexHalf % 2 == 1)
//            index = indexCurr + 1;
//        else if (deltaX < 0 && indexHalf % 2 == 0)
//            index = indexCurr;
//        // sroll over half of child width
//        if (index >= 0 && index != mCurrHalfChildIndex)
//            reportScrollStatePassHalf(OnScrollListener.SCROLL_STATE_PASS_HALF, index);
//
//        final int lastCurrChildIndex = getCurrentChildIndex();
//        int d = scrollX - lastCurrChildIndex * mScreenWidth;
//        if (Math.abs(d) / mScreenWidth >= 1)
//            setCurrentChildIndex(indexCurr);
//        if (lastCurrChildIndex != getCurrentChildIndex())
//            reportScrollStatePassWhole(OnScrollListener.SCROLL_STATE_PASS_WHOLE, getCurrentChildIndex());
//
//        return params.scrollX <= 0 || params.scrollX >= mTotalWidth - mScreenWidth;
//    }
    

    private int getFlingDistance(int velocity) {
        int scrollX = getScrollX();
        int remain = scrollX % mScreenWidth;
        int distance = 0;
        if (velocity < 0) // sroll content left
            distance = mScreenWidth - remain;
        else
            distance = -remain;
        return distance;
    }
    private int getFlingDistance2(int velocity) {
        int distance = 0;

        Params params = new Params();
//        if (velocity < 0 && params.widthRight >= 0) { // scroll content to the left
//            if (params.scrollXCenter >= params.center)
//                distance = params.centerRight - params.scrollXCenter;
//            else
//                distance = params.center - params.scrollXCenter;
//        }
//        else if (velocity > 0 && params.widthLeft >= 0) {
//            if (params.scrollXCenter <= params.center)
//                distance = params.centerLeft - params.scrollXCenter;
//            else
//                distance = params.center - params.scrollXCenter;
//        }
        // fix bug:
        //   when mCurrChildIndex == 0 and screenWidth = 480px,
        //   your finger touch the child view and move left 400px,
        //   then fling to the right. 
        //   The above will return 0 distance since params.widthLeft == -1
        if (velocity < 0) { // scroll content to the left
            if (params.scrollXCenter >= params.center)
                distance = params.centerRight - params.scrollXCenter;
            else
                distance = params.center - params.scrollXCenter;
        }
        else if (velocity > 0) {
            if (params.scrollXCenter <= params.center)
                distance = params.centerLeft - params.scrollXCenter;
            else
                distance = params.center - params.scrollXCenter;
        }
        
        return distance;
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

    private int getOverstep2() {
        int overstep = 0;
        
        Params params = new Params();
        if (params.widthLeft < 0 && params.widthRight < 0) {
                overstep = params.center - params.scrollXCenter;
        }
        else if (params.widthLeft < 0) {
            if (params.scrollXCenter < params.center)
                overstep = params.center - params.scrollXCenter;
        }
        else if (params.widthRight < 0) {
            if (params.scrollXCenter > params.center)
                overstep = params.center - params.scrollXCenter;
        }
        
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
        int distance = 0;
        if (remain < mScreenWidth / 2)
            distance = -remain;
        else
            distance = mScreenWidth - remain;
        
        return distance;
    }
    
    private int getBoundDistance2() {
        Params params = new Params();
        int distance = params.center - params.scrollXCenter;
        int d = 0;
        if (params.scrollXCenter > params.center && params.widthRight >= 0)
            d = params.centerRight - params.scrollXCenter;
        else if (params.scrollXCenter < params.center && params.widthLeft >= 0)
            d = params.centerLeft - params.scrollXCenter;
        if (Math.abs(d) < Math.abs(distance))
            distance = d;
        return distance;
    }
    
    private void slideToScreenBound() {
        int distance = getBoundDistance2();
        if (distance == 0)
            distance = getOverstep2();
        if ( mBounceRunnable == null ) {
            mBounceRunnable = new BounceRunnable();
        }
        mBounceRunnable.start(distance);
    }
    
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//        case KeyEvent.KEYCODE_DPAD_LEFT:
//            scrollToPreviousChild();
//            return true;
//        case KeyEvent.KEYCODE_DPAD_RIGHT:
//            scrollToNextChild();
//            return true;
//
//        case KeyEvent.KEYCODE_DPAD_CENTER:
//        case KeyEvent.KEYCODE_ENTER:
//            // fallthrough to default handling
//        }
//        
//        return super.onKeyDown(keyCode, event);
//    }



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
            Interpolator interpolator = new LinearInterpolator();
            mScroller = new Scroller(getContext(), interpolator);
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
            if (getBoundDistance2() != 0 || getOverstep2() != 0)
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

                    final boolean atEnd = trackMotionScroll(-delta, 0);
    
                    if (more && !atEnd) {
                        invalidate();
                        mLastFlingX = x;
                        post(this);
                        /* for sping mode begin */
                    } else if (atEnd) {
                        if (mBounceEffect && mSpringModel) {
                            int overrun = getOverstep2();
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
            Interpolator interpolator = new LinearInterpolator();
            mScroller = new Scroller(getContext(), interpolator);
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
            
            if (false) Log.w("AbsListView", "Bounce Step: delta="+delta + " pos=" + getOverstep2());
            
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
            if (false) Log.w("AbsListView", "Brake Step: delta="+delta + " pos=" + getOverstep2());
            
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
                mBounceRunnable.start(getOverstep2());
            }
        }
    }


}
