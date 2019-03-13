package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
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

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class SensorListActivity extends AppCompatActivity
{
    private String m_selectedSensor = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        GetAvailableSensors();

        // access the database and see what sensor data is available,
        // have some sort of list or something? then when you click on one you
        // can see a list of the data or something?
    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            EditText sender = (EditText)v;
            String hivenum = sender.getTag().toString();

            Log.e("a", "Hive: " + hivenum);
            Intent myIntent = new Intent(SensorListActivity.this, SensorDetailsActivity.class);
            myIntent.putExtra("HIVE_ID", hivenum); //Optional parameters
            SensorListActivity.this.startActivity(myIntent);
        }
    };

    private void GetAvailableSensors()
    {
        ResultSet hives = Database.getInstance().GetHives();

        LinearLayout mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(mainLayout, layoutParams);

        try
        {
            while(hives.next())
            {
                int hiveID = hives.getInt("HiveId");
                TextView hive = new TextView(this);
                hive.setText("Hive: " + hiveID);
                hive.setOnClickListener(myhandler1);
                hive.setTag(hiveID);
                linearLayout.addView(hive);

                View v = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = 2;
                v.setBackgroundColor(Color.BLACK);
                v.setLayoutParams(params);
            }
        }
        catch(Exception e)
        {}


    }
}