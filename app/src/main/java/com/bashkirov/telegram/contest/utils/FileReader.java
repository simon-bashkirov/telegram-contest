package com.bashkirov.telegram.contest.utils;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("CharsetObjectCanBeUsed")
public class FileReader {

    public static String readStringFromAsset(Context context, String fileName) {
        String string;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            string = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return string;
    }
}
