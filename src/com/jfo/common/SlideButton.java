package com.jfo.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SlideButton extends FrameLayout implements View.OnClickListener, Checkable {
    private ImageView mThumb;
    private StateListDrawable mThumbStateListDrawable = null;
    private LinearLayout mThumbScroller;
    private boolean mIsOn;
    private static final int INVALID_POINTER = -1;
    private boolean mIsBeingDragged = false;
    private boolean mIsBeingSlide = false;
    private float mLastMotionX;
    private int mActivePointerId = INVALID_POINTER;
    private int mScrollLength = 0;
    private int mScrollPos = 0;
    private int mTotalMove = 0;
    private static final int THRESHOLD = 6;
    
    public SlideButton(Context context) {
        super(context);
    }
    
    public SlideButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public SlideButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideButton, defStyle, 0);

        Drawable track = a.getDrawable(R.styleable.SlideButton_track);
        Drawable thumb = a.getDrawable(R.styleable.SlideButton_thumb);
        mIsOn = a.getBoolean(R.styleable.SlideButton_initOn, false);
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.slidebutton_layout, this, // we are the parent
                true);
        mThumb = (ImageView) findViewById(R.id.thumb);
        mThumbScroller = (LinearLayout) findViewById(R.id.thumb_scroller);
        if (track != null)
            mThumbScroller.setBackgroundDrawable(track);
        if (thumb != null)
            mThumb.setBackgroundDrawable(thumb);

        track = mThumbScroller.getBackground();
        thumb = mThumb.getBackground();
        if (track != null && thumb != null)
            // will be updated in onMeasure()
            mScrollLength = track.getIntrinsicWidth() - thumb.getIntrinsicWidth();
        if (thumb instanceof StateListDrawable)
            mThumbStateListDrawable = (StateListDrawable) thumb;

        mThumbScroller.setOnClickListener(this);
        mThumbScroller.setOnTouchListener(mThumbTouchListener);

        restore();
    }

    public void onClick(View v) {
        toggle();
    }
    
    public void toggle() {
        mIsOn = mIsOn ? false : true;
        slideTo(mIsOn);
    }
    
    public void restore() {
        slideTo(mIsOn);
    }
    
    private void slideTo(boolean on) {
        int pos = on ? mScrollLength : 0;
        adjustThumbTo(pos);
    }
    
    private void adjustThumbBy(int delta) {
        adjustThumbTo(mScrollPos + delta);
    }

    private void adjustThumbTo(int pos) {
        mScrollPos = pos;
        if (mScrollPos > mScrollLength)
            mScrollPos = mScrollLength;
        if (mScrollPos < 0)
            mScrollPos = 0;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mThumb.getLayoutParams();
        lp.leftMargin = mScrollPos;
        mThumb.setLayoutParams(lp);
//        mThumbScroller.setPadding(mScrollPos, 0, 0, 0);
    }

    private View.OnTouchListener mThumbTouchListener = new View.OnTouchListener() {
        
        public boolean onTouch(View v, MotionEvent ev) {
            boolean ret = false;
            final int action = ev.getAction();
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (mThumbStateListDrawable != null)
//                        mThumbDrawable.selectDrawable(0);
                        mThumbStateListDrawable.setState(new int[] { android.R.attr.state_pressed });

                    final float x = ev.getX();
                    if (!(mIsBeingDragged = inThumb((int) x, (int) ev.getY()))) {
                        return false;
                    }
                    mIsBeingDragged = true;
                    mIsBeingSlide = false;
                    mLastMotionX = x;
                    mTotalMove = 0;
                    mActivePointerId = ev.getPointerId(0);
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                    if (mIsBeingDragged) {
                        final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                        final float x = ev.getX(activePointerIndex);
                        final int deltaX = (int) (x - mLastMotionX);
                        mLastMotionX = x;
                        mTotalMove += deltaX;
                        if (Math.abs(mTotalMove) > THRESHOLD)
                            mIsBeingSlide = true;
//                        mThumbScroller.scrollBy(-deltaX, 0);
                        adjustThumbBy(deltaX);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mThumbStateListDrawable != null)
                        mThumbStateListDrawable.setState(new int[] { android.R.attr.state_enabled });

                    if (mIsBeingDragged) {
                        mActivePointerId = INVALID_POINTER;
                        mIsBeingDragged = false;
                        if (mIsBeingSlide) {
                            ret = true;
                            int origPos = mIsOn ? mScrollLength : 0;
                            int slop = 0; //Math.abs(mScrollLength / 3)
                            if (Math.abs(mScrollPos - origPos) > slop)
                                toggle();
                            else
                                restore();
                        }
                        mTotalMove = 0;
                        mIsBeingSlide = false;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mThumbStateListDrawable != null)
                        mThumbStateListDrawable.setState(new int[] { android.R.attr.state_enabled });

                    if (mIsBeingDragged) {
                        mActivePointerId = INVALID_POINTER;
                        mIsBeingDragged = false;
                        mIsBeingSlide = false;
                        mTotalMove = 0;
                        restore();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    onSecondaryPointerUp(ev);
                    break;
            }
            return ret;
        }
    };
    
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = ev.getX(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }
    
    private boolean inThumb(int x, int y) {
        final View child = mThumb;
        return !(y < child.getTop() || y >= child.getBottom() 
                || x < child.getLeft() || x >= child.getRight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int widthTrack = mThumbScroller.getMeasuredWidth();
        int widthTrackOrig = mThumbScroller.getBackground().getIntrinsicWidth();
        int widthThumbOrig = mThumb.getBackground().getIntrinsicWidth();
        int widthThumb = widthThumbOrig;
        int heightThumb = mThumb.getMeasuredHeight();
        if (widthTrackOrig > 0 && widthThumbOrig > 0) {
            float ratio = (float)widthTrack / widthTrackOrig;
            widthThumb = Math.round(widthThumbOrig * ratio);
        }
        else
            widthThumb = widthTrack / 2 + 1;
        
        int thumbWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, widthThumb);
        int thumbHeightMeasureSpec =
            MeasureSpec.makeMeasureSpec(heightThumb, MeasureSpec.EXACTLY);
        
        mThumb.measure(thumbWidthMeasureSpec, thumbHeightMeasureSpec);
        
        int len = widthTrack - mThumb.getMeasuredWidth();
        if (mScrollLength != len) {
            mScrollLength = len;
            restore();
        }
    }

    public boolean isChecked() {
        return mIsOn;
    }

    public void setChecked(boolean on) {
        if (mIsOn != on) {
            mIsOn = on;
            refreshDrawableState();
            restore();
        }
    }

}
