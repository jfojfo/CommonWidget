package com.jfo.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends Activity implements OnClickListener {
    Button mBtn1, mBtn2, mBtn3, mBtn4;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
            Intent intent = new Intent(this, PopupMenuActivity.class);
            startActivity(intent);
            finish();
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

}