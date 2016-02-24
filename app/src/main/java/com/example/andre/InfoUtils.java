package com.example.andre;

import android.os.Build;

import com.example.andre.androidshell.ShellExecuter;

/**
 * Created by andrey on 24.02.16.
 */
public class InfoUtils
{
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
}
