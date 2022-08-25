package com.upicon.app.UsersList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.BasicActivities.Login;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccount extends AppCompatActivity implements  android.view.View.OnFocusChangeListener{

    SessionManager sessionManager;
    HashMap<String, String> user;
    EditText et_name,et_mobile_number,et_password,et_c_password,et_address;
    Button btn_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        initialization();
        toolBar();
        clickListener();
    }

    private void initialization() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();


        et_name=findViewById(R.id.et_name);
        et_mobile_number=findViewById(R.id.et_mobile_number);
        et_password=findViewById(R.id.et_password);
        et_c_password=findViewById(R.id.et_c_password);
        et_address=findViewById(R.id.et_address);

        btn_create=findViewById(R.id.btn_create);

        et_mobile_number.setOnFocusChangeListener(this);

    }

    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create account");
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void clickListener() {

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validateForm();
            }
        });
    }


    private void validateForm() {
        if (et_name.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter name");
        }
        else if (et_name.getText().toString().length()<3){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Name is too short");
        }
        else if (et_mobile_number.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter mobile number");
        }
        else if (et_mobile_number.getText().toString().length()<10){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter 10 digit mobile number");
        }
        else if (et_password.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter password");
        }
        else if (et_password.getText().toString().length()<5){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Password required minimum 5 digits");
        }
        else if (et_c_password.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter confirm password");
        }
        else if (!et_c_password.getText().toString().equals(et_password.getText().toString())){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Confirm password should be same as password");
        }
        else if (et_address.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter address");
        }
        else if (et_address.getText().toString().length()<10){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter full address");
        }
        else{
            createAccount();
        }
    }

    private void createAccount() {
        final ProgressDialog pd=new ProgressDialog(CreateAccount.this);
        pd.setMessage("Creating account....");
        pd.setCancelable(false);
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.REGISTER_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SetUserResponse(response);
                        pd.dismiss();
                        Log.e("response",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Log.e("error",error.toString());

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
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name",et_name.getText().toString());
                params.put("mobile",et_mobile_number.getText().toString());
                params.put("password",et_password.getText().toString());
                params.put("address",et_address.getText().toString());
                params.put("role","User");
                params.put("token",BaseURL.TOKEN);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void SetUserResponse(String response) {

        try {
            JSONObject obj = new JSONObject(response);
            if(obj.get("Response").equals(true)){
                UtilsMethod.INSTANCE.successToast(CreateAccount.this,obj.getString("Message"));
                Intent intent=new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
            else if(obj.get("Response").equals(false)){
                UtilsMethod.INSTANCE.errorToast(CreateAccount.this,obj.getString("Message"));
            }
            else {
                UtilsMethod.INSTANCE.errorToast(CreateAccount.this,"Something went wrong");
            }

        }

        catch (JSONException e) { e.printStackTrace(); }
    }

    @Override
    public void onFocusChange(android.view.View view, boolean b) {
        if (!b){
            UtilsMethod.INSTANCE.hideKeyboard(CreateAccount.this,view);
        }
    }
}