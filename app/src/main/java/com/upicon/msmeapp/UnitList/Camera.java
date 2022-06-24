package com.upicon.msmeapp.UnitList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.upicon.msmeapp.R;
import com.upicon.msmeapp.UtilsMethod.UtilsCamera;
import com.upicon.msmeapp.UtilsMethod.UtilsMap;
import com.upicon.msmeapp.UtilsMethod.UtilsPermissions;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Camera extends AppCompatActivity {

    private UtilsPermissions utilsPermissions;
    private UtilsCamera utilsCamera;
    private UtilsMap utilsMap;

    private TextureView textureView;
    private ImageButton imageCaptureButton;
    LinearLayout linearlayout;

    private GoogleMap googleMap;
    TextView address,latLong,tvTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        utilsPermissions = new UtilsPermissions(this);

        if (utilsPermissions.hasAllowedAllPermissions()) initializeFragmentContainer();
        else utilsPermissions.requestPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initializeFragmentContainer();
    }

    private void initializeFragmentContainer() {
        utilsCamera = new UtilsCamera(this);
        utilsMap = new UtilsMap(this);

        address=findViewById(R.id.tv_address);
        latLong=findViewById(R.id.tv_lat);
        tvTime=findViewById(R.id.tv_time);

        textureView = findViewById(R.id.view_finder);
        imageCaptureButton = findViewById(R.id.imageCaptureButton);
        linearlayout=findViewById(R.id.linearlayout);

        startCamera();
        startMap();
        setData();

    }

    private void setData() {
        List<Address> addresses;

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(utilsMap.getCurrentPosition().latitude, utilsMap.getCurrentPosition().longitude, 1);
            address.setText(addresses.get(0).getAddressLine(0));
            latLong.setText("LatLong: "+utilsMap.getCurrentPosition().latitude+", "+utilsMap.getCurrentPosition().longitude+"");

            SharedPreferences.Editor editor = getSharedPreferences("MySP", MODE_PRIVATE).edit();
            editor.putString("address", addresses.get(0).getAddressLine(0));
            editor.putString("latitude", utilsMap.getCurrentPosition().latitude+"");
            editor.putString("longitude", utilsMap.getCurrentPosition().longitude+"");
            editor.apply();



        } catch (Exception err) {
            err.printStackTrace();
        }

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, h:mm a");
        String dateString = sdf.format(date);
        tvTime.setText(dateString+"");

    }

    private void startMap() {
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);

        mapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(utilsMap.getCurrentPosition(), 15));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(utilsMap.getCurrentPosition())
                    .title("")
                    .snippet("");
            this.googleMap.addMarker(markerOptions);
        });
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {
        CameraX.unbindAll();
        Preview preview = utilsCamera.configurePreview(textureView);
        final ImageCapture imageCapture = utilsCamera.configureImageCapture();
        imageCaptureButton.setOnClickListener(v -> {
            utilsCamera.takePicture(imageCapture,linearlayout);
        });
        CameraX.bindToLifecycle(this, preview, imageCapture);
    }
}