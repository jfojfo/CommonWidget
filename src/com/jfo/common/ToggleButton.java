package com.jfo.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class ToggleButton extends View implements View.OnClickListener {
    protected Drawable mOn, mOff;
    protected boolean mIsOn;
    
    public ToggleButton(Context context) {
        super(context);
    }
    
    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public ToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ToggleButton, defStyle, 0);

        mOff = a.getDrawable(R.styleable.ToggleButton_backgroundOff);
        mOn = a.getDrawable(R.styleable.ToggleButton_backgroundOn);
        mIsOn = a.getBoolean(R.styleable.ToggleButton_initOn, false);
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
        int widthSize = 0;
        int heightSize = 0;
        if (d != null) {
            widthSize = resolveSize(d.getIntrinsicWidth(), widthMeasureSpec);
            heightSize = resolveSize(d.getIntrinsicHeight(), heightMeasureSpec);
        }
        setMeasuredDimension(widthSize, heightSize);
    }
}
