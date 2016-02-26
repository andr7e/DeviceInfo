package com.example.andre;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;

import com.example.andre.androidshell.ShellExecuter;

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

    /*
    public static String[] getDriversList(ShellExecuter se)
    {
        String command = "ls cat /sys/bus/i2c/drivers";

        String out = se.execute(command);

        String[] list = out.split("\n");

        return list;
    }
    */

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

    public static ArrayList<String> getDeviceList(File dir)
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

        ArrayList<String>  list = getDeviceList(dir);

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

    public static List<Byte> readBytes(InputStream in, byte terminator) throws IOException
    {
        int BUFFER_SIZE = 1;

        byte[] b = new byte[BUFFER_SIZE];

        List<Byte> outputBytes = new ArrayList<Byte>();

        int offset = 0;
        int i = 0;
        int read = 0;

        while (((read = in.read(b)) != -1))
        {
            byte mybyte = b[0];

            //System.out.println(mybyte);
            //System.out.println((char) mybyte);

            if (mybyte == terminator) break;

            outputBytes.add(mybyte);

            offset += read;
        }

        return outputBytes;
    }

    public static String bytesToString (byte[] bytes)
    {
        String str = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            str = new String(bytes, StandardCharsets.UTF_8);
        }
        else
        {
            str = new String(bytes, Charset.forName("UTF-8"));
        }

        return str;
    }

    public static String byteListToString(List<Byte> bytes)
    {
        int count = bytes.size();

        if (count > 0)
        {
            byte[] array = new byte[bytes.size()];
            int i = 0;
            for (Byte cur : bytes)
            {
                array[i] = cur;
                i++;
            }

            return bytesToString(array);
        }

        return "";
    }

    public static ArrayList<String> getMtkCameraList()
    {
        ArrayList<String> cameraList = new ArrayList<String>();

        String fileNmae = "/system/lib/libcameracustom.so";

        System.out.println(fileNmae);

        try
        {
            File file = new File(fileNmae);

            String searchPattern = "SENSOR_DRVNAME_";

            InputStream in = null;
            try
            {
                byte[] searchPatternBytes = searchPattern.getBytes();

                int BUFFER_SIZE = 1;

                in = new BufferedInputStream(new FileInputStream(file));

                byte[] b = new byte[BUFFER_SIZE];

                int offset = 0;
                int i = 0;
                int read = 0;
                int matched = 0;
                while (((read = in.read(b)) != -1))
                {
                    //for (int c = 0; c < 4; c++)
                    //System.out.println(b[c]);

                    offset += read;

                    byte mybyte = b[0];

                    if (matched == searchPattern.length())
                    {
                        //System.out.println(mybyte);

                        System.out.println("found");

                        List<Byte> bytes = readBytes(in, (byte) 0);

                        String str = (char)mybyte + byteListToString(bytes);

                        System.out.println(str);

                        cameraList.add(str);

                        matched = 0;
                    }

                    if (mybyte == searchPatternBytes[matched])
                    {
                        matched++;
                    }
                    else
                    {
                        matched = 0;
                    }

                    i++;
                }
            }
            finally
            {
                if (in != null)
                {
                    in.close();
                }
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }

        return cameraList;
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

        for (String cameraModel : mtkCameraList)
        {
            String cameraName = cameraModel.toLowerCase();

            cameraList.add(cameraName);
        }

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
