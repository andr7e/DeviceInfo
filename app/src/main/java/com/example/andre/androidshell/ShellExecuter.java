/**
 * Created by andre on 22.02.16.
 */

package com.example.andre.androidshell;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellExecuter
{

    public ShellExecuter()
    {

    }

    public String execute (String command)
    {
        StringBuffer output = new StringBuffer();

        Process p;
        try
        {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

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
            e.printStackTrace();
        }

        String response = output.toString();

        return response;
    }
}