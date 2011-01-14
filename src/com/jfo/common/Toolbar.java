package com.jfo.common;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class Toolbar extends FrameLayout {
    public static final int BUTTON_LEFT = 1;
    public static final int BUTTON_RIGHT = 2;
    public static final int BUTTON_FULL = 3;
    
    private Button mBtnLeft, mBtnRight, mBtnFull;
    private Message mBtnLeftMessage, mBtnRightMessage, mBtnFullMessage;
    private Handler mHandler;

    
    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Toolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.toolbar_layout, this, // we are the parent
                true);
        
        mBtnLeft = (Button) this.findViewById(R.id.toolbar_btn_left);
        mBtnRight = (Button) this.findViewById(R.id.toolbar_btn_right);
        mBtnFull = (Button) this.findViewById(R.id.toolbar_btn);
        
        setButtonVisibility(new int[] { View.GONE, View.GONE, View.GONE });

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyle, 0);

        CharSequence text;
        text = a.getText(R.styleable.Toolbar_textLeftButton);
        if (text != null && mBtnLeft != null)
            setLeftButton(text, null);
        text = a.getText(R.styleable.Toolbar_textRightButton);
        if (text != null && mBtnRight != null)
            setRightButton(text, null);
        text = a.getText(R.styleable.Toolbar_textFullButton);
        if (text != null && mBtnFull != null)
            setFullButton(text, null);
        
        a.recycle();
        
        mHandler = new ButtonHandler(this);
        mBtnLeft.setOnClickListener(mOnBtnClick);
        mBtnRight.setOnClickListener(mOnBtnClick);
        mBtnFull.setOnClickListener(mOnBtnClick);
    }
    
    public void setLeftButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_LEFT, text, listener);
    }
    
    public void setRightButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_RIGHT, text, listener);        
    }

    public void setFullButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_FULL, text, listener);
    }
    
    public void setLeftButton(final OnClickListener listener) {
        CharSequence text = mBtnLeft.getText();
        setButton(BUTTON_LEFT, text, listener);
    }

    public void setRightButton(final OnClickListener listener) {
        CharSequence text = mBtnRight.getText();
        setButton(BUTTON_RIGHT, text, listener);        
    }
    
    public void setFullButton(final OnClickListener listener) {
        CharSequence text = mBtnFull.getText();
        setButton(BUTTON_FULL, text, listener);
    }
    
    public void setButton(int which, CharSequence text, final OnClickListener listener) {
        if (text == null)
            return;

        Message msg = null;
        if (listener != null)
            msg = mHandler.obtainMessage(which, listener);

        switch (which) {
        case BUTTON_LEFT: {
            mBtnLeft.setText(text);
            mBtnLeftMessage = msg;
            setButtonVisibility(new int[] { View.VISIBLE, View.VISIBLE, View.GONE });
            break;
        }
        case BUTTON_RIGHT: {
            mBtnRight.setText(text);
            mBtnRightMessage = msg;
            setButtonVisibility(new int[] { View.VISIBLE, View.VISIBLE, View.GONE });
            break;
        }
        case BUTTON_FULL: {
            mBtnFull.setText(text);
            mBtnFullMessage = msg;
            setButtonVisibility(new int[] { View.GONE, View.GONE, View.VISIBLE });
            break;
        }
        default:
            break;
        }
    }

    private void setButtonVisibility(int visible[]) {
        mBtnLeft.setVisibility(visible[0]);
        mBtnRight.setVisibility(visible[1]);
        mBtnFull.setVisibility(visible[2]);
    }
    
    public Button getLeftButton() {
        return getButton(BUTTON_LEFT);
    }
    
    public Button getRightButton() {
        return getButton(BUTTON_RIGHT);
    }
    
    public Button getFullButton() {
        return getButton(BUTTON_FULL);
    }
    
    public Button getButton(int which) {
        switch (which) {
        case BUTTON_LEFT:
            return mBtnLeft;
        case BUTTON_RIGHT:
            return mBtnRight;
        case BUTTON_FULL:
            return mBtnFull;
        }
        return null;
    }
    
    private View.OnClickListener mOnBtnClick = new View.OnClickListener() {
        public void onClick(View v) {
            Message m = null;
            if (v == mBtnLeft && mBtnLeftMessage != null) {
                m = Message.obtain(mBtnLeftMessage);
            } else if (v == mBtnRight && mBtnRightMessage != null) {
                m = Message.obtain(mBtnRightMessage);
            } else if (v == mBtnFull && mBtnFullMessage != null) {
                m = Message.obtain(mBtnFullMessage);
            }
            if (m != null) {
                m.sendToTarget();
            }
        }
    };

    
    private static final class ButtonHandler extends Handler {
        
        private WeakReference<Toolbar> mToolbar;

        public ButtonHandler(Toolbar toolbar) {
            mToolbar = new WeakReference<Toolbar>(toolbar);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Toolbar.BUTTON_LEFT:
                case Toolbar.BUTTON_RIGHT:
                case Toolbar.BUTTON_FULL:
                    ((Toolbar.OnClickListener) msg.obj).onClick(mToolbar.get(), msg.what);
                    break;
            }
        }
    }

    public interface OnClickListener {
        void onClick(Toolbar toolbar, int which);
    }
}
