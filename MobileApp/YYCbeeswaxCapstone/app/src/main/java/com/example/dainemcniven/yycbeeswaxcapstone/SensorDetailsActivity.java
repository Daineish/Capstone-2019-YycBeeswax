package com.example.dainemcniven.yycbeeswaxcapstone;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class SensorDetailsActivity extends AppCompatActivity implements DialogInterface.OnDismissListener
{
    private int m_selectedHive;
    private LinearLayout m_mainLayout = null;
    private TextView m_startDate;
    private TextView m_endDate;
    private TextView m_startTime;
    private TextView m_endTime;
    private Spinner m_hiveSpinner;
    private Spinner m_sensorSpinner;

    private DatePickerFragment m_datePicker;
    private TimePickerFragment m_timePicker;
    private boolean m_isStartTime = false;
    private boolean m_isStartDate = false;

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
            if("SENSOR_DATA".equals(hives[0]))
            {
                // If response is sensor data
                ShowSensorData(values[0]);

            }
            else
            {
                // If response is hive list
                ParseHives(values[0]);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensordetails);
        //m_selectedHive = Integer.parseInt(getIntent().getStringExtra("HIVE_ID"));
        m_startDate = (TextView)findViewById(R.id.startDateText);
        m_endDate = (TextView)findViewById(R.id.endDateText);
        m_startTime = (TextView)findViewById(R.id.startTimeText);
        m_endTime = (TextView)findViewById(R.id.endTimeText);
        m_hiveSpinner = (Spinner)findViewById(R.id.hiveSpinner);
        m_sensorSpinner = (Spinner)findViewById(R.id.sensorsSpinner);
    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {

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

        InitializeViews();
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

    private void InitializeViews()
    {
        // Default to the last 24 hours
        SimpleDateFormat dateF = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String date = dateF.format(Calendar.getInstance().getTime());
        String time = timeF.format(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance();//.add(Calendar.DAY_OF_YEAR, -1);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        String date2 = dateF.format(cal.getTime());
        String time2 = timeF.format(cal.getTime());

        m_startDate.setText(date2);
        m_startTime.setText(time2);
        m_endDate.setText(date);
        m_endTime.setText(time);

        // Add sensor types
        List<String> spinnerArray =  new ArrayList<>();
        spinnerArray.add("Temperature");
        spinnerArray.add("Humidity");
        spinnerArray.add("All");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_sensorSpinner.setAdapter(adapter);

        // Add available hives
        if(m_tcpClient!=null)
        {
            m_tcpClient.sendMessage("ANDROID_REQUEST HIVE_LIST");
            //m_tcpClient.stopClient();
        }
        //List<Integer> hiveArray = Utils.Utilities.GetAllHiveIds();
    }

    private void GetAvailableSensors(java.sql.Date start, java.sql.Date end)
    {
        // TODO: Default dates to show data for? (currently 24hours ago until now)
        ResultSet sensorsT = Database.getInstance().GetSensorsData(m_selectedHive, start, end, "Temperature");
        ResultSet sensorsH = Database.getInstance().GetSensorsData(m_selectedHive, start, end, "Humidity");

        if(m_mainLayout != null)
        {
            ((ViewManager)m_mainLayout.getParent()).removeView(m_mainLayout); // or something
        }
        m_mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        m_mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(m_mainLayout, layoutParams);
// HiveId, Time, SensorType, SensorData
        try
        {
            TextView tempTitle = new TextView(this);
            tempTitle.setText("Temperatures");
            tempTitle.setTextSize(24);
            linearLayout.addView(tempTitle);
            while(sensorsT.next())
            {
                int hiveID = sensorsT.getInt("HiveId"); // ?
                TextView hive = new TextView(this);
                hive.setText("\tHive: " + hiveID);
                hive.setOnClickListener(myhandler1);
                hive.setTag(hiveID);
                linearLayout.addView(hive);
            }
            View v = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = 2;
            v.setBackgroundColor(Color.BLACK);
            v.setLayoutParams(params);

            TextView humidTitle = new TextView(this);
            humidTitle.setText("Humidity");
            humidTitle.setTextSize(24);
            linearLayout.addView(humidTitle);
            while(sensorsH.next())
            {
                int hiveID = sensorsT.getInt("HiveId"); // ?
                TextView hive = new TextView(this);
                hive.setText("\tHive: " + hiveID);
                hive.setOnClickListener(myhandler1);
                hive.setTag(hiveID);
                linearLayout.addView(hive);
            }
        }
        catch(Exception e)
        {}
    }

    public void showButtonClicked(View v)
    {
        m_tcpClient.stopClient();
        new ConnectTask().execute("");
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) { }

        String hiveId = m_hiveSpinner.getSelectedItem().toString();
        if("All".equals(hiveId))
            hiveId = "-1";

        String sensor = m_sensorSpinner.getSelectedItem().toString();
        if("All".equals(sensor))
            sensor = "IS NOT NULL";

        String dtStart = m_startDate.getText().toString() + " " + m_startTime.getText().toString();
        String dtEnd = m_endDate.getText().toString() + " " + m_endTime.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy HH:mm");

        java.util.Date dateS;
        java.util.Date dateE;
        try
        {
            dateS = format.parse(dtStart);
            dateE = format.parse(dtEnd);
            Log.e("", dateS.toString());
            Log.e("", dateE.toString());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        String str = "ANDROID_REQUEST SENSOR_DETAILS ";
        str += hiveId + "_";
        str += sensor + "_";
        str += dateS.getTime() + "_";
        str += dateE.getTime();

        if(m_tcpClient!=null)
            m_tcpClient.sendMessage(str);
    }

    public void showTimePickerDialog(View v)
    {
        if(v.getId() == R.id.startTimeButton)
            m_isStartTime = true;
        else if(v.getId() == R.id.endTimeButton)
            m_isStartTime = false;
        else
        {
            Log.e("uh-oh", "Unknown button pressed");
            System.exit(1);
        }
        m_timePicker = new TimePickerFragment();
        m_timePicker.show(getFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v)
    {
        if(v.getId() == R.id.startDateButton)
            m_isStartDate = true;
        else if(v.getId() == R.id.endDateButton)
            m_isStartDate = false;
        else
        {
            Log.e("uh-oh", "Unknown button pressed");
            System.exit(1);
        }

        m_datePicker = new DatePickerFragment();
        m_datePicker.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onDismiss(final DialogInterface dialog)
    {
        if(dialog instanceof android.app.TimePickerDialog)
        {
            SimpleDateFormat timeF = new SimpleDateFormat("HH:mm");
            String date = m_timePicker.m_hour + ":" + m_timePicker.m_minute;
            java.util.Date d;
            try { d = timeF.parse(date); }
            catch(ParseException e) { d = new java.util.Date(); }

            if(m_isStartTime)
                m_startTime.setText(timeF.format(d));
            else
                m_endTime.setText(timeF.format(d));

        }
        else if(dialog instanceof android.app.DatePickerDialog)
        {
            SimpleDateFormat dateF = new SimpleDateFormat("MMM d, yyyy");
            String monthString = new DateFormatSymbols().getShortMonths()[m_datePicker.m_month];
            String date = monthString + " " + m_datePicker.m_day + ", " + m_datePicker.m_year;
            java.util.Date d;
            try { d = dateF.parse(date); }
            catch(ParseException e) { d = new java.util.Date(); }

            if(m_isStartDate)
                m_startDate.setText(dateF.format(d));
            else
                m_endDate.setText(dateF.format(d));
        }
    }

    public void ParseHives(String val)
    {
        String[] hives = val.split(" ");
        List<String> hiveArray = new ArrayList<>();
        for(int i = 0; i < hives.length; i++)
        {
            hiveArray.add(hives[i]);
        }
        hiveArray.add("All");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hiveArray);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        m_hiveSpinner.setAdapter(adapter1);
    }

    public void ShowSensorData(String val)
    {
        // parse data and display it in some way
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