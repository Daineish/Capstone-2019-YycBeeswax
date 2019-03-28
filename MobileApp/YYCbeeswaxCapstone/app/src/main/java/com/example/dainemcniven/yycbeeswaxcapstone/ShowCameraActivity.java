package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class ShowCameraActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcamera);
        int cam = getIntent().getIntExtra("CAMERA_ID", -1);

        WebView wb = (WebView) findViewById(R.id.webView1);

        // TODO: Change this to the IP or youtube link
        switch(cam)
        {
            case 0:
                wb.loadUrl("http://www.google.com"); // TODO: Replace with youtube stream
                break;
            case 1:
                wb.loadUrl("http://yahoo.com"); // TODO: see above
                break;
        }

    }
}


