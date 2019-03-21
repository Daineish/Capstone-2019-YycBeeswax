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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StakeholdersInfoActivity extends AppCompatActivity
{
    private int m_stakeholder;
    private String m_receivedVals = "";
    private TableLayout m_mainLayout;
    private int m_numHives = 0;
    private List<CheckBox> m_checkBoxes = new ArrayList<>();
    private List<Integer> m_hives = new ArrayList<>();

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
            Log.e("Received: ", "Received: " + values[0]);
            m_receivedVals = values[0];
            ParseInfo(values[0]);

            m_tcpClient.stopClient();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stakeholdersinfo);
        m_mainLayout = (TableLayout) findViewById(R.id.main_layout);

        m_stakeholder = getIntent().getIntExtra("STAKEHOLDER", -1);
        if(m_stakeholder == -1)
        {
            // TODO: error
        }
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
            m_tcpClient.sendMessage("ANDROID_REQUEST STAKEHOLDER_INFO " + m_stakeholder);
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
        finish();
    }

    public void saveClicked(View v)
    {
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
        new ConnectTask().execute("");

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) { }

        String str = "ANDROID_REQUEST STAKEHOLDER_UPDATE " + m_stakeholder + " " + m_hives.size() + " ";
        for(int i = 0; i < m_hives.size(); i++)
        {
            boolean b = m_checkBoxes.get((i*3)).isChecked();
            boolean h = m_checkBoxes.get((i*3)+1).isChecked();
            boolean t = m_checkBoxes.get((i*3)+2).isChecked();

            str += m_hives.get(i) + "_";
            str += b + "_" + h + "_" + t + "_";
        }

        if(m_tcpClient != null)
        {
            Log.e("Sending", "sending: " + str);
            m_tcpClient.sendMessage(str);
        }
    }

    public void resetClicked(View v)
    {
        Log.e("a","Reset clicked");
        ParseInfo(m_receivedVals);
    }

    public void ParseInfo(String str)
    {
        m_mainLayout.removeAllViews();
        m_hives.clear();
        m_checkBoxes.clear();
        String[] notifs = str.split("_");
        m_numHives = 0;
        for(int i = 0; i < notifs.length; i++)
        {
            String[] vals = notifs[i].split(" ");
            int hive = Integer.parseInt(vals[0]);
            m_hives.add(hive);
            String type = vals[1];

            TextView hiveTitle = new TextView(this);
            hiveTitle.setText("Hive: " + hive + " Alerts:");
            hiveTitle.setTextColor(Color.BLACK);
            hiveTitle.setTextSize(18);
            CheckBox cbox1 = new CheckBox(this);
            cbox1.setText("Blockages");
            cbox1.setChecked(type.contains("B") || type.contains("b"));
            CheckBox cbox2 = new CheckBox(this);
            cbox2.setText("Humidity");
            cbox2.setChecked(type.contains("H") || type.contains("h"));
            CheckBox cbox3 = new CheckBox(this);
            cbox3.setText("Temperature");
            cbox3.setChecked(type.contains("T") || type.contains("t"));
            m_checkBoxes.add(cbox1); m_checkBoxes.add(cbox2); m_checkBoxes.add(cbox3);

            TableRow row = new TableRow(this);
            row.addView(hiveTitle);

            TableRow row2 = new TableRow(this);
            row2.addView(cbox1);
            row2.addView(cbox2);
            row2.addView(cbox3);

            m_mainLayout.addView(row);
            m_mainLayout.addView(row2);
            m_numHives++;
        }
    }

}
