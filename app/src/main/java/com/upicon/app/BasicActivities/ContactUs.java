package com.upicon.app.BasicActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.R;

import java.util.HashMap;
import java.util.Locale;

public class ContactUs extends AppCompatActivity {

    ImageView fab;
    SessionManager sessionManager;
    HashMap<String, String> user;

    TextView txt_enquiry,txt_tech_support,txt_call,txt_whatsapp,txt_address;
    String website="http://upmsmemart.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        MyCollapsingToolbar();
        MyToolbar();
        Init();
        MyFavBtn();

    }

    private void MyToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Contact Us");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

    }

    private void MyCollapsingToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                final Typeface tf = ResourcesCompat.getFont(getApplicationContext(),R.font.quicksand_regular);
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {

                    collapsingToolbarLayout.setTitle("Contact us");
                    collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
                    fab.setVisibility(View.GONE);
                    isShow = true;

                } else if(isShow) {
                    collapsingToolbarLayout.setTitle("Contact us");
                    collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
                    isShow = false;
                    fab.setVisibility(View.VISIBLE);
                }
            }

        });

    }

    private void Init() {
        CardView enquery=(CardView)findViewById(R.id.enquiry_card);
        CardView techsupport=(CardView)findViewById(R.id.techsupport_card);
        CardView call=(CardView)findViewById(R.id.call_card);
        CardView whatsapp=(CardView)findViewById(R.id.whatapp_card);
        CardView address=(CardView)findViewById(R.id.address_card);


        txt_enquiry=findViewById(R.id.txt_enquiry);
        txt_tech_support=findViewById(R.id.txt_support);
        txt_call=findViewById(R.id.txt_call);
        txt_whatsapp=findViewById(R.id.txt_whatsapp);
        txt_address=findViewById(R.id.txt_address);


        enquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GotoEnquery();

            }
        });

        techsupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GotoSupport();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GotoCall();
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Whatsapp();
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GotoAddress();
            }
        });

    }


    private void MyFavBtn() {
        fab = (ImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = website;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    private void GotoEnquery() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("email"));
        String[] s={txt_enquiry.getText().toString()};
        intent.putExtra(Intent.EXTRA_EMAIL,s);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Enquiry Message");
        intent.putExtra(Intent.EXTRA_TEXT,"I want to enquire from you that");
        intent.setType("message/rfc822");
        Intent chooser= Intent.createChooser(intent,"Please Launch Email");
        startActivity(chooser);

    }

    private void GotoSupport() {
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("email"));
        String[] s={txt_tech_support.getText().toString()};
        intent.putExtra(Intent.EXTRA_EMAIL,s);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Tech Support Message");
        intent.putExtra(Intent.EXTRA_TEXT,"I want to enquire from you that ");
        intent.setType("message/rfc822");
        Intent chooser= Intent.createChooser(intent,"Please Launch Email");
        startActivity(chooser);

    }

    private void GotoCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", txt_call.getText().toString(), null));
        Intent chooser= Intent.createChooser(intent,"Please Launch call manager");
        startActivity(chooser);
    }

    private void Whatsapp() {

        try {
            String text = " ";// Replace with your message.
            String toNumber ="+91"+txt_whatsapp.getText().toString(); // Replace with mobile phone number without +Sign or leading zeros, but with country code
            //Suppose your country is India and your phone number is “xxxxxxxxxx”, then you need to send “91xxxxxxxxxx”.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+toNumber +"&text="+text));
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    private void GotoAddress() {
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse("https://goo.gl/maps/j6EX8Z2yxBENtbLH7"));
//        startActivity(intent);
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", 26.86455238185675, 81.01194861310196);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}