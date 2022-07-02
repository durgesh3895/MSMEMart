package com.upicon.app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.upicon.app.Model.Users;
import com.upicon.app.R;
import com.upicon.app.UtilsMethod.UtilsMethod;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> implements Filterable {

    private List<Users> OriginalList;
    private List<Users> FilteredList;
    private Context context;

    public UsersAdapter(Context context, List<Users> OriginalList) {
        this.context = context;
        this.OriginalList = OriginalList;
        this.FilteredList = OriginalList;
        notifyItemChanged(0, FilteredList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_list, parent, false);
        return new MyViewHolder(itemView);
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Users users = FilteredList.get(position);


        holder.user_name.setText(users.getFirst_name()+" "+ users.getLast_name());
        holder.user_contact.setText(users.getMobile());
        holder.user_address.setText(users.getAddress());


        holder.user_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilsMethod.INSTANCE.callToNumber(context, users.getMobile());
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
                    List<Users> filteredList = new ArrayList<>();
                    for (Users list : OriginalList) {
                        if (list.getFirst_name().toLowerCase().contains(charString.toLowerCase())||
                                list.getLast_name().toLowerCase().contains(charString.toLowerCase())||
                                list.getAddress().toLowerCase().contains(charString.toLowerCase())||
                                list.getDistrict().toLowerCase().contains(charString.toLowerCase())||
                                list.getMobile().toLowerCase().contains(charString.toLowerCase()))
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
                FilteredList = (ArrayList<Users>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_name,user_contact,user_address;
        LinearLayout border_layout,card_layout;

        MyViewHolder(View view) {
            super(view);
            user_name = (TextView) view.findViewById(R.id.user_name);
            user_contact = (TextView) view.findViewById(R.id.user_contact);
            user_address = (TextView) view.findViewById(R.id.user_address);
            border_layout = (LinearLayout) view.findViewById(R.id.border_layout);
            card_layout = (LinearLayout) view.findViewById(R.id.card_layout);
        }
    }


}
