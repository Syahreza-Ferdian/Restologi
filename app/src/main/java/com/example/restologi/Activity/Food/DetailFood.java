package com.example.restologi.Activity.Food;

import static android.content.Intent.getIntent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.RoundedCorner;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.restologi.Activity.BaseActivity;
import com.example.restologi.Domain.Foods;
import com.example.restologi.Handler.ManagmentCart;
import com.example.restologi.R;
import com.example.restologi.databinding.ActivityDetailFoodBinding;

public class DetailFood extends BaseActivity {

    ActivityDetailFoodBinding binding;
    private Foods object;
    private int num = 1;
    private ManagmentCart managmentCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();

        binding.pic.setOnClickListener(v -> {
            Intent intent = new Intent(DetailFood.this, FoodImageDownloadActivity.class);
            intent.putExtra("IMAGE_LINK", object.getImagePath());
            startActivity(intent);
        });
    }

    private void setVariable() {
        managmentCart = new ManagmentCart(this);

        binding.btBack.setOnClickListener(v -> finish());

        Glide.with(this)
                .load(object.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(60))
                .into(binding.pic);

        binding.tvPrice.setText("Rp"+object.getPrice());
        binding.tvTitle.setText(object.getTitle());
        binding.tvDeskripsi.setText(object.getDescription());
        binding.tvRating.setText(object.getStar()+" Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.tvTotalPrice.setText((num*object.getPrice()+"Rp"));

        binding.btTambah.setOnClickListener(v -> {
            num = num + 1;
            binding.tvJumlah.setText(num + "");
            binding.tvTotalPrice.setText("Rp" + (num * object.getPrice()));
        });

        binding.btKurang.setOnClickListener(v -> {
            if (num > 1) {
                num = num - 1;
                binding.btKurang.setText(num + "");
                binding.tvTotalPrice.setText("Rp" + (num * object.getPrice()));
            }
        });

        binding.btPay.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}
