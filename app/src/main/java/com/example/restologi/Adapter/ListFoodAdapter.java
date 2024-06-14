package com.example.restologi.Adapter;

import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.restologi.Activity.Food.DetailFood;
import com.example.restologi.Domain.Foods;
import com.example.restologi.R;

import java.util.ArrayList;

public class ListFoodAdapter extends RecyclerView.Adapter<ListFoodAdapter.viewholder> {

    ArrayList<Foods> items;
    Context context;

    public ListFoodAdapter(ArrayList<Foods> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ListFoodAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new viewholder(LayoutInflater.from(context).inflate(R.layout.list_food_viewholder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListFoodAdapter.viewholder holder, int position) {
        holder.tv_title.setText(items.get(position).getTitle());
        holder.tv_time.setText(items.get(position).getTimeValue() + " min");
        holder.tv_totalPrice.setText("Rp" + items.get(position).getPrice());
        holder.tv_rating.setText("" + items.get(position).getStar());

        Glide.with(context)
                .load(items.get(position).getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(50))
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailFood.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_totalPrice,tv_rating, tv_time;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_totalPrice = itemView.findViewById(R.id.tv_totalPrice);
            tv_rating = itemView.findViewById(R.id.tv_rating);
            tv_time = itemView.findViewById(R.id.tv_time);
            pic = itemView.findViewById(R.id.img);
        }
    }
}
