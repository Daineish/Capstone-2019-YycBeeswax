package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by dainemcniven on 2019-03-05.
 */

public class WelcomeScreen extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


    }

    public void camerasClicked(View v)
    {
        Log.e("note", "camera clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, CameraListActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void sensorsClicked(View v)
    {
        Log.e("note", "sensors clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, SensorListActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void alertsClicked(View v)
    {
        Log.e("note", "alert clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, AlertsActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void hivesClicked(View v)
    {
        Log.e("note", "hives clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, HiveListActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void stClicked(View v)
    {
        Log.e("note", "stakeholders clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, HiveListActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }
}
