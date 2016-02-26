package com.example.andre;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

import com.example.andre.androidshell.ShellExecuter;
import com.example.andre.myapplication.BinaryDataHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    public static final String CAMERA        = "Camera";
    public static final String PMIC          = "PMIC";
    public static final String RTC           = "RTC";

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

    public static boolean isActiveDeviceI2C(File dir)
    {
        for (File file : dir.listFiles())
        {
            if (file.isDirectory())
            {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getDeviceListI2C(File dir)
    {
        ArrayList<String> list = new ArrayList<String>();

        for (File file : dir.listFiles())
        {
            if (file.isDirectory())
            {
                String name = file.getName();

                if (isActiveDeviceI2C(file))
                {
                    list.add(name);
                }
            }
        }
        return list;
    }

    public static String[] getDriversList(ShellExecuter se)
    {
        String path = "/sys/bus/i2c/drivers/";

        File dir = new File(path);

        ArrayList<String>  list = getDeviceListI2C(dir);

        return list.toArray(new String[0]);
    }

    //

    public static boolean isPrefixMatched (String[] prefixList, String value)
    {
        for (String prefix : prefixList)
        {
            if (value.startsWith(prefix))
            {
                return true;
            }
        }

        return false;
    }

    public static HashMap<String,String> getDriversHash(ShellExecuter se)
    {
        String[] cameraPrefixList  = {"OV", "GC", "SP", "IMX", "S5", "HI"};
        String[] touchPrefixList   = {"GT", "FT", "S3", "GSL", "MTK-TPD", "-TS"};
        String[] chargerPrefixList = {"BQ", "FAN", "NCP", "CW"};
        String[] alspsPrefixList   = {"EPL", "APDS", "STK", "LTR"};
        String[] pmicPrefixList    = {"ACT", "WM", "TPS"};

        String[] accelerometerPrefixList  = {"LIS", "KXT", "BMA", "MMA"};
        String[] magnetometerPrefixList   = {"AK", "YAMAHA53"};

        String[] list = InfoUtils.getDriversList(se);

        HashMap<String,String> hm = new HashMap<String,String>();

        ArrayList<String> cameraList  = new ArrayList<String>();
        ArrayList<String> touchList   = new ArrayList<String>();
        ArrayList<String> chargerList = new ArrayList<String>();
        ArrayList<String> alspsList   = new ArrayList<String>();
        ArrayList<String> pmicList    = new ArrayList<String>();

        ArrayList<String> accelerometerList = new ArrayList<String>();
        ArrayList<String> magnetometerList = new ArrayList<String>();

        ArrayList<String> otherList = new ArrayList<String>();

        for (String line : list)
        {
            String value = line.toUpperCase();

            if (value.endsWith("AF"))
            {
                hm.put(InfoUtils.LENS, line);
            }
            else if (isPrefixMatched(alspsPrefixList, value))
            {
                alspsList.add(line);
            }
            else if (isPrefixMatched(accelerometerPrefixList, value))
            {
                accelerometerList.add(line);
            }
            else if (value.startsWith("MPU"))
            {
                hm.put(InfoUtils.GYROSCOPE, line);
            }
            else if (isPrefixMatched(magnetometerPrefixList, value))
            {
                magnetometerList.add(line);
            }
            else if (isPrefixMatched(chargerPrefixList, value))
            {
                chargerList.add(line);
            }
            else if (isPrefixMatched(touchPrefixList, value) || value.endsWith("-TS") || value.endsWith("-TPD"))
            {
                touchList.add(line);
            }
            else if (isPrefixMatched(pmicPrefixList, value))
            {
                pmicList.add(line);
            }
            else if (value.startsWith("RTC"))
            {
                hm.put(InfoUtils.RTC, line);
            }
            else if (isPrefixMatched(cameraPrefixList, value))
            {
                cameraList.add(line);
            }
            else
            {
                otherList.add(line);
            }
        }

        ArrayList<String> mtkCameraList = getMtkCameraList();

        cameraList.addAll(mtkCameraList);

        if ( ! cameraList.isEmpty())   hm.put(InfoUtils.CAMERA,     TextUtils.join("\n", cameraList));
        if ( ! touchList.isEmpty())    hm.put(InfoUtils.TOUCHPANEL, TextUtils.join("\n", touchList));
        if ( ! chargerList.isEmpty())  hm.put(InfoUtils.CHARGER,    TextUtils.join("\n", chargerList));
        if ( ! accelerometerList.isEmpty()) hm.put(InfoUtils.ACCELEROMETER,   TextUtils.join("\n", accelerometerList));
        if ( ! magnetometerList.isEmpty())  hm.put(InfoUtils.MAGNETOMETER,    TextUtils.join("\n", magnetometerList));
        if ( ! alspsList.isEmpty())    hm.put(InfoUtils.ALSPS,      TextUtils.join("\n", alspsList));
        if ( ! pmicList.isEmpty())     hm.put(InfoUtils.PMIC,       TextUtils.join("\n", pmicList));
        if ( ! otherList.isEmpty())    hm.put(InfoUtils.UNKNOWN,    TextUtils.join("\n", otherList));

        return hm;
    }

    public static ArrayList<String> getMtkCameraList()
    {
        String fileName = "/system/lib/libcameracustom.so";

        String searchPattern = "SENSOR_DRVNAME_";

        ArrayList<String> cameraList  = new ArrayList<String>();

        ArrayList<String> mtkCameraList = BinaryDataHelper.getStringCapturedList(fileName, searchPattern);

        for (String cameraModel : mtkCameraList)
        {
            String cameraName = cameraModel.toLowerCase();

            cameraList.add(cameraName);
        }

        return cameraList;
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
