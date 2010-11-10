package com.jfo.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

public class PopupMenuActivity extends Activity 
        implements View.OnTouchListener, AdapterView.OnItemClickListener {
    
    PopupMenu menu;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      

        setContentView(R.layout.template);
        findViewById(R.id.root).setOnTouchListener(this);
        btn = (Button) findViewById(R.id.Button01);
        btn.setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        if (menu == null) {
            String[] array = {"patpatpatpat", "ok", "another", "jfo"};
            int[] icons = {R.drawable.ic_tab_new, R.drawable.ic_tab_new, R.drawable.ic_tab_new, R.drawable.ic_tab_new};
            menu = new PopupMenu(this);
            menu.setTexts(array);
            //menu.setTexts(R.array.myarray);
            //menu.setIcons(icons);
            //menu.setIcons(R.array.myicon);
            menu.setWidth(200);
            menu.setOnItemClickListener(this);
        }
        if (v == btn)
            //menu.showCenterAbove(v);
            //menu.showAbove(v, 20, -10);
            menu.showAbove(v, 0.5, 0.5);
        else
            menu.showAt(v, x, y);
        Toast.makeText(this, "Show at:" + x + "," + y, Toast.LENGTH_SHORT).show();
        return false;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "clicked:" + position, Toast.LENGTH_SHORT).show();
        menu.dismiss();
    }

}
