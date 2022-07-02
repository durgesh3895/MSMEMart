package com.upicon.app.Dashboards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.upicon.app.AppController.BottomNavigationBehavior;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.R;
import com.upicon.app.Cart.Cart;

import java.util.HashMap;

public class DashBoard extends AppCompatActivity{

    SessionManager sessionManager;
    HashMap<String, String> user;
    ImageView iv_cart;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_board);

        initialization();
        clickListeners();

    }


    long back_pressed;
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
        else{
            Toast.makeText(this,"Hit back again to close app", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    private void initialization() {

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        iv_cart=(ImageView)findViewById(R.id.iv_cart);
        tv_title=(TextView) findViewById(R.id.txt_title);



        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        bottomNavigationView.setSelectedItemId(R.id.navigationHome);




    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigationFavorite:
                    fragment = new FavoriteFragment();
                    tv_title.setText("Wishlist");
                    loadFragment(R.id.frame_container,fragment);
                    return true;
                case R.id.navigationOffer:
                    fragment = new CategoryFragment();
                    tv_title.setText("Category");
                    loadFragment(R.id.frame_container,fragment);
                    return true;
                case R.id.navigationHome:
                    fragment = new HomeFragment();
                    tv_title.setText("UP MSME Mart");
                    loadFragment(R.id.frame_container,fragment);
                    return true;
                case R.id.navigationProfile:
                    fragment = new UserFragment();
                    tv_title.setText("Profile");
                    loadFragment(R.id.frame_container,fragment);
                    return true;
            }

            return false;
        }
    };


    private void loadFragment(int view ,Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(view, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void clickListeners() {

        iv_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashBoard.this,Cart.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

    }



}