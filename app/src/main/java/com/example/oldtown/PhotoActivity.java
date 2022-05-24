package com.example.oldtown;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

public class PhotoActivity extends Activity {
    public PhotoActivity() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);
        Intent fromMain = getIntent();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String name = fromMain.getStringExtra("name");
        String describe = fromMain.getStringExtra("describe");

        String resOld = fromMain.getStringExtra("resOld");
        Task<Uri> oldT = storage.getReferenceFromUrl(resOld).getDownloadUrl();

        String resNew = fromMain.getStringExtra("resNew");
        Task<Uri> newT = storage.getReferenceFromUrl(resNew).getDownloadUrl();

        TextView txt = (TextView) findViewById(R.id.photoName);
        ImageView old = (ImageView) findViewById(R.id.oldPhoto);
        ImageView newPhoto = (ImageView) findViewById(R.id.newPhoto);
        TextView desc = findViewById(R.id.describe);
        while (!newT.isComplete() | !oldT.isComplete()){
        }
        Glide.with(this /* context */)
                .load(newT.getResult())
                .into(newPhoto);
        Glide.with(this /* context */)
                .load(oldT.getResult())
                .into(old);

        txt.setText(name);
        desc.setText(describe);

    }
}