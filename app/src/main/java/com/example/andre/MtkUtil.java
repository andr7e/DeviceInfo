package com.example.andre;

import android.text.TextUtils;

import com.example.andre.androidshell.ShellExecuter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by andrey on 01.03.16.
 */
public class MtkUtil
{
    public static ArrayList<String> getMtkCameraList()
    {
        String fileName = "/system/lib/libcameracustom.so";

        String searchPattern = "SENSOR_DRVNAME_";

        ArrayList<String> cameraList  = new ArrayList<String>();

        ArrayList<String> mtkCameraList = BinaryDataHelper.getStringCapturedList(fileName, searchPattern, 100);

        for (String cameraModel : mtkCameraList)
        {
            String cameraName = cameraModel.toLowerCase();

            cameraList.add(cameraName);
        }

        return cameraList;
    }

    public static String[] getFields ()
    {
        String[]  fields = {
                "MODEL",
                "MTK_PLATFORM",
                "LCM_HEIGHT",
                "LCM_WIDTH",
                "CUSTOM_KERNEL_LCM",
                "CUSTOM_KERNEL_TOUCHPANEL",
                "CUSTOM_HAL_IMGSENSOR",
                "CUSTOM_HAL_MAIN_IMGSENSOR",
                "CUSTOM_HAL_SUB_IMGSENSOR",
                "CUSTOM_KERNEL_MAIN_LENS",
                "CUSTOM_KERNEL_SOUND",
                "CUSTOM_KERNEL_ACCELEROMETER",
                "CUSTOM_KERNEL_ALSPS",
                "CUSTOM_KERNEL_MAGNETOMETER",
                "CUSTOM_MODEM",
                "COMMENTS"
        };

        return fields;
    }

    public static String convertFields (String mtkField)
    {
        if (mtkField.equals("CUSTOM_KERNEL_LCM"))
        {
            return InfoUtils.LCM;
        }
        if (mtkField.equals("CUSTOM_KERNEL_TOUCHPANEL"))
        {
            return InfoUtils.TOUCHPANEL;
        }
        if (mtkField.equals("CUSTOM_HAL_IMGSENSOR"))
        {
            return InfoUtils.CAMERA;
        }
        if (mtkField.equals("CUSTOM_HAL_MAIN_IMGSENSOR"))
        {
            return InfoUtils.CAMERA_BACK;
        }
        if (mtkField.equals("CUSTOM_HAL_SUB_IMGSENSOR"))
        {
            return InfoUtils.CAMERA_FRONT;
        }
        if (mtkField.equals("CUSTOM_KERNEL_MAIN_LENS"))
        {
            return InfoUtils.LENS;
        }
        if (mtkField.equals("CUSTOM_KERNEL_ACCELEROMETER"))
        {
            return InfoUtils.ACCELEROMETER;
        }
        if (mtkField.equals("CUSTOM_KERNEL_ALSPS"))
        {
            return InfoUtils.ALSPS;
        }
        if (mtkField.equals("CUSTOM_KERNEL_MAGNETOMETER"))
        {
            return InfoUtils.MAGNETOMETER;
        }
        if (mtkField.equals("CUSTOM_KERNEL_GYROSCOPE"))
        {
            return InfoUtils.GYROSCOPE;
        }
        if (mtkField.equals("CUSTOM_KERNEL_SOUND"))
        {
            return InfoUtils.SOUND;
        }
        if (mtkField.equals("CUSTOM_MODEM"))
        {
            return InfoUtils.MODEM;
        }

        return "";
    }

    public static HashMap<String,String> getProjectDriversHash()
    {
        HashMap<String,String> hash = new HashMap<String,String>();

        String fileName = "/system/data/misc/ProjectConfig.mk";

        String[] allowKeys = getFields();

        String text = IOUtil.getFileText(fileName);

        String[] lines = text.split("\n");

        for (String line : lines)
        {
            if (line.isEmpty()) continue;

            if (line.charAt(0) != '#')
            {
                if (line.startsWith("CUSTOM")||
                        line.startsWith("LCM") ||
                    line.startsWith("MTK_"))
                {
                    String[] strList = line.split("=");

                    if (strList.length >= 2)
                    {
                        String key   = strList[0].trim();
                        String value = strList[1];

                        // ignore comment
                        int pos = value.indexOf("#");

                        if (pos != -1)
                        {
                            value = value.substring(0, pos);
                        }

                        System.out.println(key);
                        System.out.println(value);

                        // contains key
                        for (String allowKey : allowKeys)
                        {
                            if (key.contains(allowKey))
                            {
                                String convKey = convertFields (key);

                                System.out.println("!!!!!!!!!!!!!!");

                                String[] valueList = value.trim().split(" ");

                                String res = TextUtils.join("\n", valueList);

                                hash.put(convKey, res);
                                break;
                            }
                        }
                    }
                }
            }
        }

        return hash;
    }
}
