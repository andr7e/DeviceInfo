package com.example.andre.myapplication;

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
        String command = "ls cat /sys/bus/i2c/drivers";

        String out = se.execute(command);

        String[] list = out.split("\n");

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

    public String getPlatform()
    {
        return Build.HARDWARE;
    }

    public String getKernelVersion (ShellExecuter se)
    {
        String command = "cat /proc/version";

        return se.execute(command);
    }

    public String getFlashName (ShellExecuter se)
    {
        String command = "cat /sys/class/mmc_host/mmc0/mmc0:0001/name";

        return se.execute(command);
    }

    public String getCpufreq (ShellExecuter se)
    {
        String command = "cat /proc/cpufreq/cpufreq_freq";

        return se.execute(command);
    }

    public String getRamType (ShellExecuter se)
    {
        String command = "cat /sys/bus/platform/drivers/ddr_type/ddr_type";

        return se.execute(command);
    }

    /*
    public String getPlatform2(ShellExecuter se)
    {
        String command = "cat /proc/cpuinfo";

        String out = se.execute(command);

        String[] list = out.split("\n");

        //System.out.println(list);

        HashMap<String,String> hm = new HashMap<String,String>();

        for (String line : list)
        {
            String[] elementList = line.split(":");

            if (elementList.length == 2)
            {
                String key   = elementList[0].trim();
                String value = elementList[1].trim();

                hm.put(key, value);
            }
        }

        String platform = hm.get("Hardware");

        //System.out.println(platform);

        return platform;
    }*/

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

    /*
    public String getDeviceName()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        if (model.startsWith(manufacturer))
        {
            return model;
        }
        else
        {
            return manufacturer + " " + model;
        }
    }


    public String getModel()
    {
        return Build.MODEL;
    }

    public String getBrand()
    {
        return Build.BRAND;
    }

    public String getManufacturer()
    {
        return Build.MANUFACTURER;
    }*/

    public void onOpenEngineerMode(View view)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.mediatek.engineermode","com.mediatek.engineermode.EngineerMode"));
        startActivity(intent);
    }

    public void onMyButtonClick(View view)
    {
        ShellExecuter exec = new ShellExecuter();

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String brand = Build.BRAND;

        String version = Build.VERSION.RELEASE;
        String api = Integer.toString(Build.VERSION.SDK_INT);

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        tableLayout.removeAllViews();

        tableLayout.setStretchAllColumns(true);

        float horMargin = getResources().getDimension(R.dimen.activity_horizontal_margin);
        int screenWidthOffset = getScreenWidth()/2 - Math.round(horMargin);

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        objList.add (new Pair<String, String>("Manufacturer", manufacturer));
        objList.add (new Pair<String, String>("Model", model));
        objList.add (new Pair<String, String>("Brand", brand));

        objList.add (new Pair<String, String>("Resolution", getResolution()));

        objList.add (new Pair<String, String>("Platform", getPlatform()));
        //objList.add (new Pair<String, String>("CPU freq", getCpufreq(exec)));
        objList.add (new Pair<String, String>("RAM", getRamType(exec)));

        objList.add (new Pair<String, String>("Android Version", version));

        objList.add (new Pair<String, String>("API", api));

        objList.add (new Pair<String, String>("Baseband", Build.getRadioVersion()));

        objList.add (new Pair<String, String>("Kernel", getKernelVersion(exec)));
        objList.add (new Pair<String, String>("Flash", getFlashName(exec)));

        for (int i = 0; i < objList.size(); i++)
        {
            Pair<String, String> obj = objList.get(i);

            TableRow row = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(lp);

            //int color = getResources().getColor(R.color.colorBackground);

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
        //String command = "cat /proc/cpuinfo";

        //String out = exe.execute(command);

        String manufacturer = "Manufacturer:\n"  + Build.MANUFACTURER;
        String model = "Model:\n"  +  Build.MODEL;
        String brand = "Brand:\n"  + Build.BRAND;

        String platform = "Platform:\n"  + getPlatform();
        String cpufreq = "Freq:\n"  + getCpufreq(exe);
        String raminfo = "RAM:\n"  + getRamType(exe);

        String resolution = "Resolution:\n" + getResolution();
        String verison = "Version:\n"  + Build.VERSION.RELEASE + "(" + Build.VERSION.SDK_INT + ")";

        String baseband = "Baseband:\n"  + Build.getRadioVersion();

        String kernelVerison = "Kernel:\n" + getKernelVersion(exe);
        String flashName = "Flash:\n" + getFlashName(exe);

        String drivers = getDrivers(exe);

        StringBuilder sb = new StringBuilder();

        sb.append(manufacturer).append("\n");
        sb.append(model).append("\n");
        sb.append(brand).append("\n");

        sb.append(verison).append("\n");
        sb.append(baseband).append("\n");
        sb.append(flashName).append("\n");

        sb.append(kernelVerison).append("\n");

        sb.append(platform).append("\n");
        sb.append(cpufreq).append("\n");
        sb.append(raminfo).append("\n");
        sb.append(resolution).append("\n");;
        sb.append(drivers);

        String out = sb.toString();

        TextView myTextView = (TextView)findViewById(R.id.textView);

        myTextView.setText(out);
    }
}

