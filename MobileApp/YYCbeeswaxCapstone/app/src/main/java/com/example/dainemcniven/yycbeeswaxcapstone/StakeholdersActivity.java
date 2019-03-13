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

public class StakeholdersActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stakeholders);

        GetStakeholders();

    }

    private void GetStakeholders()
    {

    }
}
