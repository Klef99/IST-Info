package com.example.oldtown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainMenuActivity extends Activity {
    private HashMap<Integer, Markers> markersMap = new HashMap<>();
    private Map<String, Integer> nameIntMap;
    private String pathToJson;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private ImageView generateImageView(String url){
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 350);
        imageView.setLayoutParams(params);
        Task<Uri> downloadUrl = storage.getReferenceFromUrl(url).getDownloadUrl();
        while (!downloadUrl.isComplete()){}
        Glide.with(this /* context */)
                .load(downloadUrl.getResult())
                .into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
    public void genCard(LinearLayout Vi, Markers markers){
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // CardView width
                LinearLayout.LayoutParams.WRAP_CONTENT // CardView height
        );
        layoutParams.setMargins(20, 20, 20, 20);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(16F);
        cardView.setContentPadding(25, 25, 25, 25);
        cardView.setCardBackgroundColor(Color.LTGRAY);
        cardView.setCardElevation(8F);
        cardView.setMaxCardElevation(8F);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(
                        getBaseContext(),
                        "Card clicked.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainMenuActivity.this, PhotoActivity.class);
                assert markers != null;
                intent.putExtra("name", markers.name);
                intent.putExtra("resOld", markers.photoResOld);
                intent.putExtra("resNew", markers.photoResNew);
                intent.putExtra("describe", markers.describe);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView textView = new TextView(this);
        textView.setText(markers.name);
        linearLayout.addView(generateImageView(markers.photoResNew));
        linearLayout.addView(textView);
        cardView.addView(linearLayout);
        Vi.addView(cardView);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        Intent intent = getIntent();
        pathToJson = intent.getStringExtra("path");
        try {
            genMarkerFromJson();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LinearLayout scrollView = findViewById(R.id.listsll);
        Button MainBtn = findViewById(R.id.mainBtn);
        Button MapBtn = findViewById(R.id.mapBtn);
        Button QuestBtn = findViewById(R.id.questBtn);
        MainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMenuActivity.this, MainMenuActivity.class);
                MainMenuActivity.this.startActivity(intent);
            }
        });
        MapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainMenuActivity.this, MapsActivity.class);
                MainMenuActivity.this.startActivity(myIntent);
            }
        });
        for (int i = 1; i <= markersMap.size(); i++) {
            Markers markers = markersMap.get(i);
            genCard(scrollView, markers);
        }

    }
    public void genMarkerFromJson() throws IOException, InterruptedException {
        Context context = getBaseContext();
        Gson g = new Gson();
        File localFile = new File(this.pathToJson);
        InputStream is = new FileInputStream(localFile);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String json = new String(buffer, StandardCharsets.UTF_8);
        Type type = new TypeToken<Map<Integer, Markers>>(){}.getType();
        Map<Integer, Markers> tmp = g.fromJson(json, type);
        this.nameIntMap = new HashMap<>();
        for (int i = 1; i <= tmp.size(); i++) {
            this.markersMap.put(i, tmp.get(i));
            Markers data = this.markersMap.get(i);
            assert data != null;
            this.nameIntMap.put(data.name, i);
        }
    }
}
