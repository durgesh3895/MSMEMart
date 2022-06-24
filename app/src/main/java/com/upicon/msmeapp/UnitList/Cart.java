package com.upicon.msmeapp.UnitList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.upicon.msmeapp.AppController.BaseURL;
import com.upicon.msmeapp.AppController.SessionManager;
import com.upicon.msmeapp.Model.Product;
import com.upicon.msmeapp.R;
import com.upicon.msmeapp.UtilsMethod.UtilsMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart extends AppCompatActivity {

    SessionManager sessionManager;
    HashMap<String, String> user;
    private List<Product> cartList;
    private CartAdapter cartAdapter;

    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout ll_cart_layout;
    TextView tv_count,tv_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);

        initialization();
        toolBar();
        refresh();
        clickListener();
        recyclerView();
        getUserList();
    }

    private void initialization() {
        sessionManager = new SessionManager(getApplicationContext());
        user = sessionManager.getUserDetails();

        shimmerFrameLayout = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        ll_cart_layout = (LinearLayout) findViewById(R.id.ll_cart);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_price = (TextView) findViewById(R.id.tv_price);


    }

    private void toolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Cart");
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
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void clickListener() {



    }

    private void refresh() {
        SwipeRefreshLayout swipeLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout) ;
        swipeLayout.setColorSchemeColors(Color.RED, Color.MAGENTA, Color.GREEN);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        getUserList();
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void recyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        //recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(Cart.this, cartList);
        recyclerView.setAdapter(cartAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && ll_cart_layout.getVisibility() == View.VISIBLE) {
                    ll_cart_layout.setVisibility(View.GONE);
                } else if (dy < 0 && ll_cart_layout.getVisibility() != View.VISIBLE) {
                    ll_cart_layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getUserList() {

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.GET_CART,
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
            cartList.clear();
            cartList.addAll(items);
            cartAdapter.notifyDataSetChanged();


            tv_count.setText("Total items: "+cartList.size());

            int price=0;
            for (int i=0;i<cartList.size();i++){
               price= price+Integer.parseInt(cartList.get(i).getProduct_price());
            }
            tv_price.setText("Total amount: "+price);

            if (cartList.size()==0){
                ll_cart_layout.setVisibility(View.GONE);
            }
            else{
                ll_cart_layout.setVisibility(View.VISIBLE);
            }

        }

    }

    public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

        private List<Product> OriginalList;
        private List<Product> FilteredList;
        private Context context;

        SessionManager sessionManager;
        HashMap<String, String> user;
        int total_price=0;


        public CartAdapter(Context context, List<Product> OriginalList) {
            this.context = context;
            this.OriginalList = OriginalList;
            this.FilteredList = OriginalList;
            notifyItemChanged(0, FilteredList.size());
        }

        @Override
        public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_cart_list, parent, false);
            return new CartAdapter.MyViewHolder(itemView);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onBindViewHolder(CartAdapter.MyViewHolder holder, final int position) {
            final Product product = FilteredList.get(position);


            holder.product_name.setText(product.getProduct_name());
            holder.product_price.setText("Rs: \u20B9  "+product.getProduct_price());
            Picasso.get().load(BaseURL.IMAGE_PATH+ product.getProduct_image()).placeholder(R.drawable.msme_white_bg).into(holder.product_image);

            holder.product_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog=new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.image_view_dailog);
                    dialog.show();
                    Window window=dialog.getWindow();
                    window.setBackgroundDrawableResource(R.drawable.dialog_bg);
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    TextView txt_title=(TextView) dialog.findViewById(R.id.txt_title);
                    //txt_title.setText(product.getUnit_name());
                    txt_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    ImageView imageView=(ImageView) dialog.findViewById(R.id.unit_image);
                    Picasso.get().load(BaseURL.IMAGE_PATH+ product.getProduct_image()).placeholder(R.drawable.ic_default_unit).into(imageView);


                }
            });

            holder.product_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFromCart(product.getProduct_id());
                }
            });


        }



        @Override
        public int getItemCount() {
            return FilteredList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView product_name,product_price,product_remove;
            ImageView product_image;
            LinearLayout card_layout;


            MyViewHolder(View view) {
                super(view);
                product_name = (TextView) view.findViewById(R.id.product_name);
                product_price = (TextView) view.findViewById(R.id.product_price);
                product_remove = (TextView) view.findViewById(R.id.product_remove);
                product_image = (ImageView) view.findViewById(R.id.product_image);
                card_layout = (LinearLayout) view.findViewById(R.id.card_layout);
            }
        }


        private void removeFromCart(String id) {
            sessionManager = new SessionManager(context);
            user = sessionManager.getUserDetails();

            ProgressDialog pd=new ProgressDialog(context);
            pd.setMessage("Adding please wait.....");
            pd.setCancelable(false);
            pd.show();

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.REMOVE_CART,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("dc_response",response);
                            pd.dismiss();
                            try {
                                JSONObject jsonObject=new JSONObject(response);

                                if(jsonObject.get("Response").equals(true)){

                                    UtilsMethod.INSTANCE.successToast(context,jsonObject.getString("Message"));
                                    getUserList();
                                    cartAdapter.notifyDataSetChanged();
                                }
                                else if(jsonObject.get("Response").equals(false)){

                                    UtilsMethod.INSTANCE.infoToast(context,jsonObject.getString("Message"));
                                }
                                else {
                                    UtilsMethod.INSTANCE.successToast(context,"Something went wrong");

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
                    params.put("product_id", id);
                    params.put("token", BaseURL.TOKEN);
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(postRequest);
        }


    }
}