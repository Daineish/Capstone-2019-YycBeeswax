package com.example.dainemcniven.yycbeeswaxcapstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class LoginActivity extends AppCompatActivity
{
    private View m_progressView;
    private Button m_welcomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        m_progressView = findViewById(R.id.login_progress);
        m_welcomeButton = (Button)findViewById(R.id.welcomeButton);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        m_progressView.setVisibility(View.GONE);
        m_welcomeButton.setVisibility(View.VISIBLE);
    }

    public void welcomeClicked(View v)
    {
        m_welcomeButton.setVisibility(View.GONE);
        m_progressView.setVisibility(View.VISIBLE);

        Intent myIntent = new Intent(LoginActivity.this, WelcomeScreen.class);
        LoginActivity.this.startActivity(myIntent);
    }
}

