package com.example.andre.myapplication;

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

    public void addItem (ArrayList< Pair<String, String> > objList, String key, String value)
    {
        if ( ! value.isEmpty())
        {
            objList.add(new Pair<String, String>(key, value));
        }
    }

    public void adjustAvailabilityActions() {
        String platform = InfoUtils.getPlatform().toUpperCase();

        if ( ! platform.startsWith("MT"))
        {
            Button engineerModeButton = (Button)findViewById(R.id.engineerModeButton);

            engineerModeButton.setVisibility(View.GONE);
        }
    }

    void fillTableView (ArrayList< Pair<String, String> > objList)
    {
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        tableLayout.removeAllViews();

        tableLayout.setStretchAllColumns(true);

        float horMargin = getResources().getDimension(R.dimen.activity_horizontal_margin);
        int screenWidthOffset = getScreenWidth()/2 - Math.round(horMargin);

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
        ShellExecuter exec = new ShellExecuter();

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        String platform = InfoUtils.getPlatform();

        addItem(objList, "Manufacturer", InfoUtils.getManufacturer());
        addItem(objList, "Model", InfoUtils.getModel());
        addItem(objList, "Brand", InfoUtils.getBrand());

        addItem(objList, "Resolution", getResolution());

        addItem(objList, "Platform", platform);

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

        hash.put(InfoUtils.SOUND, InfoUtils.getSoundCard(exec));

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
                InfoUtils.CAMERA_BACK,
                InfoUtils.CAMERA_FRONT,
                InfoUtils.LENS,
                InfoUtils.SOUND,
                InfoUtils.MODEM,
                InfoUtils.UNKNOWN
        };

        for (String key : keyList)
        {
            if (hash.containsKey(key))
            {
                String value = hash.get(key);

                addItem(objList, key, value);
            }
        }

        HashMap<String,String>  mtkhash = MtkUtil.getProjectDriversHash();

        for (String key : keyList)
        {
            if (mtkhash.containsKey(key))
            {
                String value = mtkhash.get(key);

                addItem(objList, key, value);
            }
        }

        //
        addItem(objList, "RAM",   InfoUtils.getRamType(exec));
        addItem(objList, "Flash", InfoUtils.getFlashName(exec));

        addItem(objList, "Baseband", Build.getRadioVersion());

        addItem(objList, "cmdline", cmdline);

        addItem(objList, "Partitions", InfoUtils.getPartitions(platform, exec));

       fillTableView(objList);
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
