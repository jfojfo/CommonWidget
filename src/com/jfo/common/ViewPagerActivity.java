package com.jfo.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

public class ViewPagerActivity extends Activity {
    private ViewPager mViewPager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_layout);
        
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        MyPagerAdapter adapter = new MyPagerAdapter(this);
        mViewPager.setAdapter(adapter);
    }
    
    private class MyPagerAdapter extends PagerAdapter {
        private Context context;
        private String[] items = {
                "aaa",
                "bbb",
                "ccc",
                "ddd",
        };
        
        public MyPagerAdapter(Context ctx) {
            context = ctx;
        }
        
        @Override
        public void destroyItem(View collection, int position, Object view) {
            ViewPager pager = (ViewPager) collection;
            pager.removeView((TextView) view);
        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object instantiateItem(View collection, int position) {
            ViewPager pager = (ViewPager) collection;
            TextView tv = new TextView(context);
            tv.setText(items[position] + " " + formatTimestamp2Date(System.currentTimeMillis(), null));
            tv.setTextSize(30);
            tv.setTextColor(Color.BLUE);
            pager.addView(tv);
            return tv;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    public static String formatTimestamp2Date(long timeStamp, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        if (format == null || format.length() == 0)
            format = "yyyy-MM-dd HH:mm";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

}