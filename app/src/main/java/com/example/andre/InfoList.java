package com.example.andre;

import android.os.Build;
import android.util.Pair;

import com.example.andre.androidshell.ShellExecuter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andrey on 03.03.16.
 */
public class InfoList
{
    public static void addItem (ArrayList< Pair<String, String> > objList, String key, String value)
    {
        if ( ! value.isEmpty())
        {
            objList.add(new Pair<String, String>(key, value));
        }
    }

    public static ArrayList< Pair<String, String> > buildInfoList(boolean isRootMode)
    {
        ShellExecuter exec = new ShellExecuter();

        ArrayList< Pair<String, String> > objList = new ArrayList< Pair<String, String> >();

        String platform = InfoUtils.getPlatform();

        addItem(objList, "Manufacturer", InfoUtils.getManufacturer());
        addItem(objList, "Model", InfoUtils.getModel());
        addItem(objList, "Brand", InfoUtils.getBrand());

        addItem(objList, "Resolution", InfoUtils.getResolution());

        addItem(objList, "Platform", platform);

        addItem(objList, "Android Version", InfoUtils.getAndroidVersion());
        addItem(objList, "API", InfoUtils.getAndroidAPI());

        addItem(objList, "Kernel", InfoUtils.getKernelVersion(exec));

        //
        HashMap<String,String> hash = InfoUtils.getDriversHash(exec);

        //
        String cmdline = "";

        if (isRootMode)
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

        return objList;
    }
}
