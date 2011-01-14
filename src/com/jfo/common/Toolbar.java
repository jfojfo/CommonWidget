package com.jfo.common;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class Toolbar extends FrameLayout {
    /**
     * The identifier for the left button.
     */
    public static final int BUTTON_LEFT = 1;
    /**
     * The identifier for the right button.
     */
    public static final int BUTTON_RIGHT = 2;
    /**
     * The identifier when only one button exists.
     */
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
    
    /**
     * Set left button of Toolbar, text & listener.
     * 
     * @param text The text to display in left button.
     * @param listener Callback listener, {@link Toolbar.OnClickListener}.
     */
    public void setLeftButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_LEFT, text, listener);
    }
    
    /**
     * Set right button of Toolbar, text & listener.
     * 
     * @param text The text to display in right button.
     * @param listener Callback listener, {@link Toolbar.OnClickListener}.
     */
    public void setRightButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_RIGHT, text, listener);        
    }

    /**
     * Set full button(when only one button exists) of Toolbar, text & listener.
     * 
     * @param text The text to display in button.
     * @param listener Callback listener, {@link Toolbar.OnClickListener}.
     */
    public void setFullButton(CharSequence text, final OnClickListener listener) {
        setButton(BUTTON_FULL, text, listener);
    }
    
    /**
     * Set listener for left button of Toolbar.
     * 
     * @param listener Callback listener, {@link Toolbar.OnClickListener}.
     */
    public void setLeftButton(final OnClickListener listener) {
        CharSequence text = mBtnLeft.getText();
        setButton(BUTTON_LEFT, text, listener);
    }

    /**
     * Set listener for right button of Toolbar.
     * 
     * @param listener Callback listener, {@link Toolbar.OnClickListener}.
     */
    public void setRightButton(final OnClickListener listener) {
        CharSequence text = mBtnRight.getText();
        setButton(BUTTON_RIGHT, text, listener);        
    }
    
    /**
     * Set listener for only one button of Toolbar.
     * 
     * @param listener Callback listener, {@link Toolbar.OnClickListener}.
     */
    public void setFullButton(final OnClickListener listener) {
        CharSequence text = mBtnFull.getText();
        setButton(BUTTON_FULL, text, listener);
    }
    
    /**
     * Set the text and listener to be invoked when corresponding button of the toolbar is pressed.
     * 
     * @param which Which button to set the text and listener on, can be one of
     *            {@link Toolbar#BUTTON_LEFT},
     *            {@link Toolbar#BUTTON_RIGHT}, or
     *            {@link Toolbar#BUTTON_FULL}
     * @param text The text to display in the button.
     * @param listener The {@link Toolbar.OnClickListener} to use.
     */
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
    
    /**
     * @return The left button of Toolbar.
     */
    public Button getLeftButton() {
        return getButton(BUTTON_LEFT);
    }
    
    /**
     * @return The right button of Toolbar.
     */
    public Button getRightButton() {
        return getButton(BUTTON_RIGHT);
    }
    
    /**
     * @return The only one button that exists in Toolbar.
     */
    public Button getFullButton() {
        return getButton(BUTTON_FULL);
    }
    
    /**
     * @param which Which button to return. Can be one of
     *            {@link Toolbar#BUTTON_LEFT},
     *            {@link Toolbar#BUTTON_RIGHT}, or
     *            {@link Toolbar#BUTTON_FULL}
     * 
     * @return The corresponding button of Toolbar.
     */
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

    /**
     * Interface used to allow the creator of a toolbar to run some code when a
     * button on the toolbar is clicked.
     */
    public interface OnClickListener {
        /**
         * This method will be invoked when a button in the toolbar is clicked.
         * 
         * @param toolbar The toolbar that received the click.
         * @param which The button that was clicked (
         * {@link Toolbar#BUTTON_LEFT}, {@link Toolbar#BUTTON_RIGHT}, {@link Toolbar#BUTTON_FULL}).
         */
        void onClick(Toolbar toolbar, int which);
    }
}
