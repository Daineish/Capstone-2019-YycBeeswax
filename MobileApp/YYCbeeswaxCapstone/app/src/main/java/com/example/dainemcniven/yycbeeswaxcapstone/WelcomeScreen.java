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

    public void camerasTextClicked(View v)
    {
        Log.e("note", "camera clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, CameraListActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void sensorsTextClicked(View v)
    {
        Log.e("note", "sensors clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, SensorListActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void alertsTextClicked(View v)
    {
        Log.e("note", "alers clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, AlertsActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }

    public void otherTextClicked(View v)
    {
        Log.e("note", "other clicked");
        Intent myIntent = new Intent(WelcomeScreen.this, OtherActivity.class);
        myIntent.putExtra("key", ""); //Optional parameters
        WelcomeScreen.this.startActivity(myIntent);
    }
}
