package com.jfo.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupMenu extends PopupWindow implements View.OnTouchListener {
    private final static String TAG = "PopupMenu";
    private Context mContext;
    private LayoutInflater mInflater;
    private View mMenu;
    private ListView mList;
    private PopupMenuItemAdapter mAdapter;
    private int mOffsetX = 0, mOffsetY = 0;
    private final int mScreenHeight, mScreenWidth;
    private View mBottomView;
    private int mBottomPadding = 15;
    private int mBottomViewWidth;

    public PopupMenu(Context context) {
        this(context, 0);
    }
    
    public PopupMenu(Context context, int styleRes) {
        // don't use android.R.attr.popupWindowStyle for styleAttr ==> will introduce a background
        //super(context, null, android.R.attr.popupWindowStyle);
        super(context, null, 0);
        
        if (styleRes == 0)
            styleRes = R.style.PopupMenuStyle;
        TypedArray a = context.obtainStyledAttributes(
                null, R.styleable.PopupMenu, 0, styleRes);

        Drawable bkgContent = a.getDrawable(R.styleable.PopupMenu_popupContent);
        Drawable bkgBottom = a.getDrawable(R.styleable.PopupMenu_popupBottom);
        int animStyle = a.getResourceId(R.styleable.PopupMenu_animStyle, R.style.Animation_PopupMenu);
        int offsetX = a.getDimensionPixelOffset(R.styleable.PopupMenu_popupOffsetX, 0);
        int offsetY = a.getDimensionPixelOffset(R.styleable.PopupMenu_popupOffsetY, 0);
        int overlay = a.getDimensionPixelSize(R.styleable.PopupMenu_overlay, 0);
        int padding = a.getDimensionPixelOffset(R.styleable.PopupMenu_padding, 0);
        
        a.recycle();

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mMenu = mInflater.inflate(R.layout.popup_menu_layout, null);
        
        mList = (ListView) mMenu.findViewById(R.id.popup_content);
        mList.setBackgroundDrawable(bkgContent);
        mAdapter = new PopupMenuItemAdapter();
        mList.setAdapter(mAdapter);
        
        mBottomView = mMenu.findViewById(R.id.popup_bottom);
        mBottomView.setBackgroundDrawable(bkgBottom);
        mBottomPadding = padding;
        mBottomViewWidth = bkgBottom.getIntrinsicWidth();
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)mBottomView.getLayoutParams();
        lp.topMargin = overlay;
        mBottomView.setLayoutParams(lp);

        setContentView(mMenu);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        // dismiss this popup menu when touch outside of the menu area
        setOutsideTouchable(true);
        mMenu.setOnTouchListener(this);

        setAnimationStyle(animStyle);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        
        // the origin is the left top corner is (0,0).
        // the left bottom corner of popup menu is (x0,y0), the popup point is (x1,y1).
        // then setOffset(x1-x0, y1-y0)
        setOffset(offsetX, offsetY);
        
        // if not set to true, ListView will not respond to onItemClick
        setFocusable(true);
    }

    // this is called when the touch event is not dealt with by any child
    public boolean onTouch(View v, MotionEvent event) {
//        final int x = (int) event.getX();
//        final int y = (int) event.getY();
//
//        if ((event.getAction() == MotionEvent.ACTION_DOWN)
//                && ((x < 0) || (x >= getWidth()) || (y < 0) || (y >= getHeight()))) {
//            dismiss();
//            return true;
//        } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//            dismiss();
//            Log.d(TAG, "click outside of PopupMenu, dismiss the PopupMenu");
//            return true;
//        } else {
//            return false;
//        }
        Log.d(TAG, "click outside of PopupMenu, dismiss the PopupMenu");
        dismiss();
        return false;
    }

    /**
     * <p>Show this popup menu at absolute position (x,y),
     * the top left corner coordination is (0,0).
     * </p>
     */
    public void showAt(View parent, int x, int y) {
        int[] v = adjust(x, y);
        x = v[0];
        y = v[1];
        int margin = v[2];
        
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)mBottomView.getLayoutParams();
        lp.leftMargin = margin;
        mBottomView.setLayoutParams(lp);
        
        showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, x, y);
    }
    
    /**
     * <p>Show this popup menu at the left top corner of parent view.</p>
     */
    public void showAbove(View parent) {
        showAbove(parent, 0, 0);
    }

    /**
     * <p>Show this popup menu at the center top of parent view.</p>
     */
    public void showCenterAbove(View parent) {
        if (parent != null) {
            int w = parent.getWidth();
            if (w >= 0)
                showAbove(parent, w / 2, 0);
        }
    }

    /**
     * <p>Show this popup menu above parent view, 
     * offset to xp percent of parent view's width,
     * and yp percent of height.
     * The left top corner is (0,0).</p>
     * 
     * @param parent parent view
     * @param xp percent of offset to parent's width in horizontal
     * @param yp percent of offset to parent's height in vertical
     */
    public void showAbove(View parent, double xp, double yp) {
        if (parent != null) {
            int w = parent.getWidth() < 0 ? 0 : parent.getWidth();
            int h = parent.getHeight() < 0 ? 0 : parent.getHeight();
            int dw = (int)(w * xp);
            int dh = (int)(h * yp);
            showAbove(parent, dw, dh);
        }
    }

    /**
     * <p>Show this popup menu above parent view, with (dx,dy) offset, 
     * the left top corner is (0,0).</p>
     * 
     * @param parent parent view
     * @param dx offset in x coordination
     * @param dy offset in y coordination
     */
    public void showAbove(View parent, int dx, int dy) {
        if (parent != null) {
            int[] location = { 0, 0 };
            parent.getLocationOnScreen(location);
            showAt(parent, location[0] + dx, location[1] + dy);
        }
    }
    
    private int[] adjust(int x, int y) {
        y = mScreenHeight - y;
        y += mOffsetY;
        
        int w = getWidth();
        if (w < 0)
            w = mScreenWidth;
        int margin = mBottomPadding;
        int left = x - w / 2;
        int right = x + w / 2;
        if (left < 0) {
            x = 0;
            margin = w / 2 + left - mOffsetX;
        }
        else if (right > mScreenWidth) {
            x = mScreenWidth - w;
            margin = w / 2 + (right - mScreenWidth) - mOffsetX;
        }
        else {
            x = left;
            margin = w / 2 - mOffsetX;
        }

        if (margin < mBottomPadding)
            margin = mBottomPadding;
        else if (margin + mBottomViewWidth + mBottomPadding > w)
            margin = w - mBottomPadding - mBottomViewWidth;
        
        int[] v = {x, y, margin};
        return v;
    }
    
    public void setOffset(int dx, int dy) {
        mOffsetX = dx;
        mOffsetY = dy;
    }

    public ListView getListView() {
        return mList;
    }
    
    public void setTexts(CharSequence[] texts) {
        mAdapter.setTexts(texts);
    }

    public void setTexts(int arrayID) {
        CharSequence[] texts = mContext.getResources().getTextArray(arrayID);
        setTexts(texts);
    }
    
    public void setTexts(int[] textIDs) {
        if (textIDs != null) {
            int N = textIDs.length;
            String[] texts = new String[N];
            for (int i = 0; i < N; i++)
                texts[i] = mContext.getResources().getString(textIDs[i]);
            setTexts(texts);
        }
    }

    public void setIcons(Drawable[] icons) {
        mAdapter.setIcons(icons);
    }

    public void setIcons(int arrayID) {
        TypedArray ar = mContext.getResources().obtainTypedArray(arrayID);
        int N = ar.length();
        int icons[] = new int[N];
        for (int i = 0; i < N; i++) {
            int id = ar.getResourceId(i, 0);
            icons[i] = id;
        }
        setIcons(icons);
    }
    
    public void setIcons(int[] iconIDs) {
        if (iconIDs != null) {
            int N = iconIDs.length;
            Drawable[] icons = new Drawable[N];
            for (int i = 0; i < N; i++)
                icons[i] = mContext.getResources().getDrawable(iconIDs[i]);
            setIcons(icons);
        }
    }
    
    public void setOnItemClickListener(AdapterView.OnItemClickListener l) {
        mList.setOnItemClickListener(l);
    }

    private class PopupMenuItemAdapter extends BaseAdapter {
        private class Data {
            CharSequence text;
            Drawable icon;
            public Data(CharSequence s, Drawable d) {
                text = s;
                icon = d;
            }
        }
        private CharSequence[] mTextItems = null;
        private Drawable[] mIconItems = null;
        
        public void setTexts(CharSequence[] texts) {
            mTextItems = texts;
        }
        
        public void setIcons(Drawable[] icons) {
            mIconItems = icons;
        }

        public int getCount() {
            return (mTextItems != null ? 
                        mTextItems.length :
                        (mIconItems != null ? mIconItems.length : 0));
        }

        public Object getItem(int position) {
            CharSequence t = (mTextItems != null ? mTextItems[position] : null);
            Drawable d = (mIconItems != null ? mIconItems[position] : null);
            Data data = new Data(t, d);
            return data;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.popup_menu_item, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            TextView text = (TextView) convertView.findViewById(R.id.text);
            Data d = (Data) getItem(position);
            icon.setImageDrawable(d.icon);
            text.setText(d.text);
            return convertView;
        }
    }

}
