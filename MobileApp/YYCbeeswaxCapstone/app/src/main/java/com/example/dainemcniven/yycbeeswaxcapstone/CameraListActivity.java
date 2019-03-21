package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class CameraListActivity extends AppCompatActivity
{
    private String m_selectedCamera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameralist);

        // TODO: Somehow have a list of cameras somewhere that are available to be streamed?
        GetAvailableCameras();

    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            TextView sender = (TextView)v;
            int cam = Integer.parseInt(sender.getTag().toString());

            Log.e("a", "Camera: " + cam);
            Intent myIntent = new Intent(CameraListActivity.this, ShowCameraActivity.class);
            myIntent.putExtra("CAMERA_ID", cam); //Optional parameters
            CameraListActivity.this.startActivity(myIntent);
        }
    };


    private void GetAvailableCameras()
    {
        LinearLayout mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(mainLayout, layoutParams);

        for(int i = 0; i < 2; i++)
        {
            TextView cam0 = new TextView(this);
            if(i == 0)
                cam0.setText("Inside Camera");
            else
                cam0.setText("Outside Camera");
            cam0.setTextColor(Color.BLACK);
            cam0.setTextSize(24);
            cam0.setOnClickListener(myhandler1);
            cam0.setTag(i);
            linearLayout.addView(cam0);

            View v = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = 2;
            v.setBackgroundColor(Color.BLACK);
            v.setLayoutParams(params);
        }
    }
}
