package com.example.andre;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by andrey on 01.03.16.
 */
public class IOUtil
{
    public static String getFileText (String fileName)
    {
        StringBuffer output = new StringBuffer();

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            int i = 0;
            String line = "";
            while ((line = reader.readLine())!= null)
            {
                if (i != 0) output.append("\n");

                output.append(line);

                i++;
            }

        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        String response = output.toString();

        return response;
    }
}
