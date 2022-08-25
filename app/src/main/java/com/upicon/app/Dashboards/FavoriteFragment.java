package com.upicon.app.Dashboards;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.upicon.app.Adapters.ProductAdapter;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Cart.Cart;
import com.upicon.app.Model.Product;
import com.upicon.app.Product.ProductDetails;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoriteFragment extends androidx.fragment.app.Fragment {

    View view;
    SessionManager sessionManager;
    HashMap<String, String> user;
    private List<Product> wishList;
    private WishListAdapter wishListAdapter;

    ShimmerFrameLayout shimmerFrameLayout;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        initialization();
        refresh();
        recyclerView();
        getWishList();

        return view;
    }

    private void initialization() {
        sessionManager = new SessionManager(getActivity());
        user = sessionManager.getUserDetails();

        shimmerFrameLayout = (ShimmerFrameLayout)view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();


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

    private void refresh() {
        SwipeRefreshLayout swipeLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout) ;
        swipeLayout.setColorSchemeColors(Color.RED, Color.MAGENTA, Color.GREEN);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        getWishList();
                        swipeLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    private void recyclerView() {
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        wishList = new ArrayList<>();
        wishListAdapter = new WishListAdapter(getActivity(), wishList);
        recyclerView.setAdapter(wishListAdapter);
    }

    private void getWishList() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.GET_WISHLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        setResponse(UtilsMethod.INSTANCE.setApiResponse(getActivity(),response));
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
            wishList.clear();
            wishList.addAll(items);
            wishListAdapter.notifyDataSetChanged();

        }

    }

    public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.MyViewHolder> {

        private List<Product> OriginalList;
        private List<Product> FilteredList;
        private Context context;


        public WishListAdapter(Context context, List<Product> OriginalList) {
            this.context = context;
            this.OriginalList = OriginalList;
            this.FilteredList = OriginalList;
            notifyItemChanged(0, FilteredList.size());
        }

        @Override
        public WishListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_wishlist_list, parent, false);
            return new WishListAdapter.MyViewHolder(itemView);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onBindViewHolder(WishListAdapter.MyViewHolder holder, final int position) {
            final Product product = FilteredList.get(position);


            holder.product_name.setText(product.getProduct_name());
            holder.product_price.setText("Rs: \u20B9  "+product.getProduct_price());
            Picasso.get().load(BaseURL.IMAGE_PATH+ product.getProduct_image()).placeholder(R.drawable.msme_white_bg).into(holder.product_image);


            holder.card_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, ProductDetails.class);
                    intent.putExtra("product_id",product.getId());
                    intent.putExtra("product_name",product.getProduct_name());
                    intent.putExtra("product_price",product.getProduct_price());
                    intent.putExtra("product_desc",product.getProduct_desc());
                    intent.putExtra("product_image",product.getProduct_image());
                    context.startActivity(intent);
                }
            });

            holder.iv_wishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeFromWishlist(product.getProduct_id());
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
            TextView product_name,product_price;
            ImageView product_image,iv_wishlist;
            LinearLayout card_layout;


            MyViewHolder(View view) {
                super(view);
                product_name = (TextView) view.findViewById(R.id.product_name);
                product_price = (TextView) view.findViewById(R.id.product_price);
                product_image = (ImageView) view.findViewById(R.id.product_image);
                iv_wishlist = (ImageView) view.findViewById(R.id.iv_wishlist);
                card_layout = (LinearLayout) view.findViewById(R.id.card_layout);
            }
        }


        private void removeFromWishlist(String id) {
            ProgressDialog pd=new ProgressDialog(context);
            pd.setMessage("Removing please wait.....");
            pd.setCancelable(false);
            pd.show();

            RequestQueue queue = Volley.newRequestQueue(context);
            StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.REMOVE_WISHLIST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("dc_response",response);
                            pd.dismiss();
                            try {
                                JSONObject jsonObject=new JSONObject(response);

                                if(jsonObject.get("Response").equals(true)){

                                    UtilsMethod.INSTANCE.successToast(context,jsonObject.getString("Message"));
                                    recyclerView();
                                    getWishList();
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
