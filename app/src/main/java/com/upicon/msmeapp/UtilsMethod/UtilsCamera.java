package com.upicon.msmeapp.UtilsMethod;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.core.content.ContextCompat;


import com.upicon.msmeapp.UnitList.AddUnit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UtilsCamera {

    private Activity context;
    File mapFile;

    //define constructor
    public UtilsCamera(Activity context) {
        this.context = context;
    }

    //Get the current location via the network provider
    private Location getCurrentLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            return location;
        }

        return null;
    }

    //Callback for what will happen when the image is saved
    private ImageCapture.OnImageSavedListener getOnImageSavedListener() {
        return new ImageCapture.OnImageSavedListener() {
            @Override
            public void onImageSaved(File file) {
                String message = "Saved at" + file.getAbsolutePath();
                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                Intent intent=new Intent(context, AddUnit.class);
                intent.putExtra("imagePath",file.getAbsolutePath());
                intent.putExtra("imagePathMap",mapFile.getAbsolutePath());
                context.startActivity(intent);
                context.finish();
            }

            @Override
            public void onError(
                    ImageCapture.ImageCaptureError imageCaptureError,
                    String errorMessage,
                    Throwable cause) {
                    String message = "Capture failed : " + errorMessage;
                    //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(cause != null){
                    cause.printStackTrace();
                }
            }
        };
    }

    //Update the texture view based on rotation
    private void updateTransform(TextureView textureView) {
        Matrix matrix = new Matrix();

        float cX = textureView.getMeasuredWidth() / 2f;
        float cY = textureView.getMeasuredHeight() / 2f;

        float rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0f;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90f;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180f;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270f;
                break;
            default:
                return;
        }

        matrix.postRotate(rotationDgr, cX, cY);
        textureView.setTransform(matrix);
    }

    //Configure the preview for the camera
    public Preview configurePreview(TextureView textureView) {
        PreviewConfig previewConfig = new PreviewConfig
                .Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .build();
        Preview preview = new Preview(previewConfig);

        preview.setOnPreviewOutputUpdateListener(output -> {
            ViewGroup parent = (ViewGroup) textureView.getParent();
            parent.removeView(textureView);
            parent.addView(textureView, 0);

            textureView.setSurfaceTexture(output.getSurfaceTexture());
            updateTransform(textureView);
        });

        return preview;
    }

    //Configure the imageCapture
    public ImageCapture configureImageCapture() {
        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig
                .Builder()
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(
                        context
                        .getWindowManager()
                        .getDefaultDisplay()
                        .getRotation())
                .build();

        ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);

        return imageCapture;
    }

    //Called when the user captures an image
    @SuppressLint("RestrictedApi")
    public void takePicture(ImageCapture imageCapture, View drawView) {
        File path;
        Log.e("osVersion",Build.VERSION.SDK_INT+"");

        if (Build.VERSION.SDK_INT >Build.VERSION_CODES.Q)
            path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Geo_Camera");
        else
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Geo_Camera");

        path.mkdirs();


        //create image from camera preview
        File file = new File(path, "IMG_" + System.currentTimeMillis() + ".jpeg");
        ImageCapture.Metadata metadata = new ImageCapture.Metadata();
        // Add the current location to the metadata
        metadata.location = getCurrentLocation();
        imageCapture.takePicture(file, metadata, CameraXExecutors.mainThreadExecutor(), getOnImageSavedListener());


        //create image of map layout
        mapFile = new File(path, "IMG_MAP" + System.currentTimeMillis() + ".jpeg");
        Bitmap mapBitmap = getBitmapFromView(drawView);
        try {

            file.createNewFile();
            FileOutputStream oStream = new FileOutputStream(mapFile);
            mapBitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();

        }
        catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
    }

    //save layout view into image
    public void saveBitMap(Context context, View drawView){
        File path;
        Log.e("osVersion",Build.VERSION.SDK_INT+"");

        if (Build.VERSION.SDK_INT >Build.VERSION_CODES.Q)
            path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Geo_Camera");
        else
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Geo_Camera");

        path.mkdirs();

        File file = new File(path, "IMG_MAP" + System.currentTimeMillis() + ".jpeg");
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            file.createNewFile();
            FileOutputStream oStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
    }

    //get bitmap from view
    private Bitmap getBitmapFromView (View view){
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}
