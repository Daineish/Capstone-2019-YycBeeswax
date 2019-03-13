package com.example.dainemcniven.yycbeeswaxcapstone;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HiveInfoActivity extends AppCompatActivity
{
    private int m_selectedHive = -1;
    private EditText m_hiveId;
    private EditText m_location;
    private EditText m_owner;
    private EditText m_tempLB;
    private EditText m_tempUB;
    private EditText m_humidLB;
    private EditText m_humidUB;
    private EditText m_blockTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertbounds);
        m_selectedHive = Integer.parseInt(getIntent().getStringExtra("HIVE_ID"));
        if(m_selectedHive == -1)
        {
            //TODO: error
        }
        m_hiveId = (EditText) findViewById(R.id.hiveText);
        m_location = (EditText) findViewById(R.id.locationText);
        m_owner = (EditText) findViewById(R.id.ownerText);
        m_tempLB = (EditText) findViewById(R.id.tempLB);
        m_tempUB = (EditText) findViewById(R.id.tempUB);
        m_humidLB = (EditText) findViewById(R.id.humidLB);
        m_humidUB = (EditText) findViewById(R.id.humidUB);
        m_blockTime = (EditText) findViewById(R.id.blockTimeText);

        // TODO: Access the database or whatever and list what hives are available
        ShowHiveInfo();

    }

    private void ShowHiveInfo()
    {
        ResultSet hives = Database.getInstance().GetHives();

        try
        {
            while(hives.next())
            {
                int hiveVal = hives.getInt("HiveID");
                if(hiveVal == m_selectedHive)
                {
                    m_hiveId.setText(String.valueOf(hiveVal));
                    m_location.setText(hives.getString("Location"));
                    m_owner.setText(hives.getString("Owner"));
                    m_tempLB.setText(String.valueOf(hives.getFloat("TempLB")));
                    m_tempUB.setText(String.valueOf(hives.getFloat("TempUB")));
                    m_humidLB.setText(String.valueOf(hives.getFloat("HumidLB")));
                    m_humidUB.setText(String.valueOf(hives.getFloat("HumidUB")));
                    m_blockTime.setText(String.valueOf(hives.getFloat("BlockTime")));

                    break;
                }
            }
        }
        catch(Exception e)
        {}
        // TODO: Add set/cancel buttons and integrate them
    }

    private void resetButtonClicked(View v)
    {
        ShowHiveInfo();
    }

    private void saveButtonClicked(View v)
    {
        String loc = m_location.getText().toString();
        String own = m_owner.getText().toString();
        float tmlb = Float.valueOf(m_tempLB.getText().toString());
        float tmub = Float.valueOf(m_tempUB.getText().toString());
        float hmlb = Float.valueOf(m_humidLB.getText().toString());
        float hmub = Float.valueOf(m_humidUB.getText().toString());
        float bltm = 0.0f;//Float.valueOf()
        int hiveId = Integer.valueOf(m_hiveId.getText().toString());

        Database.getInstance().UpdateHives(loc,own,tmlb,tmub,hmlb,hmub,bltm,hiveId);
    }

    private void AlertsOnOffClicked(View v)
    {

    }
}
