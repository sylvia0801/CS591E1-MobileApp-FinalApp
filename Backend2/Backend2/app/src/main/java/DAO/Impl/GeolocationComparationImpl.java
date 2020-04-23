package DAO.Impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import Model.Item;

public class GeolocationComparationImpl {
    public static void sortItems(List<Item>res,Location location,Geocoder gCoder){
        Collections.sort(res,new SortPlaces(location,gCoder));
    }
}
class SortPlaces implements Comparator<Item> {
    Location currentLoc;
    Geocoder gCoder;
    static String mytag="mytag";
    public SortPlaces(Location location,Geocoder gCoder){
        this.gCoder=gCoder;
        currentLoc=location;
    }
    @Override
    public int compare(final Item place1, final Item place2) {
        double lat1 = 0;
        double lon1 = 0;
        double lat2 = 0;
        double lon2 = 0;
        try {
            List<Address> res=gCoder.getFromLocationName(place1.getAddress(),5);
            if(res!=null){
                lat1 = res.get(0).getLatitude();
                lon1 = res.get(0).getLongitude();
            }
            res=gCoder.getFromLocationName(place2.getAddress(),5);
            if(res!=null){
                lat2=res.get(0).getLatitude();
                lon2=res.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.i(mytag,""+lat1+lon1);
        //Log.i(mytag,""+lat2+lon2);
        double distanceToPlace1 = distance(currentLoc.getLatitude(), currentLoc.getLongitude(), lat1, lon1);
        double distanceToPlace2 = distance(currentLoc.getLatitude(), currentLoc.getLongitude(), lat2, lon2);
        return (int) (distanceToPlace1 - distanceToPlace2);
    }

    public double distance(double fromLat, double fromLon, double toLat, double toLon) {
        double radius = 6378137;   // approximate Earth radius, *in meters*
        double deltaLat = toLat - fromLat;
        double deltaLon = toLon - fromLon;
        double angle = 2 * Math.asin( Math.sqrt(
                Math.pow(Math.sin(deltaLat/2), 2) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                                Math.pow(Math.sin(deltaLon/2), 2) ) );
        return radius * angle;
    }
}

