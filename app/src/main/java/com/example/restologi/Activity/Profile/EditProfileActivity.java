package com.example.restologi.Activity.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.signature.ObjectKey;
import com.example.restologi.Activity.MainActivity;
import com.example.restologi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;

    private EditText editName, editAddress, editPhone, editEmail;
    private ImageView editProfileImage, picture_profile;
    private TextView saveButton, cancelButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editName = findViewById(R.id.edit_nama);
        editAddress = findViewById(R.id.edit_alamat);
        editPhone = findViewById(R.id.edit_phone_number);
        editEmail = findViewById(R.id.edit_email);
        editProfileImage = findViewById(R.id.btn_edit_picture);
        saveButton = findViewById(R.id.tvSave);
        cancelButton = findViewById(R.id.tvCancel);
        picture_profile = findViewById(R.id.imageProfile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        storage = FirebaseStorage.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserProfile(user);
        }

        editProfileImage.setOnClickListener(v -> {
            openFileChooser();
        });

        saveButton.setOnClickListener(v -> {
            saveProfile();
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserProfile(FirebaseUser user) {
        mDatabase.child(user.getUid()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String address = dataSnapshot.child("address").getValue(String.class);
                String phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                editName.setText(name);
                editAddress.setText(address);
                editPhone.setText(phone);
                editEmail.setText(email);

                // Load profile image if exists
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    // Load image with Glide, using a unique signature
                    Glide.with(this)
                            .load(profileImageUrl)
                            .transform(new CenterCrop(), new RoundedCorners(60))
                            .signature(new ObjectKey(System.currentTimeMillis())) // Add unique signature
                            .into(picture_profile);
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            picture_profile.setImageURI(imageUri);
        } else if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            picture_profile.setImageBitmap(bitmap);
            // Convert bitmap to URI
            imageUri = getImageUriFromBitmap(bitmap);
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "ProfileImage", null);
        return Uri.parse(path);
    }

    private void saveProfile() {
        String name = editName.getText().toString();
        String address = editAddress.getText().toString();
        String phone = editPhone.getText().toString();

        if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("name", name);
            userMap.put("address", address);
            userMap.put("phoneNumber", phone);

            if (imageUri != null) {
                StorageReference fileReference = storage.getReference("profile_images").child(user.getUid() + ".jpg");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = fileReference.putBytes(data);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            userMap.put("profileImageUrl", uri.toString());
                            mDatabase.child(user.getUid()).updateChildren(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to upload profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mDatabase.child(user.getUid()).updateChildren(userMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }
    }
}
