package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Intent;
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

public class AlertsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        // TODO: here you should be able to change alerts bounds and whatnot
        GetAvailableHives();

    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            EditText sender = (EditText)v;
            String hivenum = sender.getText().toString();
            // TODO: get database data for dbSensor and show it somehow
            Log.e("a", "Hive: " + hivenum);
            Intent myIntent = new Intent(AlertsActivity.this, ChangeAlertBounds.class);
            myIntent.putExtra("HIVE_ID", hivenum); //Optional parameters
            AlertsActivity.this.startActivity(myIntent);
        }
    };


    private void GetAvailableHives()
    {
        ArrayList<String> sensors = Database.getInstance().GetHives();

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
