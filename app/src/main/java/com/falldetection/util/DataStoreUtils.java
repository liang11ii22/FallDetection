package com.falldetection.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liang
 */
public class DataStoreUtils {
    private static final int MODE_PRIVATE = 0;
    private SharedPreferences sp;

    public DataStoreUtils(Context ctx) {
        sp = ctx.getSharedPreferences("USER", MODE_PRIVATE);// get SharedPreferences object
    }

    /**
     * store data
     * @param name
     * @param phone
     * @param age
     * @param height
     * @param weight
     * @param sex
     */
    public void store(String name, String phone, String age, String height, String weight, String sex){
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_NAME", name);
        editor.putString("USER_PHONE", phone);
        editor.putString("USER_AGE", age);
        editor.putString("USER_HEIGHT", height);
        editor.putString("USER_WEIGHT", weight);
        editor.putString("USER_SEX", sex);
        editor.commit();
    }

    public String getPhone(){
        return sp.getString("USER_PHONE", null);
    }
    public List<String> getAll(){
        List<String> list = new ArrayList<String>();
        list.add(sp.getString("USER_NAME", null));
        list.add(sp.getString("USER_AGE", null));
        list.add(sp.getString("USER_HEIGHT", null));
        list.add(sp.getString("USER_WEIGHT", null));
        list.add(sp.getString("USER_SEX", null));
        return list;
    }
}
