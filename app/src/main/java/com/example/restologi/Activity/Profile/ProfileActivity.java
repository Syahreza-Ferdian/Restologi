package com.example.restologi.Activity.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.signature.ObjectKey;
import com.example.restologi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    private Button edit_profile_btn;
    private ImageButton back_btn;
    private ImageView profile_image;
    private TextView tv_nama, tv_email;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edit_profile_btn = findViewById(R.id.MBEditProfile);
        back_btn = findViewById(R.id.IVBack);
        tv_nama = findViewById(R.id.TV_nama);
        tv_email = findViewById(R.id.TV_email);
        profile_image = findViewById(R.id.IVProfileImage);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = mAuth.getCurrentUser();

        edit_profile_btn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        back_btn.setOnClickListener(v -> {
            finish();
        });


        databaseReference.child(user.getUid()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                tv_nama.setText(name);
                tv_email.setText(email);

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profileImageUrl)
                            .transform(new CenterCrop(), new RoundedCorners(60))
                            .signature(new ObjectKey(System.currentTimeMillis()))
                            .into(profile_image);
                }
            }
        });
    }
}