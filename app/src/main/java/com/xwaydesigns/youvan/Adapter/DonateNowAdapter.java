package com.xwaydesigns.youvan.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xwaydesigns.youvan.Model.DonateNow;
import com.xwaydesigns.youvan.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonateNowAdapter extends RecyclerView.Adapter<DonateNowAdapter.DonateNowViewHolder>
{
    Context ctx;
    ArrayList<DonateNow> data;

    public DonateNowAdapter(Context ctx, ArrayList<DonateNow> data)
    {
        this.ctx =ctx;
        this.data = data ;
    }

    @NonNull
    @Override
    public DonateNowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_donate_now, parent, false);
        return new DonateNowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonateNowViewHolder holder, int position)
    {
        DonateNow obj = data.get(position);
        holder.image.setImageBitmap(obj.getBitmap());
        holder.name.setText(obj.getName());
        holder.quantity.setText(obj.getQuantity());
        holder.date.setText(obj.getDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class DonateNowViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        CircleImageView image;
        TextView name;
        TextView quantity;
        TextView date;
        public DonateNowViewHolder(@NonNull View itemView)
        {
            super(itemView);
            mview = itemView;
            image = mview.findViewById(R.id.single_row_donate_now_image);
            name = mview.findViewById(R.id.single_row_donate_now_name);
            quantity = mview.findViewById(R.id.single_row_donate_now_item_quantity);
            date = mview.findViewById(R.id.single_row_donate_now_date);
        }

    }
}