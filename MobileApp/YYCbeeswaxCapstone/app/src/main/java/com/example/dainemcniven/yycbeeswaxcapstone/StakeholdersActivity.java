package com.example.dainemcniven.yycbeeswaxcapstone;

import android.os.AsyncTask;
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
    TcpClient m_tcpClient;
    public class ConnectTask extends AsyncTask<String, String, TcpClient>
    {
        @Override
        protected TcpClient doInBackground(String... message)
        {

            //we create a TCPClient object
            m_tcpClient = new TcpClient(new TcpClient.OnMessageReceived()
            {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message)
                {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            m_tcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);

            String[] hives = values[0].split(" ");
            ParseStakeholders(values[0]);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stakeholders);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
        new ConnectTask().execute("");

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) { }

        if(m_tcpClient != null)
            m_tcpClient.sendMessage("ANDROID_REQUEST STAKEHOLDER_LIST");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
    }

    private void ParseStakeholders(String val)
    {

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
        finish();
    }
}
