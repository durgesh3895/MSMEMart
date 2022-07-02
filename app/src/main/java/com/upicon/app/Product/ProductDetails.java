package com.upicon.app.Product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Cart.Cart;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductDetails extends AppCompatActivity {

    SessionManager sessionManager;
    HashMap<String, String> user;

    ImageView iv_product,iv_wishlist;
    TextView tv_product_id,tv_product_name,tv_product_price,tv_product_desc;
    LinearLayout ll_cart;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);

        initialization();
        toolBar();
        clickListener();
    }



    private void initialization() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        intent=getIntent();

        iv_product=(ImageView) findViewById(R.id.iv_product_image);
        iv_wishlist=(ImageView) findViewById(R.id.iv_wishlist);

        tv_product_id=(TextView) findViewById(R.id.tv_product_id);
        tv_product_name=(TextView) findViewById(R.id.tv_product_name);
        tv_product_price=(TextView) findViewById(R.id.tv_product_price);
        tv_product_desc=(TextView) findViewById(R.id.tv_product_desc);

        ll_cart=(LinearLayout) findViewById(R.id.ll_cart);


        tv_product_id.setText(intent.getStringExtra("product_id"));
        tv_product_name.setText(intent.getStringExtra("product_name"));
        tv_product_price.setText("\u20B9 "+intent.getStringExtra("product_price"));
        tv_product_desc.setText(intent.getStringExtra("product_desc"));

        Picasso.get().load(BaseURL.IMAGE_PATH+ intent.getStringExtra("product_image")).placeholder(R.drawable.ic_default_small_product).into(iv_product);





    }

    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Product Details");
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

    private void clickListener() {

        ll_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });
        iv_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToWishlist();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void addToCart() {
        ProgressDialog pd=new ProgressDialog(ProductDetails.this);
        pd.setMessage("Adding please wait.....");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ProductDetails.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dc_response",response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            if(jsonObject.get("Response").equals(true)){
                                UtilsMethod.INSTANCE.successToast(ProductDetails.this,jsonObject.getString("Message"));
                                startActivity(new Intent(ProductDetails.this, Cart.class));
                                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            }
                            else if(jsonObject.get("Response").equals(false)){

                                UtilsMethod.INSTANCE.infoToast(ProductDetails.this,jsonObject.getString("Message"));
                            }
                            else {
                                UtilsMethod.INSTANCE.successToast(ProductDetails.this,"Something went wrong");

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
                headers.put("Authorization", user.get(SessionManager.KEY_MOBILE));
                return headers;
            }
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",user.get(SessionManager.KEY_ID));
                params.put("product_id", tv_product_id.getText().toString());
                params.put("token", BaseURL.TOKEN);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

    private void addToWishlist() {
        ProgressDialog pd=new ProgressDialog(ProductDetails.this);
        pd.setMessage("Adding please wait.....");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(ProductDetails.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.ADD_TO_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dc_response",response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            if(jsonObject.get("Response").equals(true)){
                                UtilsMethod.INSTANCE.successToast(ProductDetails.this,jsonObject.getString("Message"));
                                iv_wishlist.setBackgroundResource(R.drawable.ic_fav_active);
                            }
                            else if(jsonObject.get("Response").equals(false)){

                                UtilsMethod.INSTANCE.infoToast(ProductDetails.this,jsonObject.getString("Message"));
                            }
                            else {
                                UtilsMethod.INSTANCE.successToast(ProductDetails.this,"Something went wrong");

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
                headers.put("Authorization", user.get(SessionManager.KEY_MOBILE));
                return headers;
            }
            @SuppressLint("HardwareIds")
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",user.get(SessionManager.KEY_ID));
                params.put("product_id", tv_product_id.getText().toString());
                params.put("token", BaseURL.TOKEN);
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


}