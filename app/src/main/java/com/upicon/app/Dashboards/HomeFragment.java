package com.upicon.app.Dashboards;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.upicon.app.Adapters.ProductAdapter;
import com.upicon.app.Adapters.SlidingImageAdapter;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Model.Product;
import com.upicon.app.Product.Products;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends androidx.fragment.app.Fragment {

    View view;
    ArrayList<String> dcurls;
    private static int mcurrentPage = 0;

    SessionManager sessionManager;
    HashMap<String, String> user;
    private List<Product> productList;
    private ProductAdapter productAdapter;

    ShimmerFrameLayout shimmerFrameLayout;
    TextView tv_view_all_product;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initialization();
        mainSliderImages();
        clickListeners();
        recyclerView();
        getProductList();


        return view;
    }



    private void initialization() {
        sessionManager = new SessionManager(getContext());
        user = sessionManager.getUserDetails();

        shimmerFrameLayout = (ShimmerFrameLayout)view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        tv_view_all_product=(TextView)view.findViewById(R.id.tv_view_all);

    }

    private void mainSliderImages() {

        dcurls = new ArrayList<>();


        dcurls.add(BaseURL.IMAGE_PATH_SLIDER+"b1.jpg");
        dcurls.add(BaseURL.IMAGE_PATH_SLIDER+"b2.jpg");
        dcurls.add(BaseURL.IMAGE_PATH_SLIDER+"b3.jpg");

        Log.e("dccc",BaseURL.IMAGE_PATH_SLIDER+"b1.jpg");

        ViewPager mainViewpager = (ViewPager)view.findViewById(R.id.main_slider_viewpager);
        mainViewpager.setAdapter(new SlidingImageAdapter(getActivity(),dcurls));

        TextView tv_dot=(TextView)view.findViewById(R.id.tv_dot);
        //DotsIndicator pageIndicatorView =findViewById(R.id.dots_indicator);
        //pageIndicatorView.addDot(1);
        //pageIndicatorView.setCount(dcurls.size());
        //pageIndicatorView.setSelection(0);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (mcurrentPage ==  dcurls.size()) { mcurrentPage = 0;}
                mainViewpager.setCurrentItem(mcurrentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 3000);

        mainViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {/*empty*/}

            @Override
            public void onPageSelected(int position) {

                //pageIndicatorView.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {/*empty*/}
        });
    }

    private void clickListeners() {
        tv_view_all_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),Products.class);
                intent.putExtra("key","All");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
    }


    private void recyclerView() {
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getActivity(), productList);
        recyclerView.setAdapter(productAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void getProductList() {

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.PRODUCT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        setResponse(UtilsMethod.INSTANCE.setApiResponse(getContext(),response));
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
                params.put("limit","6");
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
