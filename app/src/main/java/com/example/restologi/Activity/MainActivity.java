package com.example.restologi.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.signature.ObjectKey;
import com.example.restologi.Activity.BaseActivity;
import com.example.restologi.Activity.Cart.CartActivity;
import com.example.restologi.Activity.Profile.ProfileActivity;
import com.example.restologi.Adapter.CategoryAdapter;
import com.example.restologi.Adapter.SliderAdapter;
import com.example.restologi.Domain.Category;
import com.example.restologi.Domain.SliderItems;
import com.example.restologi.R;
import com.example.restologi.databinding.ActivityMainBinding;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCategory();
        initBanner();
        setVariable();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = mAuth.getCurrentUser();

        databaseReference.child(user.getUid()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                String nama = dataSnapshot.child("name").getValue(String.class);

                assert nama != null;
                if (!nama.isEmpty()) {
                    binding.textView5.setText(nama);
                }

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profileImageUrl)
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .transform(new CenterCrop(), new RoundedCorners(60))
                            .into(binding.imageView7);
                }
            }
        });

        binding.imageView7.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent1);
        });
    }

    private void initBanner() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        SliderItems sliderItem = issue.getValue(SliderItems.class);
                        if (sliderItem != null) {
                            items.add(sliderItem);
                            Log.d("BANNER_ITEM", "Image URL: " + sliderItem.getImage());
                        } else {
                            Log.e("BANNER_ITEM", "SliderItem is null");
                        }
                    }
                    if (!items.isEmpty()) {
                        banner(items);
                    } else {
                        Log.e("BANNER_ITEM", "No items found");
                    }
                    binding.progressBarBanner.setVisibility(View.GONE);
                } else {
                    Log.e("BANNER_ITEM", "No snapshot exists");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BANNER_ITEM", "DatabaseError: " + error.getMessage());
            }
        });
    }


    private void banner(ArrayList<SliderItems> items) {
        SliderAdapter sliderAdapter = new SliderAdapter(items, binding.viewpager2);
        binding.viewpager2.setAdapter(sliderAdapter);
        binding.viewpager2.setClipChildren(false);
        binding.viewpager2.setClipToPadding(false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpager2.setPageTransformer(compositePageTransformer);
    }
    private void setVariable() {
        binding.bottomMenu.setItemSelected(R.id.home, true);
        binding.bottomMenu.setOnItemSelectedListener(i -> {
            if (i == R.id.cart) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
            if (i == R.id.profile) {
                Intent intent1 = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent1);
            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef=database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list=new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue:snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (list.size()>0) {
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                        binding.categoryView.setAdapter(new CategoryAdapter(list));
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}