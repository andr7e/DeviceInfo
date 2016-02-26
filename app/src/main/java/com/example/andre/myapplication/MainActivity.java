package com.example.andre.myapplication;

import com.example.andre.InfoUtils;
import com.example.andre.androidshell.ShellExecuter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

        fillInformatoin();
    }

    public int getScreenWidth ()
    {
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int width = size.x;

        return width;
    }

    public int getScreenHeight ()
    {
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int height = size.y;

        return height;
    }

    public String getResolution ()
    {
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        return String.format("%dx%d", height, width);
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
            System.out.println ("Can't run app");
        }
    }

    public void onOpenEngineerMode(View view)
    {
        //runApplication("com.mediatek.engineermode", "EngineerMode");

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    Pair<String, String> createObj (String key, String value)
    {
        return new Pair<String, String>(key, value);
    }
    public void onMyButtonClick(View view)
    {
        fillInformatoin();

        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent);
    }

    public void addItem (ArrayList< Pair<String, String> > objList, String key, String value)
    {
        if ( ! value.isEmpty())
        {
            objList.add(new Pair<String, String>(key, value));
        }
    }

    public void fillInformatoin()
    {
        ShellExecuter exec = new ShellExecuter();

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        tableLayout.removeAllViews();

        tableLayout.setStretchAllColumns(true);

        float horMargin = getResources().getDimension(R.dimen.activity_horizontal_margin);
        int screenWidthOffset = getScreenWidth()/2 - Math.round(horMargin);

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        addItem(objList, "Manufacturer", InfoUtils.getManufacturer());
        addItem(objList, "Model", InfoUtils.getModel());
        addItem(objList, "Brand", InfoUtils.getBrand());

        addItem(objList, "Resolution", getResolution());

        addItem(objList, "Platform", InfoUtils.getPlatform());

        //addItem(objList, "CPU freq",   InfoUtils.getCpufreq(exec));

        addItem(objList, "Android Version", InfoUtils.getAndroidVersion());
        addItem(objList, "API", InfoUtils.getAndroidAPI());

        addItem(objList, "Kernel", InfoUtils.getKernelVersion(exec));

        //
        HashMap<String,String>  hash = InfoUtils.getDriversHash(exec);

        //
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean rootMode = prefs.getBoolean(PREF_USE_ROOT_MODE, false);

        String cmdline = "";

        if (rootMode)
        {
            cmdline = InfoUtils.getCmdline(exec);

            if ( ! cmdline.isEmpty())
            {
                String lcmName = InfoUtils.getLcmName(cmdline);

                if ( ! lcmName.isEmpty())
                {
                    hash.put(InfoUtils.LCM, lcmName);
                }
            }
        }

        String[] keyList = {
                InfoUtils.PMIC,
                InfoUtils.RTC,
                InfoUtils.LCM,
                InfoUtils.TOUCHPANEL,
                InfoUtils.ACCELEROMETER,
                InfoUtils.ALSPS,
                InfoUtils.MAGNETOMETER,
                InfoUtils.GYROSCOPE,
                InfoUtils.CHARGER,
                InfoUtils.CAMERA,
                InfoUtils.LENS,
                InfoUtils.UNKNOWN
        };

        for (String key : keyList)
        {
            if (hash.containsKey(key))
            {
                String value = hash.get(key);

                objList.add(new Pair<String, String>(key, value));
            }
        }

        //
        addItem(objList, "Sound", InfoUtils.getSoundCard(exec));

        addItem(objList, "RAM",   InfoUtils.getRamType(exec));
        addItem(objList, "Flash", InfoUtils.getFlashName(exec));

        addItem(objList, "Baseband", Build.getRadioVersion());

        addItem(objList, "cmdline", cmdline);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                runApplication("com.mediatek.engineermode", "EngineerMode");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

