package com.rpm.realmexamples.utils;

import android.util.Log;

import java.util.Random;

/**
 * Created by RPM on 31/7/18.
 */

public class AppUtil {
    private static String TAG = AppUtil.class.getSimpleName();

    public static Random RANDOM = new Random();
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }
        Log.d(TAG, "randomString: " + sb.toString());
        return sb.toString();
    }
}
