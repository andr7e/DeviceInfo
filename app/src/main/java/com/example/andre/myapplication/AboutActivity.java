package com.example.andre.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        fillAbout();
    }

    public void fillAbout()
    {
        TextView appNameTextView = (TextView)findViewById(R.id.appNameTextView);
        appNameTextView.setText(R.string.app_name);

        TextView versionTextView = (TextView)findViewById(R.id.versionTextView);
        versionTextView.setText(R.string.about_version);

        TextView authorTextView = (TextView)findViewById(R.id.authorTextView);
        authorTextView.setText(R.string.about_author);

        TextView aboutBottomTextView = (TextView)findViewById(R.id.aboutBottomTextView);
        aboutBottomTextView.setText(R.string.about_bottom);
    }

}
