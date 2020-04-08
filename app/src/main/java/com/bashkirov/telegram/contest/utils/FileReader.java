package com.bashkirov.telegram.contest.utils;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Provides utility to read files
 */

public class FileReader {
    /**
     * Reads data form file into single string format
     *
     * @param context  context to get access to app assets
     * @param fileName requested file name
     * @return file content as single string
     */
    public static String readStringFromAsset(Context context, String fileName) {
        String string;
        try (InputStream is = context.getAssets().open(fileName)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                string = new String(buffer, StandardCharsets.UTF_8);
            } else {
                //noinspection CharsetObjectCanBeUsed
                string = new String(buffer, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return string;
    }
}