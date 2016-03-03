package com.example.andre.myapplication;

import com.example.andre.InfoList;
import com.example.andre.InfoUtils;
import com.example.andre.MtkUtil;
import com.example.andre.androidshell.ShellExecuter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    // settings
    public static final String PREF_USE_ROOT_MODE = "user_root_switch";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adjustAvailabilityActions();

        fillInformation();
    }

    public void runApplication(String packageName, String activityName)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName(packageName, packageName + "." + activityName));
            startActivity(intent);
        }
        catch (Exception e)
        {
            System.err.println ("Can't run app");
        }
    }

    public void onOpenEngineerMode(View view)
    {
        runApplication("com.mediatek.engineermode", "EngineerMode");
    }

    public void onRefreshButtonClick(View view)
    {
        fillInformation();
    }

    public void adjustAvailabilityActions() {
        String platform = InfoUtils.getPlatform().toUpperCase();

        if ( ! InfoUtils.isMtkPlatform(platform))
        {
            Button engineerModeButton = (Button)findViewById(R.id.engineerModeButton);

            engineerModeButton.setVisibility(View.GONE);
        }
    }

    void fillTableView (TableLayout tableLayout, ArrayList< Pair<String, String> > objList)
    {
        tableLayout.removeAllViews();

        tableLayout.setStretchAllColumns(true);

        float horMargin = getResources().getDimension(R.dimen.activity_horizontal_margin);
        int screenWidthOffset = InfoUtils.getScreenWidth()/2 - Math.round(horMargin);

        // View

        for (int i = 0; i < objList.size(); i++)
        {
            Pair<String, String> obj = objList.get(i);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            Context context = getApplicationContext();
            int color = ContextCompat.getColor(context, R.color.colorBackground);
            if (i % 2 == 0) row.setBackgroundColor(color);

            TextView text1 = new TextView(this);
            text1.setText(obj.first);

            TextView text2 = new TextView(this);
            text2.setText(obj.second);
            text2.setMaxWidth(screenWidthOffset);

            row.addView(text1);
            row.addView(text2);

            tableLayout.addView(row,i);
        }
    }

    public void fillInformation()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean rootMode = prefs.getBoolean(PREF_USE_ROOT_MODE, false);

        ArrayList< Pair<String, String> > objList = InfoList.buildInfoList(rootMode);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        fillTableView(tableLayout, objList);
    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_about: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
