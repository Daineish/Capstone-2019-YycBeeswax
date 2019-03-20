package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import java.util.concurrent.TimeUnit;


public class HiveListActivity extends AppCompatActivity
{
    private String m_selectedHive = null;
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
            //response received from server
            Log.e("test", "response " + values[0]);
            GetAvailableHives(values[0]);
            //process server response here....

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hivelist);

        // TODO: Access the database or whatever and list what hives are available
//        new ConnectTask().execute("");
//
//        try
//        {
//            Thread.sleep(1000);
//        }
//        catch (InterruptedException e) { }
//
//
//        // Send Request for Available Hives
//        if(m_tcpClient!=null)
//            m_tcpClient.sendMessage("ANDROID_REQUEST HIVE_LIST");
    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            m_tcpClient.stopClient();
            TextView sender = (TextView)v;
            int hivenum = Integer.parseInt(sender.getTag().toString());

            Log.e("a", "Hive: " + hivenum);
            Intent myIntent = new Intent(HiveListActivity.this, HiveInfoActivity.class);
            myIntent.putExtra("HIVE_ID", hivenum); //Optional parameters
            HiveListActivity.this.startActivity(myIntent);
        }
    };

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
        if(m_tcpClient!=null)
            m_tcpClient.sendMessage("ANDROID_REQUEST HIVE_LIST");
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


    private void GetAvailableHives(String response)
    {
        LinearLayout mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(mainLayout, layoutParams);

        TextView title = new TextView(this);
        title.setText("Select a Hive to View Details");
        title.setTextSize(32);
        title.setTextColor(Color.BLACK);
        linearLayout.addView(title);
        View v = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = 3;
        v.setBackgroundColor(Color.BLACK);
        v.setLayoutParams(params);
        linearLayout.addView(v);

        String[] hives = response.split(" ");

        for(int i = 0; i < hives.length; i++)
        {
            TextView hive = new TextView(this);
            hive.setText("Hive: " + hives[i]);
            hive.setTextSize(24);
            hive.setTextColor(Color.BLACK);
            hive.setOnClickListener(myhandler1);
            hive.setTag(hives[i]);
            hive.setPadding(0,40,0,0);
            linearLayout.addView(hive);

//            View v = new View(this);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            params.gravity = Gravity.CENTER;
//            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
//            params.height = 2;
//            v.setBackgroundColor(Color.BLACK);
//            v.setLayoutParams(params);
//            linearLayout.addView(v);
        }
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
