package com.example.dainemcniven.yycbeeswaxcapstone;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

        // TODO: Access the database or whatever and list what cameras are available for streaming
        GetAvailableCameras();

    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            EditText sender = (EditText)v;
            String dbSensor = sender.getText().toString();
            // TODO: Open up stream or something
            Log.e("a", "Camera: " + dbSensor);
        }
    };


    private void GetAvailableCameras()
    {
        ArrayList<String> sensors = Database.getInstance().GetCameras();

        LinearLayout mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(mainLayout, layoutParams);

        for(int i = 0; i < sensors.size(); i++)
        {
            EditText ed = new EditText(this);
            ed.setText(sensors.get(i));
            ed.setInputType(0);
            ed.setOnClickListener(myhandler1);
            linearLayout.addView(ed);
        }

    }
}
