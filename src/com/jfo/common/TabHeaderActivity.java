package com.jfo.common;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

public class TabHeaderActivity extends TabActivity {
    TabHost mTabHost;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFormat(PixelFormat.RGBA_8888);

        setContentView(R.layout.tab_header);
        mTabHost = getTabHost();
        setupTabs();
    }
    
    private void setupTabs() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);

        TabHost.TabSpec tab = mTabHost.newTabSpec("tab1");
        View tabView = LayoutInflater.from(this).inflate(R.layout.tab_header_indicator, null);
        tabView.setBackgroundResource(R.drawable.tab_header_left);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1");

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);

        tab = mTabHost.newTabSpec("tab2");
        tabView = LayoutInflater.from(this).inflate(R.layout.tab_header_indicator, null);
        tabView.setBackgroundResource(R.drawable.tab_header_middle);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1");

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);

        tab = mTabHost.newTabSpec("tab3");
        tabView = LayoutInflater.from(this).inflate(R.layout.tab_header_indicator, null);
        tabView.setBackgroundResource(R.drawable.tab_header_right);
        ((TextView)tabView.findViewById(android.R.id.title)).setText("tab1");

        tab.setIndicator(tabView);
        tab.setContent(intent);
        mTabHost.addTab(tab);
    }
}