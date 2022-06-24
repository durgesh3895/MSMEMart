package com.upicon.msmeapp.Dashboards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.upicon.msmeapp.Adapters.ProductAdapter;
import com.upicon.msmeapp.AppController.BaseURL;
import com.upicon.msmeapp.AppController.SessionManager;
import com.upicon.msmeapp.BasicActivities.UserProfile;
import com.upicon.msmeapp.Model.Product;
import com.upicon.msmeapp.R;
import com.upicon.msmeapp.UnitList.Cart;
import com.upicon.msmeapp.UnitList.Products;
import com.upicon.msmeapp.UtilsMethod.UtilsMethod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoard extends AppCompatActivity implements View.OnClickListener{

    SessionManager sessionManager;
    HashMap<String, String> user;
    ImageView iv_profile,iv_cart;
    TextView view_all;

    private List<Product> productList;
    private ProductAdapter productAdapter;
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dash_board);

        initialization();
        recyclerView();
        getUserList();

    }

    private void initialization() {

        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        iv_profile=findViewById(R.id.iv_profile);
        iv_cart=findViewById(R.id.iv_cart);

        view_all=findViewById(R.id.view_all);



        iv_profile.setOnClickListener(this);
        iv_cart.setOnClickListener(this);
        view_all.setOnClickListener(this);


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
            UtilsMethod.INSTANCE.infoToast(this,"Hit back again to close app");
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch(id) {
            case R.id.iv_profile:
                startActivity(new Intent(this, UserProfile.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                break;
            case R.id.view_all:
                startActivity(new Intent(this, Products.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                break;
            case R.id.iv_cart:
                startActivity(new Intent(this, Cart.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                break;
            default:
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    private void recyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(DashBoard.this, productList);
        recyclerView.setAdapter(productAdapter);

    }

    private void getUserList() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        setResponse(UtilsMethod.INSTANCE.setApiResponse(getApplicationContext(),response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error",error.toString());
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
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
                params.put("user_role",user.get(SessionManager.KEY_ROLE));
                params.put("limit","4");
                params.put("token",BaseURL.TOKEN);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void setResponse(String response) {

        if (!response.isEmpty()){
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Product>>(){}.getType();
            List<Product> items = gson.fromJson(response, listType);
            productList.clear();
            productList.addAll(items);
            productAdapter.notifyDataSetChanged();
        }

    }


}