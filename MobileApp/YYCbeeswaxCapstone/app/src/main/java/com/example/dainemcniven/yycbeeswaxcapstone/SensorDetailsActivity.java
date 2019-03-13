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
import android.view.ViewManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class SensorDetailsActivity extends AppCompatActivity
{
    private int m_selectedHive;
    private LinearLayout m_mainLayout = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensordetails);
        m_selectedHive = Integer.parseInt(getIntent().getStringExtra("HIVE_ID"));

        // TODO: default is 24hrs up until now?
        java.util.Date curDate = new java.util.Date();
        Date end = new java.sql.Date(curDate.getTime());
        GregorianCalendar cal = new GregorianCalendar(); cal.setTime(end); cal.add(Calendar.DAY_OF_YEAR, -1);
        Date start = new java.sql.Date(cal.getTime().getTime());
        GetAvailableSensors(start, end);

        // access the database and see what sensor data is available,
        // have some sort of list or something? then when you click on one you
        // can see a list of the data or something?
    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {

        }
    };


    private void GetAvailableSensors(java.sql.Date start, java.sql.Date end)
    {
        // TODO: Default dates to show data for? (currently 24hours ago until now)
        ResultSet sensorsT = Database.getInstance().GetSensorsData(m_selectedHive, start, end, "Temperature");
        ResultSet sensorsH = Database.getInstance().GetSensorsData(m_selectedHive, start, end, "Humidity");

        if(m_mainLayout != null)
        {
            ((ViewManager)m_mainLayout.getParent()).removeView(m_mainLayout); // or something
        }
        m_mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        m_mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(m_mainLayout, layoutParams);

        try
        {
            TextView tempTitle = new TextView(this);
            tempTitle.setText("Temperatures");
            tempTitle.setTextSize(24);
            linearLayout.addView(tempTitle);
            while(sensorsT.next())
            {
                int hiveID = sensorsT.getInt("HiveId"); // ?
                TextView hive = new TextView(this);
                hive.setText("\tHive: " + hiveID);
                hive.setOnClickListener(myhandler1);
                hive.setTag(hiveID);
                linearLayout.addView(hive);
            }
            View v = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = 2;
            v.setBackgroundColor(Color.BLACK);
            v.setLayoutParams(params);

            TextView humidTitle = new TextView(this);
            humidTitle.setText("Humidity");
            humidTitle.setTextSize(24);
            linearLayout.addView(humidTitle);
            while(sensorsH.next())
            {
                int hiveID = sensorsT.getInt("HiveId"); // ?
                TextView hive = new TextView(this);
                hive.setText("\tHive: " + hiveID);
                hive.setOnClickListener(myhandler1);
                hive.setTag(hiveID);
                linearLayout.addView(hive);
            }
        }
        catch(Exception e)
        {}
    }

    public void updateButtonClicked(View v)
    {
        // get calendar date
        CalendarView calV = (CalendarView) findViewById(R.id.calendarView);
        java.sql.Date start = new java.sql.Date(calV.getDate());
        GregorianCalendar gCal = new GregorianCalendar();
        gCal.setTime(start); gCal.add(Calendar.DAY_OF_YEAR, 1);
        Date end = new java.sql.Date(gCal.getTime().getTime());
        GetAvailableSensors(start, end);
    }
}