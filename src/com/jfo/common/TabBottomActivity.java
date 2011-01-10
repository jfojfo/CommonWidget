package com.jfo.common;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class TabBottomActivity extends TabActivity {
    TabHost mTabHost;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setContentView(R.layout.tab_bottom);
        mTabHost = getTabHost();
        setupTabs();
    }
    
    private void setupTabs() {
        getTabWidget().setDividerDrawable(R.drawable.tab_bottom_rounded_divider);

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);

        TabHost.TabSpec tab = mTabHost.newTabSpec("tab1");
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_bottom_indicator_left, null);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1 a long text test");
        ((ImageView)tabView.findViewById(android.R.id.icon)).setImageResource(R.drawable.ic_tab_new);

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);

        tab = mTabHost.newTabSpec("tab2");
        tabView = LayoutInflater.from(this).inflate(R.layout.tab_bottom_indicator_middle, null);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1");
        ((ImageView)tabView.findViewById(android.R.id.icon)).setImageResource(R.drawable.ic_tab_new);

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);

        tab = mTabHost.newTabSpec("tab22");
        tabView = LayoutInflater.from(this).inflate(R.layout.tab_bottom_indicator_middle, null);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1");
        ((ImageView)tabView.findViewById(android.R.id.icon)).setImageResource(R.drawable.ic_tab_new);

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);

        tab = mTabHost.newTabSpec("tab3");
        tabView = LayoutInflater.from(this).inflate(R.layout.tab_bottom_indicator_right, null);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1");
        ((ImageView)tabView.findViewById(android.R.id.icon)).setImageResource(R.drawable.ic_tab_new);

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);
    }
}