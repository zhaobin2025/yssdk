package com.yasee.yaseejava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.yasee.yaseejava.databinding.ActivityOcrBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class Ocr extends AppCompatActivity {

    private ActivityOcrBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityOcrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // 初始化 Launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri == null) return;
                        binding.orcShowImg.setImageURI(uri);

                    }
                }
        );

        binding.orcSelectImg.setOnClickListener((view) -> {
            imagePickerLauncher.launch("image/*");
        });

        binding.orcRun.setOnClickListener((view) -> {


        });



    }


    private ActivityResultLauncher<String> imagePickerLauncher;
    private Bitmap uriToBitmap(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




}