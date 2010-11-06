package com.jfo.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ToggleView extends View implements View.OnClickListener {
    private Drawable mOn, mOff;
    private boolean mIsOn;
    
    public ToggleView(Context context) {
        super(context);
    }
    
    public ToggleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public ToggleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleView, defStyle, 0);

        mOff = a.getDrawable(R.styleable.ToggleView_backgroundOff);
        mOn = a.getDrawable(R.styleable.ToggleView_backgroundOn);
        mIsOn = a.getBoolean(R.styleable.ToggleView_initOn, false);
        if (mIsOn && mOn != null)
            setBackgroundDrawable(mOn);
        else if (!mIsOn && mOff != null)
            setBackgroundDrawable(mOff);

        a.recycle();
        
        this.setOnClickListener(this);
    }

    public void onClick(View v) {
        // toggle now
        setBackgroundDrawable(mIsOn ? mOff : mOn);
        mIsOn = mIsOn ? false : true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = mIsOn ? mOn : mOff;
        int widthSize = resolveSize(d.getIntrinsicWidth(), widthMeasureSpec);
        int heightSize = resolveSize(d.getIntrinsicHeight(), heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }
}