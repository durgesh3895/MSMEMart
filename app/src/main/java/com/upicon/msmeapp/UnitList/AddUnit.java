package com.upicon.msmeapp.UnitList;

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
import com.upicon.msmeapp.AppController.BaseURL;
import com.upicon.msmeapp.AppController.SessionManager;
import com.upicon.msmeapp.Dashboards.DashBoard;
import com.upicon.msmeapp.R;
import com.upicon.msmeapp.UtilsMethod.ImageUtils;
import com.upicon.msmeapp.UtilsMethod.UtilsMethod;
import com.upicon.msmeapp.UtilsMethod.UtilsPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddUnit extends AppCompatActivity implements android.view.View.OnFocusChangeListener{

    SessionManager sessionManager;
    HashMap<String, String> user;
    AutoCompleteTextView auto_district;
    ArrayAdapter<String> districtAdapter = null;
    EditText et_unit_name,et_owner_name,et_mobile_number,et_latitude,et_longitude,et_address;
    TextView tv_latLng;
    ImageView unit_image;
    Button add_surveyor;
    private UtilsPermissions utilsPermissions;

    String encoded;
    Intent intent;
    String imagePath,imagePathMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_survey);

        utilsPermissions = new UtilsPermissions(this);

        if (utilsPermissions.hasAllowedAllPermissions()) initializeFragmentContainer();
        else utilsPermissions.requestPermissions();

        initialization();
        toolBar();
        clickListener();
        closeKeyBoard();



    }

    private void initialization() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        intent=getIntent();
        imagePath=intent.getStringExtra("imagePath");
        imagePathMap=intent.getStringExtra("imagePathMap");


        et_unit_name=findViewById(R.id.et_unit_name);
        et_owner_name=findViewById(R.id.et_owner_name);
        et_mobile_number=findViewById(R.id.et_mobile_number);
        et_address=findViewById(R.id.et_address);
        et_latitude=findViewById(R.id.et_latitude);
        et_longitude=findViewById(R.id.et_longitude);
        tv_latLng=findViewById(R.id.tv_lat_lng);
        auto_district=findViewById(R.id.auto_district);
        unit_image=findViewById(R.id.unit_image);

        et_latitude.setEnabled(false);
        et_longitude.setEnabled(false);


        add_surveyor=findViewById(R.id.add_surveyor);

        et_mobile_number.setOnFocusChangeListener(this);

        auto_district.setKeyListener(null);
        districtAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.hint_completion_layout, R.id.tvHintCompletion, getResources().getStringArray(R.array.up_district));
        auto_district.setAdapter(districtAdapter);
        auto_district.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                auto_district.showDropDown();
                return false;
            }
        });


        unit_image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddUnit.this,Camera.class);
                startActivity(intent);
            }
        });


        if (!imagePath.equals("")){

            Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(imagePath);
            Bitmap bitmap2 = ImageUtils.getInstant().getCompressedBitmap(imagePathMap);
            //unit_image.setImageBitmap(bitmap);
            //encoded=imageToString(bitmap);


            SharedPreferences prefs = getSharedPreferences("MySP", MODE_PRIVATE);
            String address = prefs.getString("address", "");
            String latitude = prefs.getString("latitude", "");
            String longitude = prefs.getString("longitude", "");

            et_address.setText(address);
            et_latitude.setText(latitude);
            et_longitude.setText(longitude);

            tv_latLng.setText("LatLng : "+latitude+" , "+longitude);


            Bitmap mergedImages = createSingleImageFromMultipleImages(bitmap, bitmap2);

            unit_image.setImageBitmap(mergedImages);
            encoded=imageToString(mergedImages);


        }



    }


    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Survey");
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

        add_surveyor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm();
            }
        });
    }

    private void validateForm() {
        if (imagePath.isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Select image please");
        }
        else if (et_unit_name.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter product name");
        }
        else if (et_unit_name.getText().toString().length()<5){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Unit name is too short");
        }
        else if (et_owner_name.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter owner name");
        }
        else if (et_mobile_number.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter contact number");
        }
        else if (et_mobile_number.getText().toString().length()<10){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter 10 digit contact number");
        }
        else if (auto_district.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Select district");
        }
        else if (et_address.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter address");
        }
        else if (et_latitude.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter latitude");
        }
        else if (et_longitude.getText().toString().isEmpty()){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter longitude");
        }
        else if (et_address.getText().toString().length()<10){
            UtilsMethod.INSTANCE.errorToast(getApplicationContext(),"Enter full address");
        }
        else{
            addUnit();

        }
    }

    private void addUnit() {

        ProgressDialog pd=new ProgressDialog(AddUnit.this);
        pd.setMessage("Adding please wait.....");
        pd.setCancelable(false);
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.ADD_UNIT,
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
                params.put("unit_name",et_unit_name.getText().toString());
                params.put("unit_owner_name",et_owner_name.getText().toString());
                params.put("unit_contact",et_mobile_number.getText().toString());
                params.put("unit_district",auto_district.getText().toString());
                params.put("unit_latitude",et_latitude.getText().toString());
                params.put("unit_longitude",et_longitude.getText().toString());
                params.put("unit_address",et_address.getText().toString());
                params.put("unit_image",encoded);
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
                UtilsMethod.INSTANCE.successToast(AddUnit.this,obj.getString("Message"));
                Intent intent=new Intent(getApplicationContext(), DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
            else if(obj.get("Response").equals(false)){
                UtilsMethod.INSTANCE.successToast(AddUnit.this,obj.getString("Message"));
            }
            else {
                UtilsMethod.INSTANCE.errorToast(AddUnit.this,"Something went wrong");
            }

        }

        catch (JSONException e) { e.printStackTrace(); }
    }

    public String imageToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
        byte[] imageBytes=outputStream.toByteArray();
        return encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    public void onFocusChange(android.view.View view, boolean b) {
        if (!b){
            UtilsMethod.INSTANCE.hideKeyboard(AddUnit.this,view);
        }
    }

    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {

        Bitmap b = Bitmap.createScaledBitmap(secondImage, firstImage.getWidth(), secondImage.getHeight(), false);
        //Bitmap result = Bitmap.createBitmap(firstImage.getWidth() + secondImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight()+secondImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(b, 0f, firstImage.getHeight(), null);

        return result;
    }



}