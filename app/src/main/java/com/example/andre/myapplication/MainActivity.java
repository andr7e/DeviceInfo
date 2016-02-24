package com.example.andre.myapplication;

import com.example.andre.InfoUtils;
import com.example.andre.androidshell.ShellExecuter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String getDrivers(ShellExecuter se)
    {
        String[] list = InfoUtils.getDriversList(se);

        HashMap<String,String> hm = new HashMap<String,String>();

        ArrayList<String> otherList = new ArrayList<String>();

        for (String line : list)
        {
            String value = line.toUpperCase();

            if (value.endsWith("AF"))
            {
                hm.put("Lens", line);
            }
            else if (value.startsWith("LIS") || value.startsWith("KXT") || value.startsWith("BMA"))
            {
                hm.put("Accelerometer", line);
            }
            else if (value.startsWith("EPL") || value.startsWith("APDS") || value.startsWith("STK") || value.startsWith("LTR"))
            {
                hm.put("Als/ps", line);
            }
            else if (value.startsWith("MPU"))
            {
                hm.put("Gyroscope", line);
            }
            else if (value.startsWith("MPU") || value.startsWith("AK") || value.startsWith("YAMAHA53"))
            {
                hm.put("Magnetometer", line);
            }
            else if (value.startsWith("BQ") || value.startsWith("FAN") || value.startsWith("NCP"))
            {
                hm.put("Charger", line);
            }
            else if (value.startsWith("GT") || value.startsWith("FT") || value.startsWith("S3") || value.startsWith("MTK-TPD"))
            {
                hm.put("Touchscreen", line);
            }
            else
            {
                otherList.add(line);
            }

            hm.put("Other", TextUtils.join("\n", otherList));
        }

        String res = "";

        for (String key : hm.keySet())
        {
            String value = hm.get(key);

            res += key + ":\n" + value + "\n";
        }

        return res;
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(packageName,packageName + "." + activityName));
        startActivity(intent);
    }

    public void onOpenEngineerMode(View view)
    {
        runApplication("com.mediatek.engineermode", "EngineerMode");
    }

    Pair<String, String> createObj (String key, String value)
    {
        return new Pair<String, String>(key, value);
    }

    public void onMyButtonClick(View view)
    {
        ShellExecuter exec = new ShellExecuter();

        String version = Build.VERSION.RELEASE;
        String api = Integer.toString(Build.VERSION.SDK_INT);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        tableLayout.removeAllViews();

        tableLayout.setStretchAllColumns(true);

        float horMargin = getResources().getDimension(R.dimen.activity_horizontal_margin);
        int screenWidthOffset = getScreenWidth()/2 - Math.round(horMargin);

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        objList.add (new Pair<String, String>("Manufacturer", InfoUtils.getManufacturer()));
        objList.add (new Pair<String, String>("Model",        InfoUtils.getModel()));
        objList.add (new Pair<String, String>("Brand",        InfoUtils.getBrand()));

        objList.add (new Pair<String, String>("Resolution", getResolution()));

        objList.add (new Pair<String, String>("Platform",   InfoUtils.getPlatform()));
        //objList.add (new Pair<String, String>("CPU freq", InfoUtils.getCpufreq(exec)));
        objList.add (new Pair<String, String>("RAM",        InfoUtils.getRamType(exec)));

        objList.add (new Pair<String, String>("Android Version", InfoUtils.getAndroidVersion()));
        objList.add (new Pair<String, String>("API",             InfoUtils.getAndroidAPI()));

        objList.add (new Pair<String, String>("Baseband", Build.getRadioVersion()));

        objList.add (new Pair<String, String>("Kernel", InfoUtils.getKernelVersion(exec)));
        objList.add (new Pair<String, String>("Flash",  InfoUtils.getFlashName(exec)));

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
            //text2.setMaxLines(5);
            text2.setMaxWidth(screenWidthOffset);

            row.addView(text1);
            row.addView(text2);

            tableLayout.addView(row,i);
        }
    }

    public void onMyButtonClick2(View view)
    {
        ShellExecuter exe = new ShellExecuter();
        //String command = "su -c cat /proc/cmdline";

        String drivers = getDrivers(exe);

        StringBuilder sb = new StringBuilder();

        sb.append(drivers);

        String out = sb.toString();

        TextView myTextView = (TextView)findViewById(R.id.textView);

        myTextView.setText(out);
    }
}

