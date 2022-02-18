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

import java.lang.reflect.Field;
import java.util.Map;

public class PhotoActivity extends Activity {
    public PhotoActivity() {}
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_view);
        Intent fromMain = getIntent();
        String name = fromMain.getStringExtra("name");
        String resOld = fromMain.getStringExtra("resOld");
        String resNew = fromMain.getStringExtra("resNew");
        int drawableResourceIdNew = this.getResources().getIdentifier(resNew, "drawable", this.getPackageName());
        int drawableResourceIdOld = this.getResources().getIdentifier(resOld, "drawable", this.getPackageName());
        TextView txt = (TextView) findViewById(R.id.photoName);
        ImageView old = (ImageView) findViewById(R.id.oldPhoto);
        ImageView newPhoto = (ImageView) findViewById(R.id.newPhoto);
        newPhoto.setImageResource(drawableResourceIdNew);
        old.setImageResource(drawableResourceIdOld);
        txt.setText(name);

    }
}