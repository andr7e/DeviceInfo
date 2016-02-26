package com.example.andre;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrey on 26.02.16.
 */
public class BinaryDataHelper
{
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

            if (mybyte == terminator) break;

            outputBytes.add(mybyte);

            offset += read;
        }

        return outputBytes;
    }

    // maxStopBytes used for reduce time where searched values in near area
    public static ArrayList<String> getStringCapturedList(String fileName, String searchPattern, int maxStopBytes)
    {
        ArrayList<String> strList = new ArrayList<String>();

        //System.out.println(fileNmae);

        try
        {
            File file = new File(fileName);

            InputStream in = null;
            try
            {
                byte[] searchPatternBytes = searchPattern.getBytes();

                int BUFFER_SIZE = 1;

                in = new BufferedInputStream(new FileInputStream(file));

                byte[] buffer = new byte[BUFFER_SIZE];

                int offset = 0;
                int i = 0;
                int read = 0;
                int matched = 0;
                int matchedOffset = 0;
                while (((read = in.read(buffer)) != -1))
                {
                    offset += read;

                    byte curByte = buffer[0];

                    if (matched == searchPattern.length())
                    {
                        //System.out.println("found");

                        List<Byte> bytes = BinaryDataHelper.readBytes(in, (byte) 0);

                        String str = (char)curByte + BinaryDataHelper.byteListToString(bytes);

                        //System.out.println(str);

                        strList.add(str);

                        matched = 0;

                        matchedOffset = offset;
                    }

                    if (curByte == searchPatternBytes[matched])
                    {
                        matched++;
                    }
                    else
                    {
                        matched = 0;
                    }

                    // check if need stop
                    if (matchedOffset > 0 && offset - matchedOffset > maxStopBytes) break;

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

        return strList;
    }
}
