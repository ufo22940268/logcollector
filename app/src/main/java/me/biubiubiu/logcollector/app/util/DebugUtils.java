package me.biubiubiu.logcollector.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ccheng on 5/15/14.
 */
public class DebugUtils {
    public static void printStream(InputStream errorStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println("line = " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            errorStream.close();
        }
    }
}
