package com.jfo.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener {
    Button mBtn1, mBtn2, mBtn3, mBtn4;
    PopupMenu menu;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        findViewById(R.id.root).setOnTouchListener(this);
        mBtn1 = (Button) findViewById(R.id.Button01);
        mBtn1.setOnClickListener(this);
        mBtn2 = (Button) findViewById(R.id.Button02);
        mBtn2.setOnClickListener(this);
        mBtn3 = (Button) findViewById(R.id.Button03);
        mBtn3.setOnClickListener(this);
        mBtn4 = (Button) findViewById(R.id.Button04);
        mBtn4.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.Button01: {
            Intent intent = new Intent(this, TabBottomActivity.class);
            startActivity(intent);
            finish();
            break;
        }
        case R.id.Button02: {
            Intent intent = new Intent(this, TabHeaderActivity.class);
            startActivity(intent);
            finish();
            break;
        }
        case R.id.Button03: {
            if (menu != null) {
                menu.showAsDropDown(v, 0, 0);
//                menu.showAtLocation(v, Gravity.NO_GRAVITY, 0, v.getTop() + 20);
            }
            break;
        }
        case R.id.Button04: {
            LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View t = inflater.inflate(R.layout.tmp, null);
            PopupWindow w = new PopupWindow(t, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            w.showAtLocation(v, Gravity.NO_GRAVITY, 0, 475);
            break;
        }
        default:
            break;
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
//        int h = getWindowManager().getDefaultDisplay().getHeight();
//        y = h - y;
        if (menu == null) {
            String[] array = {"patpatpatpat", "ok", "another", "jfo"};
            int[] icons = {R.drawable.ic_tab_new, R.drawable.ic_tab_new, R.drawable.ic_tab_new, R.drawable.ic_tab_new};
            menu = new PopupMenu(this);
            menu.setTexts(array);
            //menu.setTexts(R.array.myarray);
            //menu.setIcons(icons);
            //menu.setIcons(R.array.myicon);
            menu.setWidth(200);
            menu.showAt(v, x, y);
        }
        else {
            menu.showAt(v, x, y);
        }
        Toast.makeText(this, "Show at:" + x + "," + y, Toast.LENGTH_SHORT).show();
        return false;
    }
}