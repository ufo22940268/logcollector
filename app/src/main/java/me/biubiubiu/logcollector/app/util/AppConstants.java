package me.biubiubiu.logcollector.app.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by ccheng on 5/15/14.
 */
public class AppConstants {
    public static final String ACTION_LOGCAT_STOPPED = "ACTION_LOGCAT_STOPPED";

    public static final String SCARD_DB = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "a.db";
    public static final String SDCARD_LOG = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "a.log";
}
