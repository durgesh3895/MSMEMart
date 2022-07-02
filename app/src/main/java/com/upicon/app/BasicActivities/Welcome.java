package com.upicon.app.BasicActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Dashboards.DashBoard;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import java.util.HashMap;

public class Welcome extends AppCompatActivity {

    SessionManager sessionManager;
    HashMap<String, String> user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        changeStatusBarColor();
        initialization();


    }

    private void initialization() {

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();


        TextView tag_line = (TextView) findViewById(R.id.welcome_title);
        Animation img = new TranslateAnimation(Animation.ABSOLUTE,Animation.ABSOLUTE, 200,Animation.ABSOLUTE);
        img.setDuration(2000);
        img.setFillAfter(true);
        tag_line.startAnimation(img);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(BaseURL.isOnline(getApplicationContext())){
                    StartActivity();
                }
                else{
                    UtilsMethod.INSTANCE.errorToast(Welcome.this,"Please turn on data connection");
                }


            }
        },2000);

    }

    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void StartActivity() {
        if(sessionManager.isLoggedIn()){
            Intent intent=new Intent(getApplicationContext(), DashBoard.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        }
        else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}