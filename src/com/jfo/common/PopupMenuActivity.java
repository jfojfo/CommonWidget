package com.jfo.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PopupMenuActivity extends Activity implements OnClickListener {
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
            break;
        }
        case R.id.Button02: {
            break;
        }
        case R.id.Button03: {
            break;
        }
        case R.id.Button04: {
            break;
        }
        default:
            break;
        }
    }
}