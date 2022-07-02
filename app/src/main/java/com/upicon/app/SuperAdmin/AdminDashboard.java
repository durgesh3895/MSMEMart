package com.upicon.app.SuperAdmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.upicon.app.Adapters.SlidingImageAdapter;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.BasicActivities.UserProfile;
import com.upicon.app.Product.ProductsAdmin;
import com.upicon.app.R;
import com.upicon.app.UsersList.AllUsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class AdminDashboard extends AppCompatActivity {

    SessionManager sessionManager;
    HashMap<String, String> user;

    ArrayList<String> dcurls;
    private static int mcurrentPage = 0;

    CardView all_product_card,all_user_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        initialization();
        toolBar();
        clickListener();
        mainSliderImages();
    }



    private void initialization() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        all_product_card=(CardView) findViewById(R.id.all_product_card);
        all_user_card=(CardView) findViewById(R.id.all_user_card);

    }

    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Admin Dashboard");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void clickListener() {

        all_user_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AdminDashboard.this, AllUsers.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
        all_product_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AdminDashboard.this, ProductsAdmin.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
    }

    private void mainSliderImages() {

        dcurls = new ArrayList<>();


        dcurls.add(BaseURL.IMAGE_PATH_SLIDER+"b1.jpg");
        dcurls.add(BaseURL.IMAGE_PATH_SLIDER+"b2.jpg");
        dcurls.add(BaseURL.IMAGE_PATH_SLIDER+"b3.jpg");

        ViewPager mainViewpager = (ViewPager)findViewById(R.id.main_slider_viewpager);
        mainViewpager.setAdapter(new SlidingImageAdapter(getApplicationContext(),dcurls));

        TextView tv_dot=(TextView)findViewById(R.id.tv_dot);
        //DotsIndicator pageIndicatorView =findViewById(R.id.dots_indicator);
        //pageIndicatorView.addDot(1);
        //pageIndicatorView.setCount(dcurls.size());
        //pageIndicatorView.setSelection(0);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (mcurrentPage ==  dcurls.size()) { mcurrentPage = 0;}
                mainViewpager.setCurrentItem(mcurrentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 3000);

        mainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {

                //pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });
    }
}