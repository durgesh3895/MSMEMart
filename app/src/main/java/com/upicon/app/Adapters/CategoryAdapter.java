package com.upicon.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.upicon.app.AppController.BaseURL;
import com.upicon.app.Model.Category;
import com.upicon.app.Product.Products;
import com.upicon.app.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements Filterable {

    private List<Category> OriginalList;
    private List<Category> FilteredList;
    private Context context;

    public CategoryAdapter(Context context, List<Category> OriginalList) {
        this.context = context;
        this.OriginalList = OriginalList;
        this.FilteredList = OriginalList;
        notifyItemChanged(0, FilteredList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_list, parent, false);
        return new MyViewHolder(itemView);
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Category category = FilteredList.get(position);


        holder.tv_category.setText(category.getC_name());
        Picasso.get().load(BaseURL.IMAGE_PATH_CATEGORY+ category.getC_image()).placeholder(R.drawable.up_msme_logo_gray_bg).into(holder.iv_category);

        holder.iv_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, Products.class);
                intent.putExtra("key","All");
                context.startActivity(intent);
                //context.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
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
                    List<Category> filteredList = new ArrayList<>();
                    for (Category list : OriginalList) {
                        if (
                                list.getC_name().toLowerCase().contains(charString.toLowerCase()))
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
                FilteredList = (ArrayList<Category>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_category;
        ImageView iv_category;
        LinearLayout border_layout,card_layout;

        MyViewHolder(View view) {
            super(view);
            tv_category = (TextView) view.findViewById(R.id.tv_category);
            iv_category = (ImageView) view.findViewById(R.id.iv_category);
            border_layout = (LinearLayout) view.findViewById(R.id.border_layout);
            card_layout = (LinearLayout) view.findViewById(R.id.card_layout);
        }
    }


}
