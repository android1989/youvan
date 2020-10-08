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
import com.xwaydesigns.youvan.Model.Donation;
import com.xwaydesigns.youvan.Model.Reward;
import com.xwaydesigns.youvan.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyRewardAdapter extends RecyclerView.Adapter<MyRewardAdapter.MyRewardViewHolder>
{

    Context ctx;
    List<Reward> data;
    String student_id;
    private String image_url;

    public MyRewardAdapter(Context ctx,List<Reward> data , String student_id)
    {
        this.ctx =ctx;
        this.data = data;
        this.student_id = student_id;
    }

    @NonNull
    @Override
    public MyRewardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_my_reward, parent, false);
        return new MyRewardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRewardViewHolder holder, int position)
    {
        Reward obj = data.get(position);
        holder.name.setText(obj.getItem_name());
        holder.quantity.setText(obj.getItem_quantity());
        holder.reward_points.setText(obj.getItem_rewards());
        holder.date.setText(obj.getDonation_date());
        image_url = Constants.BASE_URL+"youvan/images/"+student_id+"/Items Image/"+obj.getDonation_id()+"/"+obj.getItem_image();
        Picasso.get().load(image_url).resize(70,70).centerCrop().placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyRewardViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        CircleImageView image;
        TextView name;
        TextView quantity;
        TextView reward_points;
        TextView date;
        public MyRewardViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mview = itemView;
            image = mview.findViewById(R.id.single_row_reward_image);
            name = mview.findViewById(R.id.single_row_reward_name);
            quantity = mview.findViewById(R.id.single_row_reward_item_quantity);
            reward_points = mview.findViewById(R.id.single_row_my_reward_points);
            date = mview.findViewById(R.id.single_row_reward_date);
        }

    }
}
