package com.example.dainemcniven.yycbeeswaxcapstone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.sql.ResultSet;


public class HiveInfoActivity extends AppCompatActivity
{
    private int m_selectedHive;
    private EditText m_hiveId;
    private EditText m_location;
    private EditText m_owner;
    private EditText m_tempLB;
    private EditText m_tempUB;
    private EditText m_humidLB;
    private EditText m_humidUB;
    private EditText m_blockTime;
    private String m_origValues = "";
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
            m_origValues = values[0];
            ShowHiveInfo(values[0]);

            m_tcpClient.stopClient();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertbounds);
        m_selectedHive = getIntent().getIntExtra("HIVE_ID", -1);
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
//            m_tcpClient.sendMessage("ANDROID_REQUEST HIVE_INFO " + m_selectedHive);
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
            Thread.sleep(500);
        }
        catch (InterruptedException e) { }
        // Send Request for Available Hives
        if(m_tcpClient!=null)
            m_tcpClient.sendMessage("ANDROID_REQUEST HIVE_INFO " + m_selectedHive);
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

    private void ShowHiveInfo(String response)
    {
        String[] data = response.split("_");

        if(data.length != 8)
        {
            Log.e("Server", "Length should be 8, received: " + data.length);
            return;
        }

        m_hiveId.setText(data[0]);
        m_location.setText(data[1]);
        m_owner.setText(data[2]);
        m_tempLB.setText(data[3]);
        m_tempUB.setText(data[4]);
        m_humidLB.setText(data[5]);
        m_humidUB.setText(data[6]);
        m_blockTime.setText(data[7]); // TODO: minutes (I hope)
    }

    public void resetButtonClicked(View v)
    {
        ShowHiveInfo(m_origValues);
    }

    public void saveButtonClicked(View v)
    {
        if(m_tcpClient != null)
            m_tcpClient.stopClient();
        new ConnectTask().execute("");

        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e) { }

        String str = "ANDROID_REQUEST HIVE_UPDATE ";
        str += m_hiveId.getText().toString() + "_";
        str += m_location.getText().toString() + "_";
        str += m_owner.getText().toString() + "_";
        str += Float.valueOf(m_tempLB.getText().toString()) + "_";
        str += Float.valueOf(m_tempUB.getText().toString()) + "_";
        str += Float.valueOf(m_humidLB.getText().toString()) + "_";
        str += Float.valueOf(m_humidUB.getText().toString()) + "_";
        float bltm = Float.valueOf(m_blockTime.getText().toString());
        bltm *= 60.0; // TODO: check this works as expected
        str += bltm + "_";
        str += m_selectedHive;

        Log.e("Sending", "sending: "+str);
        if(m_tcpClient != null)
        {
            m_tcpClient.sendMessage(str);
            AlertDialog alertDialog = new AlertDialog.Builder(HiveInfoActivity.this).create();
            alertDialog.setTitle("Message Sent");
            alertDialog.setMessage("Update sent to database.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        //Database.getInstance().UpdateHives(loc,own,tmlb,tmub,hmlb,hmub,bltm,hiveId);
    }

    public void AlertsOnOffClicked(View v)
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
