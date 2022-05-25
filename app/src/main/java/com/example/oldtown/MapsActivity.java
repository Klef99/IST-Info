package com.example.oldtown;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.oldtown.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {
    private GoogleMap mMap;
private ActivityMapsBinding binding;
private HashMap<Integer, Markers> markersMap = new HashMap<>();
private Map<String, Integer> nameIntMap;
private FirebaseStorage storage = FirebaseStorage.getInstance();
private String pathToJson;


    public MapsActivity() {}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button MainBtn = findViewById(R.id.mainBtn);
        MainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, MainMenuActivity.class);
                intent.putExtra("path", pathToJson);
                MapsActivity.this.startActivity(intent);
            }
        });
        Button mapBtn = findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                MapsActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMinZoomPreference(12);
        LatLng blag = new LatLng(50.282632, 127.536498);
        mMap.addMarker(new MarkerOptions().position(blag).title("Благовещенск"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(blag));
        try {
            genMarkerFromJson();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        googleMap.setOnInfoWindowClickListener(this);
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, "Открыта метка",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PhotoActivity.class);
        String message = marker.getTitle();
        Markers markers = this.markersMap.get(this.nameIntMap.get(message));
        intent.putExtra("name", message);
        assert markers != null;
        intent.putExtra("resOld", markers.photoResOld);
        intent.putExtra("resNew", markers.photoResNew);
        intent.putExtra("describe", markers.describe);
        startActivity(intent);
    }
    public void genMarkerFromJson() throws IOException, InterruptedException {
        Context context = getBaseContext();
        Gson g = new Gson();
        StorageReference jsonRef = this.storage.getReferenceFromUrl("gs://old-town-338512.appspot.com/BLG/images.json");
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File localFile = File.createTempFile("images", ".json", outputDir);
        pathToJson = localFile.getAbsolutePath();
        StorageTask<FileDownloadTask.TaskSnapshot> task = jsonRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", ";local tem file created  created " + localFile.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", ";local tem file not created  created " + exception.toString());
            }
        });
        while (!task.isComplete()){
        }
//        InputStream is = context.getResources().openRawResource(R.raw.images);
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
            mMap.addMarker(new MarkerOptions().position(new LatLng(data.v, data.v1)).title(data.name));
            this.nameIntMap.put(data.name, i);
        }
    }
}