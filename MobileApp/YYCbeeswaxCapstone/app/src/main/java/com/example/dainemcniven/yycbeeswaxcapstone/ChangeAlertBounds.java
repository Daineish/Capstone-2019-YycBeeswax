package com.example.dainemcniven.yycbeeswaxcapstone;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class ChangeAlertBounds extends AppCompatActivity
{
    // The Hive ID for communication with the database
    private int m_hive;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertbounds);

        // TODO: here you should be able to change alerts bounds and whatnot
        m_hive = Integer.parseInt(getIntent().getStringExtra("HIVE_ID").split(" ")[1]);
        GetAlertsForHive();

    }

    private void GetAlertsForHive()
    {
//        ArrayList<Integer> alerts = Database.getInstance().GetAlertsForHive(m_hive);
//        if(alerts.size() != 4)
//        {
//            // error
//        }
//        int humidValUB = alerts.get(0);
//        int humidValLB = alerts.get(1);
//        int tempValUB = alerts.get(2);
//        int tempValLB = alerts.get(3);
//
//        EditText hLB = (EditText) findViewById(R.id.humidLB); hLB.setText(String.valueOf(humidValLB));
//        EditText hUB = (EditText) findViewById(R.id.humidUB); hUB.setText(String.valueOf(humidValUB));
//        EditText tLB = (EditText) findViewById(R.id.tempLB); tLB.setText(String.valueOf(tempValLB));
//        EditText tUB = (EditText) findViewById(R.id.tempUB); tUB.setText(String.valueOf(tempValUB));

    }

    public void resetButtonClicked(View v)
    {
        GetAlertsForHive();
    }

    public void saveButtonClicked(View v)
    {
        // send data to database
    }

//    public void AlertsOnOffClicked(View v)
//    {
//        // TODO: turn alerts on or off in database depending on state
//        ToggleButton toggle = (ToggleButton) findViewById(R.id.alertsOnOff);
//        if(toggle.isChecked())
//        {
//            // turn alerts on
//            Log.e("a", "turning alerts on");
//        }
//        else
//        {
//            // turn alerts off
//            Log.e("a", "turning alerts off");
//        }
//    }
}
