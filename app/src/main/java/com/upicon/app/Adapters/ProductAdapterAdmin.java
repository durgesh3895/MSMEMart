package com.upicon.app.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.AppController.SessionManager;
import com.upicon.app.Model.Product;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapterAdmin extends RecyclerView.Adapter<ProductAdapterAdmin.MyViewHolder> implements Filterable {

    private List<Product> OriginalList;
    private List<Product> FilteredList;
    private Context context;

    SessionManager sessionManager;
    HashMap<String, String> user;


    public ProductAdapterAdmin(Context context, List<Product> OriginalList) {
        this.context = context;
        this.OriginalList = OriginalList;
        this.FilteredList = OriginalList;
        notifyItemChanged(0, FilteredList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_product_list_admin, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Product product = FilteredList.get(position);


        holder.product_name.setText(product.getProduct_name());
        holder.product_price.setText("Rs: \u20B9 "+product.getProduct_price());
        holder.product_quantity.setText("Quantity: " +product.getProduct_quantity());
        Picasso.get().load(BaseURL.IMAGE_PATH+ product.getProduct_image()).placeholder(R.drawable.up_msme_logo_gray_bg).into(holder.product_image);



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

                TextView txt_desc=(TextView) dialog.findViewById(R.id.txt_desc);
                txt_desc.setText(product.getProduct_desc());


                ImageView imageView=(ImageView) dialog.findViewById(R.id.unit_image);
                Picasso.get().load(BaseURL.IMAGE_PATH+ product.getProduct_image()).placeholder(R.drawable.ic_default_unit).into(imageView);


            }
        });

        holder.product_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //UtilsMethod.INSTANCE.successToast(context,"Item added into cart");
                addToCart(product.getId());
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    FilteredList = OriginalList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product list : OriginalList) {
                        if (list.getProduct_price().toLowerCase().contains(charString.toLowerCase())||
                                list.getProduct_name().toLowerCase().contains(charString.toLowerCase())||
                                list.getProduct_desc().toLowerCase().contains(charString.toLowerCase()))
                        {
                            filteredList.add(list);
                        }
                    }
                    FilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = FilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                FilteredList = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView product_name,product_price,product_quantity;
        ImageView product_image;
        LinearLayout card_layout;


        MyViewHolder(View view) {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            product_price = (TextView) view.findViewById(R.id.product_price);
            product_quantity = (TextView) view.findViewById(R.id.product_quantity);
            product_image = (ImageView) view.findViewById(R.id.product_image);
            card_layout = (LinearLayout) view.findViewById(R.id.card_layout);
        }
    }


    private void addToCart(String id) {
        sessionManager = new SessionManager(context);
        user = sessionManager.getUserDetails();

        ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Adding please wait.....");
        pd.setCancelable(false);
        pd.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, BaseURL.ADD_TO_CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("dc_response",response);
                        pd.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);

                            if(jsonObject.get("Response").equals(true)){

                                UtilsMethod.INSTANCE.successToast(context,jsonObject.getString("Message"));
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
