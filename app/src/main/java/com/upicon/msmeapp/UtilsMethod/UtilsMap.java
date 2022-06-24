package com.upicon.msmeapp.UtilsMethod;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class UtilsMap {
    private Activity context;

    public UtilsMap(Activity context) {
        this.context = context;
    }

    // Get the current location based on the network
    public LatLng getCurrentPosition() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            try {
                return new LatLng(location.getLatitude(), location.getLongitude());
            } catch (Exception err) {
                return new LatLng(0,0);
            }
        }

        return new LatLng(0, 0);
    }

    // Get the address by reverse-geocoding
    private Address getReverseGeoLocation(LatLng latLng) {
        List<Address> addresses;

        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            return addresses.get(0);
        } catch (Exception err) {
            err.printStackTrace();
        }

        return null;
    }



}
