package com.upicon.app.UtilsMethod;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.upicon.app.AppController.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import es.dmoral.toasty.Toasty;

public enum UtilsMethod {
    INSTANCE;

    SessionManager sessionManager;
    HashMap<String, String> user;


    public void  successToast(Context context,String message){
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public void  errorToast(Context context,String message){
        Toasty.error(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public void  infoToast(Context context,String message){
        Toasty.info(context, message, Toast.LENGTH_SHORT, true).show();
    }

    public String setApiResponse(Context context, String response) {
        String return_value = "";
        sessionManager = new SessionManager(context);
        user = sessionManager.getUserDetails();

        try {
            JSONObject obj = new JSONObject(response);
            if (obj.get("Response").equals(false)) {
                errorToast(context,obj.getString("Message"));
                //sessionManager.logoutUser();
            } else if (obj.get("Response").equals(true)) {
                return_value = obj.getString("Data");
            } else if (obj.get("Response").equals(true)) {
                successToast(context,obj.getString("Message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return return_value;

    }

    public String convertAmPm(String timing) {

        StringTokenizer tk = new StringTokenizer(timing);
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
        Date dt = null;
        try {
            dt = sdf.parse(time);
            //holder.user_business_timing.setText(sdfs.format(dt)+" to "+userssBusiness.getClose_time());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sdfs.format(dt);
    }

    public void callToNumber(Context context, String mobile_number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobile_number, null));
        Intent chooser = Intent.createChooser(intent, "Please Launch call manager");
        context.startActivity(chooser);
    }

    public void hideKeyboard(Context context, android.view.View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }





}
