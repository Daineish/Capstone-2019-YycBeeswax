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


public class HiveListActivity extends AppCompatActivity
{
    private String m_selectedHive = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameralist);

        // TODO: Access the database or whatever and list what cameras are available for streaming
        GetAvailableCameras();

    }

//    View.OnClickListener myhandler1 = new View.OnClickListener()
//    {
//        public void onClick(View v)
//        {
//            EditText sender = (EditText) v;
//            String dbSensor = sender.getText().toString();
//            // TODO: Open up stream or something
//            Log.e("a", "Camera: " + dbSensor);
//        }
//    };


    private void GetAvailableCameras()
    {
        ResultSet hives = Database.getInstance().GetHives();

        LinearLayout mainLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(scrollView);
        scrollView.addView(linearLayout);
        this.addContentView(mainLayout, layoutParams);

        // rs.getInt(HiveId)
        // rs.getVarchar(Location)
        // rs.getVarchar(Owner)
        // rs.getFloat(TempLB)
        // rs.getFloat(BlockTime);
        try
        {
            while(hives.next())
            {
                TextView hiveid = new TextView(this);
                hiveid.setText("Hive ID: " + hives.getInt("HiveId"));
                hiveid.setInputType(0);
//                ed.setOnClickListener(myhandler1);
                linearLayout.addView(hiveid);

                EditText location = new EditText(this);
                location.setText("\tLoc: " + hives.getString("Location"));
                location.setInputType(1); // ??
                linearLayout.addView(location);

                EditText owner = new EditText(this);
                owner.setText("\tOwner: " + hives.getString("Owner"));
                owner.setInputType(1); //?
                linearLayout.addView(owner);

                EditText tempLB = new EditText(this);
                tempLB.setText("\tOwner: " + hives.getFloat("TempLB"));
                tempLB.setInputType(1); //?
                linearLayout.addView(tempLB);

                EditText tempUB = new EditText(this);
                tempUB.setText("\tOwner: " + hives.getFloat("TempUB"));
                tempUB.setInputType(1); //?
                linearLayout.addView(tempUB);

                EditText humudLB = new EditText(this);
                humudLB.setText("\tOwner: " + hives.getFloat("HumidLB"));
                humudLB.setInputType(1); //?
                linearLayout.addView(humudLB);

                EditText humidUB = new EditText(this);
                humidUB.setText("\tOwner: " + hives.getFloat("humidUB"));
                humidUB.setInputType(1); //?
                linearLayout.addView(humidUB);
            }
        }
        catch(Exception e)
        {}
    }
}
