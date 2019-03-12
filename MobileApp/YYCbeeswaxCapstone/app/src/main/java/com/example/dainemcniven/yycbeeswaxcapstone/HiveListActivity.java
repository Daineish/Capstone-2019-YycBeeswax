package com.example.dainemcniven.yycbeeswaxcapstone;

import android.content.Intent;
import android.graphics.Color;
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


public class HiveListActivity extends AppCompatActivity
{
    private String m_selectedHive = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hivelist);

        // TODO: Access the database or whatever and list what hives are available
        GetAvailableHives();

    }

    View.OnClickListener myhandler1 = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            EditText sender = (EditText)v;
            String hivenum = sender.getTag().toString();
            // TODO Parse num from hivenum
            Log.e("a", "Hive: " + hivenum);
            Intent myIntent = new Intent(HiveListActivity.this, HiveInfoActivity.class);
            myIntent.putExtra("HIVE_ID", hivenum); //Optional parameters
            HiveListActivity.this.startActivity(myIntent);
        }
    };


    private void GetAvailableHives()
    {
//        ResultSet hives = Database.getInstance().GetHives();
//
//        LinearLayout mainLayout = new LinearLayout(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
//        ScrollView scrollView = new ScrollView(this);
//        LinearLayout linearLayout = new LinearLayout(this);
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        mainLayout.addView(scrollView);
//        scrollView.addView(linearLayout);
//        this.addContentView(mainLayout, layoutParams);
//
//        try
//        {
//            while(hives.next())
//            {
//                int hiveID = hives.getInt("HiveId");
//                TextView hive = new TextView(this);
//                hive.setText("Hive: " + hiveID);
//                hive.setOnClickListener(myhandler1);
//                hive.setTag(hiveID);
//                linearLayout.addView(hive);
//
//                View v = new View(this);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//                params.gravity = Gravity.CENTER;
//                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
//                params.height = 2;
//                v.setBackgroundColor(Color.BLACK);
//                v.setLayoutParams(params);
//            }
//        }
//        catch(Exception e)
//        {}
    }
}
