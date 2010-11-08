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
import android.view.ViewGroup.LayoutParams;
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
    private final int mScreenHeight;
    
    public PopupMenu(Context context) {
        super(context, null, 0); // don't use android.R.attr.popupWindowStyle ==> will introduce a background
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mMenu = mInflater.inflate(R.layout.popup_menu_layout, null);
        mList = (ListView) mMenu.findViewById(R.id.popup_content);
        mAdapter = new PopupMenuItemAdapter();
        mList.setAdapter(mAdapter);

        setContentView(mMenu);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        // dismiss this popup menu when touch outside of the menu area
        setOutsideTouchable(true);
        mMenu.setOnTouchListener(this);
        
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        // the origin is the left top corner is (0,0).
        // the left bottom corner of popup menu is (x0,y0), the popup point is (x1,y1).
        // then setOffset(x1-x0, y1-y0)
        setOffset(37, -10);
    }
    
    /*
    public PopupMenu(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public PopupMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PopupMenu() {
        this(null, 0, 0);
    }

    public PopupMenu(View contentView) {
        this(contentView, 0, 0);
    }

    public PopupMenu(int width, int height) {
        this(null, width, height);
    }

    public PopupMenu(View contentView, int width, int height) {
        this(contentView, width, height, false);
    }

    public PopupMenu(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }
    */
    
    // this is called when the touch event is not dealt with by any child
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, "click outside of PopupMenu, dismiss the PopupMenu");
        dismiss();
        return false;
    }

    /**
     * <p>Show this popup menu at absolute position (x,y),
     * the top left corner coordination is (0,0).
     * </p>
     */
    public void showAt(View parent, int x, int y) {
        y = mScreenHeight - y;
        this.showAtLocation(parent, Gravity.LEFT | Gravity.BOTTOM, x - mOffsetX, y + mOffsetY);
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