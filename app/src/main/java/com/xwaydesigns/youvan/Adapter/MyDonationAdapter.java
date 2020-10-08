package com.xwaydesigns.youvan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.xwaydesigns.youvan.ExtraClasses.Constants;
import com.xwaydesigns.youvan.Model.DonateNow;
import com.xwaydesigns.youvan.Model.Donation;
import com.xwaydesigns.youvan.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyDonationAdapter extends RecyclerView.Adapter<MyDonationAdapter.MyDonationViewHolder>
{
    Context ctx;
    List<Donation> data;
    String student_id;
    private String image_url;

    public MyDonationAdapter(Context ctx, List<Donation> data , String student_id)
    {
        this.ctx =ctx;
        this.data = data;
        this.student_id = student_id;
    }

    @NonNull
    @Override
    public MyDonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_donation, parent, false);
        return new MyDonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDonationViewHolder holder, int position)
    {

        Donation obj = data.get(position);
        holder.name.setText(obj.getItem_name());
        holder.quantity.setText(obj.getItem_quantity());
        holder.status.setText(obj.getItem_status());
        holder.date.setText(obj.getDonation_date());
        image_url = Constants.BASE_URL+"youvan/images/"+student_id+"/Items Image/"+obj.getDonation_id()+"/"+obj.getItem_image();
        Picasso.get().load(image_url).resize(70,70).centerCrop().placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyDonationViewHolder extends RecyclerView.ViewHolder
    {
           View mview;
           TextView name;
           TextView quantity;
           CircleImageView image;
           TextView status;
           TextView date;

        public MyDonationViewHolder(@NonNull View itemView)
        {
            super(itemView);
             mview = itemView;
            name = mview.findViewById(R.id.single_row_name);
            quantity = mview.findViewById(R.id.single_row_item_quantity);
            image = mview.findViewById(R.id.single_row_image);
            status = mview.findViewById(R.id.single_row_status);
            date = mview.findViewById(R.id.single_row_date);
        }

    }
}
