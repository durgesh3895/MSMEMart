package com.upicon.app.AppController;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.upicon.app.BasicActivities.Login;
import java.util.HashMap;

public class SessionManager {

    private SharedPreferences pref;
    private Editor editor;
    private Context context;

    private static final String PREF_NAME = "UPICon";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String SESSION = "email";

    public static final String KEY_ID = "id";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_DISTRICT = "district";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_ROLE = "role";



    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context){
        this.context = context;
        int PRIVATE_MODE = 0;
        try{
            pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }
        catch (Exception e){
            Log.e("exception",e.toString());
        }

    }


    public void CreateLoginSession(String id, String first_name,String last_name, String mobile, String district,String address,String role){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_FIRST_NAME, first_name);
        editor.putString(KEY_LAST_NAME, last_name);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_DISTRICT, district);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }



    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(SESSION, pref.getString(SESSION, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_FIRST_NAME, pref.getString(KEY_FIRST_NAME, null));
        user.put(KEY_LAST_NAME, pref.getString(KEY_LAST_NAME, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_DISTRICT, pref.getString(KEY_DISTRICT, null));
        user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
        user.put(KEY_ROLE, pref.getString(KEY_ROLE, null));
        return user;
    }



    public void logoutUser(){
        Intent i = new Intent(context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
        editor.clear();
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
