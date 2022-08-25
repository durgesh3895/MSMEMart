package com.upicon.app.Product;

import static android.util.Base64.encodeToString;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.upicon.app.R;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Dashboards.DashBoard;
import com.upicon.app.UtilsMethod.ImageUtils;
import com.upicon.app.UtilsMethod.UtilsMethod;
import com.upicon.app.UtilsMethod.UtilsPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity implements android.view.View.OnFocusChangeListener{

    SessionManager sessionManager;
    HashMap<String, String> user;

    AutoCompleteTextView auto_category;
    ArrayAdapter<String> categoryAdapter = null;
    ArrayList<String> category_name;
    EditText et_product_name,et_product_price,et_product_quantity,et_product_desc;
    ImageView product_image;
    Button btn_add_product;
    UtilsPermissions utilsPermissions;
    String imagePath="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        utilsPermissions = new UtilsPermissions(this);

        if (utilsPermissions.hasAllowedAllPermissions()) initializeFragmentContainer();
        else utilsPermissions.requestPermissions();

        initialization();
        toolBar();
        getCategoryList();
        clickListener();
        closeKeyBoard();



    }

    private void initialization() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        auto_category=findViewById(R.id.auto_category);
        et_product_name=findViewById(R.id.et_product_name);
        et_product_price=findViewById(R.id.et_product_price);
        et_product_quantity=findViewById(R.id.et_product_quantity);
        et_product_desc=findViewById(R.id.et_product_desc);
        product_image=findViewById(R.id.product_image);
        btn_add_product=findViewById(R.id.btn_add_product);

        category_name = new ArrayList<>();
        auto_category.setKeyListener(null);
        categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.hint_completion_layout, R.id.tvHintCompletion, category_name);
        auto_category.setAdapter(categoryAdapter);
        auto_category.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                auto_category.showDropDown();
                return false;
            }
        });


        product_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(AddProduct.this, Camera.class);
                //startActivity(intent);
            }
        });


    }


    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add new product");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initializeFragmentContainer();
    }

    private void initializeFragmentContainer() {
       // UtilsMethod.INSTANCE.successToast(this,"All permission allowed");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void clickListener() {

        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });
    }

    private void validateForm() {
         if (auto_category.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Select category");
        }
        else if (et_product_name.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter product name");
        }
        else if (et_product_price.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter product price");
        }
        else if (et_product_price.getText().toString().equals("0")){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter valid product price");
        }
        else if (et_product_quantity.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter product quantity");
        }
        else if (et_product_quantity.getText().toString().equals("0")){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter valid product quantity");
        }
        else if (et_product_desc.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter product description");
        }
        else{
            addProduct();

        }
    }

    private void addProduct() {

        ProgressDialog pd=new ProgressDialog(AddProduct.this);
        pd.setMessage("Adding please wait.....");
        pd.setCancelable(false);
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.ADD_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SetUserResponse(response);
                        Log.e("response",response);
                        pd.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error",error.toString());
                        pd.dismiss();

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                //headers.put("Authorization", "Token "+user.get(SessionManager.KEY_MOBILE));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",user.get(SessionManager.KEY_ID));
                params.put("product_category",auto_category.getText().toString());
                params.put("product_name",et_product_name.getText().toString());
                params.put("product_price",et_product_price.getText().toString());
                params.put("product_quantity",et_product_quantity.getText().toString());
                params.put("product_desc",et_product_desc.getText().toString());
                params.put("product_image",imagePath);
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
//                UtilsMethod.INSTANCE.successToast(AddProduct.this,obj.getString("Message"));
//                Intent intent=new Intent(getApplicationContext(), DashBoard.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
                UtilsMethod.INSTANCE.successToast(AddProduct.this,obj.getString("Message"));
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
            else if(obj.get("Response").equals(false)){
                UtilsMethod.INSTANCE.successToast(AddProduct.this,obj.getString("Message"));
            }
            else {
                UtilsMethod.INSTANCE.errorToast(AddProduct.this,"Something went wrong");
            }

        }

        catch (JSONException e) { e.printStackTrace(); }
    }

    @Override
    public void onFocusChange(android.view.View view, boolean b) {
        if (!b){
            UtilsMethod.INSTANCE.hideKeyboard(AddProduct.this,view);
        }
    }

    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getCategoryList() {

        RequestQueue queue = Volley.newRequestQueue(AddProduct.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.CATEGORY_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.get("Response").equals(true)) {

                                JSONArray jsonArray = new JSONArray(obj.getString("Data"));

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    final JSONObject e = jsonArray.getJSONObject(i);
                                    category_name.add(e.getString("c_name"));
                                }
                            }

                        }

                        catch (JSONException e) { e.printStackTrace(); }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error",error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", user.get(SessionManager.KEY_MOBILE));
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user.get(SessionManager.KEY_ID));
                params.put("limit","100");
                params.put("token",BaseURL.TOKEN);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}