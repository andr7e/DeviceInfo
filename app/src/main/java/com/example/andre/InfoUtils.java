package com.example.andre;

import android.os.Build;
import android.text.TextUtils;

import com.example.andre.androidshell.ShellExecuter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andrey on 24.02.16.
 */
public class InfoUtils
{
    public static final String UNKNOWN       = "Other";
    public static final String TOUCHPANEL    = "Touchscreen";
    public static final String ACCELEROMETER = "Accelerometer";
    public static final String ALSPS         = "Als/ps";
    public static final String MAGNETOMETER  = "Magnetometer";
    public static final String GYROSCOPE     = "Gyroscope";
    public static final String CHARGER       = "Charger";
    public static final String LENS          = "Lens";

    public static String getPlatform()
    {
        return Build.HARDWARE;
    }

    public static String getModel()
    {
        return Build.MODEL;
    }

    public static String getBrand()
    {
        return Build.BRAND;
    }

    public static String getManufacturer()
    {
        return Build.MANUFACTURER;
    }

    public static String getDeviceName()
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

    public static String getAndroidVersion()
    {
        return Build.VERSION.RELEASE;
    }

    public static String getAndroidAPI()
    {
        return Integer.toString(Build.VERSION.SDK_INT);
    }

    // Shell

    public static String getKernelVersion (ShellExecuter se)
    {
        String command = "cat /proc/version";

        return se.execute(command);
    }

    public static String getFlashName (ShellExecuter se)
    {
        String command = "cat /sys/class/mmc_host/mmc0/mmc0:0001/name";

        return se.execute(command);
    }

    public static String getCpufreq (ShellExecuter se)
    {
        String command = "cat /proc/cpufreq/cpufreq_freq";

        return se.execute(command);
    }

    public static String getRamType (ShellExecuter se)
    {
        String command = "cat /sys/bus/platform/drivers/ddr_type/ddr_type";

        return se.execute(command);
    }

    public static String[] getDriversList(ShellExecuter se)
    {
        String command = "ls cat /sys/bus/i2c/drivers";

        String out = se.execute(command);

        String[] list = out.split("\n");

        return list;
    }

    public static HashMap<String,String> getDriversHash(ShellExecuter se)
    {
        String[] list = InfoUtils.getDriversList(se);

        HashMap<String,String> hm = new HashMap<String,String>();

        ArrayList<String> otherList = new ArrayList<String>();

        for (String line : list)
        {
            String value = line.toUpperCase();

            if (value.endsWith("AF"))
            {
                hm.put(InfoUtils.LENS, line);
            }
            else if (value.startsWith("LIS") || value.startsWith("KXT") || value.startsWith("BMA"))
            {
                hm.put(InfoUtils.ACCELEROMETER, line);
            }
            else if (value.startsWith("EPL") || value.startsWith("APDS") || value.startsWith("STK") || value.startsWith("LTR"))
            {
                hm.put(InfoUtils.ALSPS, line);
            }
            else if (value.startsWith("MPU"))
            {
                hm.put(InfoUtils.GYROSCOPE, line);
            }
            else if (value.startsWith("MPU") || value.startsWith("AK") || value.startsWith("YAMAHA53"))
            {
                hm.put(InfoUtils.MAGNETOMETER, line);
            }
            else if (value.startsWith("BQ") || value.startsWith("FAN") || value.startsWith("NCP"))
            {
                hm.put(InfoUtils.CHARGER, line);
            }
            else if (value.startsWith("GT") || value.startsWith("FT") || value.startsWith("S3") || value.startsWith("MTK-TPD"))
            {
                hm.put(InfoUtils.TOUCHPANEL, line);
            }
            else
            {
                otherList.add(line);
            }

            hm.put(InfoUtils.UNKNOWN, TextUtils.join("\n", otherList));
        }

        return hm;
    }

    public static String getLcmName(String cmdline)
    {
        String[] list = cmdline.split(" ");

        for (String line : list) {
            if (line.startsWith("lcm"))
            {
                return line.substring(6);
            }
        }

        return "";
    }

    // su

    public static String getCmdline(ShellExecuter se)
    {
        String command = "su -c cat /proc/cmdline";

        return se.execute(command);
    }
}
