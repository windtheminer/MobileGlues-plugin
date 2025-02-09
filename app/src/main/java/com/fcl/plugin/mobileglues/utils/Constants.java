package com.fcl.plugin.mobileglues.utils;

import android.os.Environment;

public class Constants {

    public static final String MG_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/MG";

    public static final String CONFIG_FILE_PATH = MG_DIRECTORY + "/config.json";

}
