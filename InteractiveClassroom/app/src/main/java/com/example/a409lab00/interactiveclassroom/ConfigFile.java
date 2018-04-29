package com.example.a409lab00.interactiveclassroom;

import android.graphics.Bitmap;

/**
 * Created by ming on 2017/11/25.
 */

public class ConfigFile {
    private WebApi webapi;

    public ConfigFile()
    {
        webapi=new WebApi();
        BLEinterval=Integer.valueOf(webapi.GET("UtilAPI/BLEinterval"));
    }
    public static boolean enableBLE=true;
    public static int BLEinterval;

    public static String version="16";


    public static String IP="140.130.33.228";
}

