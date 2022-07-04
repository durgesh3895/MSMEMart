package com.upicon.app.BasicActivities;

import static android.util.Base64.encodeToString;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Product.ProductsAdmin;
import com.upicon.app.UsersList.AllUsers;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {

    SessionManager sessionManager;
    HashMap<String, String> user;
    Toolbar toolbar;
    TextView txt_edit_profile,txt_user_name,txt_user_fullname,txt_user_role,txt_user_mobile,txt_user_district,txt_user_address,txt_change_password,txt_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        Init();
        MyToolbar();
        AppBar();
        MyClickListener();
    }



    private void AppBar() {
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    toolbar.setTitle("");
                    //showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
                    toolbar.setTitle("");
                    //hideOption(R.id.action_info);
                }
            }
        });
    }


    private void Init() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();


        txt_edit_profile=findViewById(R.id.txt_edit_profile);
        txt_user_name =findViewById(R.id.txt_user_name);
        txt_user_fullname=findViewById(R.id.txt_user_fullname);
        txt_user_role=findViewById(R.id.txt_user_role);
        txt_user_mobile=findViewById(R.id.txt_user_mobile);
        txt_user_district=findViewById(R.id.txt_user_district);
        txt_user_address=findViewById(R.id.txt_user_address);

        txt_change_password=findViewById(R.id.txt_change_password);
        txt_logout=findViewById(R.id.txt_logout);


        txt_user_name.setText(user.get(SessionManager.KEY_FIRST_NAME)+" "+user.get(SessionManager.KEY_LAST_NAME));
        txt_user_fullname.setText(user.get(SessionManager.KEY_FIRST_NAME)+" "+user.get(SessionManager.KEY_LAST_NAME));
        txt_user_role.setText(user.get(SessionManager.KEY_ROLE));
        //txt_user_role.setText("User");

        txt_user_mobile.setText(user.get(SessionManager.KEY_MOBILE));
        txt_user_district.setText(user.get(SessionManager.KEY_DISTRICT));
        txt_user_address.setText(user.get(SessionManager.KEY_ADDRESS));









    }


    private void MyToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
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


    private void MyClickListener() {
        txt_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordView();
            }
        });
        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutPopUp();
            }
        });



    }

    private void LogoutPopUp() {
        Dialog dialog=new Dialog(UserProfile.this);
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

    public void ChangePasswordView(){

        Dialog dialog=new Dialog(UserProfile.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.change_password_dialog);
        dialog.show();
        Window window=dialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bg);
        window.setLayout(700, LinearLayout.LayoutParams.WRAP_CONTENT);

        EditText et_old_password=(EditText) dialog.findViewById(R.id.et_old_password);
        EditText et_password=(EditText) dialog.findViewById(R.id.et_password);
        EditText et_confirm_password=(EditText) dialog.findViewById(R.id.et_confirm_password);


        Button btn_cancel=(Button)dialog.findViewById(R.id.btn_cancel);
        Button btn_update=(Button)dialog.findViewById(R.id.btn_update);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_old_password.getText().toString().isEmpty()){
                    UtilsMethod.INSTANCE.errorToast(getApplicationContext(),et_old_password.getHint().toString());
                }
                if(et_password.getText().toString().isEmpty()){
                    UtilsMethod.INSTANCE.errorToast(getApplicationContext(),et_password.getHint().toString());
                }
                else if(et_password.getText().toString().length()<5){
                    UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Minimum 5 digit password required");
                }
                else if(et_confirm_password.getText().toString().isEmpty()){
                    UtilsMethod.INSTANCE.errorToast(getApplicationContext(),et_confirm_password.getHint().toString());
                }
                else if(!et_confirm_password.getText().toString().equals(et_password.getText().toString())){
                    UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Confirm password not matched");
                }
                else{
                    dialog.dismiss();
                    ChangePassword(et_old_password.getText().toString(),et_confirm_password.getText().toString());
                }

            }
        });


    }

    private void ChangePassword(String old_password, String new_password) {

        ProgressDialog pd=new ProgressDialog(UserProfile.this);
        pd.setMessage("Updating please wait.....");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(UserProfile.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dc_response",response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            if(jsonObject.get("Response").equals(true)){

                                sessionManager.logoutUser();
                                UtilsMethod.INSTANCE.successToast(getApplicationContext(),jsonObject.getString("Message"));

                            }
                            else {
                                UtilsMethod.INSTANCE.errorToast(getApplicationContext(),jsonObject.getString("Message"));

                            }

                        }
                        catch (JSONException e) { e.printStackTrace(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Log.e("dc_error",error.toString());

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Token "+user.get(SessionManager.KEY_MOBILE));
                return headers;
            }
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user.get(SessionManager.KEY_ID));
                params.put("user_role",user.get(SessionManager.KEY_ROLE));
                params.put("old_password", old_password);
                params.put("new_password", new_password);
                params.put("token",BaseURL.TOKEN);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


}