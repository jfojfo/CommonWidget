package com.jfo.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class PopupMenuActivity extends Activity implements View.OnTouchListener {
    PopupMenu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);      

        setContentView(R.layout.template);
        findViewById(R.id.root).setOnTouchListener(this);
    }

    public boolean onTouch(View v, MotionEvent event) {
        int x = (int)event.getRawX();
        int y = (int)event.getRawY();
        if (menu == null) {
            String[] array = {"patpatpatpat", "ok", "another", "jfo"};
            int[] icons = {R.drawable.ic_tab_new, R.drawable.ic_tab_new, R.drawable.ic_tab_new, R.drawable.ic_tab_new};
            menu = new PopupMenu(this, R.style.PopupMenuStyle);
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
