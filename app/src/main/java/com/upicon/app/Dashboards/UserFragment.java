package com.upicon.app.Dashboards;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.BasicActivities.ContactUs;
import com.upicon.app.BasicActivities.UserProfile;
import com.upicon.app.R;
import com.upicon.app.SuperAdmin.AdminDashboard;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserFragment extends androidx.fragment.app.Fragment {

    View view;
    SessionManager sessionManager;
    HashMap<String, String> user;

    TextView user_name,user_role;

    CardView super_admin_card,profile_card,contact_us_card,about_app_card,rate_app_card,share_app_card,privacy_card,logout_card;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);

        Init();
        ClickListeners();

        return view;
    }



    private void Init() {
        sessionManager = new SessionManager(getContext());
        user = sessionManager.getUserDetails();



        user_name=view.findViewById(R.id.user_name);
        user_role=view.findViewById(R.id.user_role);

        super_admin_card=view.findViewById(R.id.super_admin_card);


        profile_card=view.findViewById(R.id.profile_card);
        contact_us_card=view.findViewById(R.id.contact_us_card);
        about_app_card=view.findViewById(R.id.about_app_card);
        rate_app_card=view.findViewById(R.id.rate_app_card);
        share_app_card=view.findViewById(R.id.share_app_card);
        logout_card=view.findViewById(R.id.logout_card);



        user_name.setText(user.get(SessionManager.KEY_NAME));
        user_role.setText(user.get(SessionManager.KEY_MOBILE));


        if (Objects.equals(user.get(SessionManager.KEY_ROLE), "Super Admin")||Objects.equals(user.get(SessionManager.KEY_ROLE), "Admin")){
            super_admin_card.setVisibility(View.VISIBLE);
        }
        else {
            super_admin_card.setVisibility(View.GONE);
        }



    }

    private void ClickListeners() {
        profile_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UserProfile.class));
                requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        super_admin_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AdminDashboard.class));
                requireActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });

        logout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogoutPopUp();
            }
        });
        contact_us_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(getActivity(), ContactUs.class));
               getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
        rate_app_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateApp();
            }
        });
        share_app_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareApp();
            }
        });
    }

    private void LogoutPopUp() {
        Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.logout_dialog);
        dialog.show();
        Window window=dialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bg);
        window.setLayout(700, LinearLayout.LayoutParams.WRAP_CONTENT);

        Button btn_cancel=(Button)dialog.findViewById(R.id.btn_cancel);
        Button btn_yes=(Button)dialog.findViewById(R.id.btn_yes);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sessionManager.logoutUser();

            }
        });
    }



    private void RateApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            startActivity(goToMarket);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +getActivity().getPackageName())));
        }
    }

    private void ShareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out UP MSMSE Mart app at: https://play.google.com/store/apps/details?id="+getActivity().getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
