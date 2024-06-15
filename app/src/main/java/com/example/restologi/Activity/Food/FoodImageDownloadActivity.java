package com.example.restologi.Activity.Food;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.restologi.Activity.BaseActivity;
import com.example.restologi.R;

import java.io.IOException;
import java.io.OutputStream;

public class FoodImageDownloadActivity extends BaseActivity {
    private static final int CREATE_FILE_REQUEST_CODE = 1;

    private ImageView pic_show, btn_back;
    private Button btn_download;
    private Bitmap imageBitmap;
    private String imageFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_download_picture);

        pic_show = findViewById(R.id.pic_show);
        btn_back = findViewById(R.id.bt_back);
        btn_download = findViewById(R.id.btn_download);

        Intent intent = getIntent();
        String imageLink = intent.getStringExtra("IMAGE_LINK");

        Glide.with(this)
                .load(imageLink)
                .transform(new CenterCrop(), new RoundedCorners(60))
                .into(pic_show);

        btn_back.setOnClickListener(v -> finish());

        btn_download.setOnClickListener(v -> {
            downloadImage(imageLink);
        });
    }

    private void downloadImage(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imageBitmap = resource;
                        imageFileName = getFileNameFromUrl(imageUrl);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            openFileExplorerForSave(imageFileName);
                        }
                    }
                });
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void openFileExplorerForSave(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null && imageBitmap != null) {
                    saveImageToSelectedLocation(uri);
                }
            }
        }
    }

    private void saveImageToSelectedLocation(Uri uri) {
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Toast.makeText(this, "Image saved: " + uri.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to save image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
