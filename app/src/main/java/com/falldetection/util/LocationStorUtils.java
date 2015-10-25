package com.falldetection.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2015/10/24.
 */
public class LocationStorUtils {
    private static final int MODE_PRIVATE = 0;
    private SharedPreferences sp;

    public LocationStorUtils(Context ctx) {
        sp = ctx.getSharedPreferences("LOCATION", MODE_PRIVATE);// get SharedPreferences object
    }

    /**
     * store data
     */
    public void store(String name){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("LOCATION_DES", name);
        editor.commit();
    }

    public String getAddress(){
        return sp.getString("LOCATION_DES", null);
    }
}
