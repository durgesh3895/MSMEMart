package com.upicon.app.BasicActivities;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Dashboards.DashBoard;
import com.upicon.app.R;
import com.upicon.app.UsersList.CreateAccount;
import com.upicon.app.UtilsMethod.UtilsMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    SessionManager sessionManager;
    HashMap<String, String> user;

    EditText et_mobile_number,et_password;
    Button btn_password_login;
    TextView txt_forget_password,txt_new_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        changeStatusBarColor();
        initialization();

    }

    private void initialization() {

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        et_mobile_number=findViewById(R.id.et_mobile_number);
        et_password=findViewById(R.id.et_password);

        btn_password_login=findViewById(R.id.btn_password_login);
        txt_forget_password=findViewById(R.id.txt_forget_password);
        txt_new_account=findViewById(R.id.txt_new_account);

        btn_password_login.setOnClickListener(this);
        txt_forget_password.setOnClickListener(this);
        txt_new_account.setOnClickListener(this);

    }

    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_password_login:
                validateLogin();
                break;
            case R.id.txt_forget_password:
                Intent intent2=new Intent(getApplicationContext(),ContactUs.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                break;
            case R.id.txt_new_account:
                Intent intent=new Intent(getApplicationContext(), CreateAccount.class);
                intent.putExtra("flag","login");
                intent.putExtra("mobile","");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                break;
        }
    }

    private void validateLogin() {
        if(et_mobile_number.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(Login.this,et_mobile_number.getHint().toString());
        }
        else if(et_mobile_number.getText().toString().length()<10){
            UtilsMethod.INSTANCE.errorToast(Login.this,"Enter valid mobile number");
        }
        else if(et_password.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(Login.this,et_password.getHint().toString());
        }
        else{
            passwordLogin();
        }
    }

    private void passwordLogin() {

        final ProgressDialog pd=new ProgressDialog(Login.this);
        pd.setMessage("Authenticating....");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Log.e("volleyResponse",response);

                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            if(jsonObject.get("Response").equals(true)){
                                setLoginResponse(jsonObject.getString("Data"));
                            }
                            else if (jsonObject.get("Response").equals(false)){
                                UtilsMethod.INSTANCE.errorToast(getApplicationContext(),jsonObject.getString("Message"));
                            }
                            else {
                                UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Something went wrong");
                            }

                        }
                        catch (JSONException e) { e.printStackTrace(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Log.e("volleyError",error.toString());
                    }
                }
        ) {
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", et_mobile_number.getText().toString());
                params.put("password",et_password.getText().toString());
                params.put("token",BaseURL.TOKEN);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void setLoginResponse(String response) {

        try {
            JSONArray jsonarray = new JSONArray(response);
            JSONObject obj = jsonarray.getJSONObject(0);

            if (obj.getString("status").equals("1")) {

                sessionManager.CreateLoginSession(obj.getString("id"), obj.getString("name"), obj.getString("mobile"), obj.getString("email"), obj.getString("address"), obj.getString("role"));

                Intent intent = new Intent(this, DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            else{
                UtilsMethod.INSTANCE.errorToast(this,"Your account has been deactivated");
            }


        }
        catch (JSONException e) { e.printStackTrace(); }

    }



}