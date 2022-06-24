package com.upicon.msmeapp.UtilsMethod;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationManagers {


    public  String [] LocationDetails(Context context){

        String[] location = new String[6];


        GpsTracker  gpsTracker = new GpsTracker(context);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(Double.parseDouble(Double.toString(gpsTracker.getLatitude())),Double.parseDouble(Double.toString(gpsTracker.getLongitude())),1);

            } catch (IOException e) {
                e.printStackTrace();
            }


            Address address = addresses.get(0);

            location[0]=String.valueOf(latitude);
            location[1]=String.valueOf(longitude);
            location[2]=address.getSubLocality();
            location[3]=address.getLocality();
            location[4]=address.getAdminArea();
            location[5]=address.getPostalCode();

        }else{
            gpsTracker.showSettingsAlert();
        }
        return location;
    }
}
