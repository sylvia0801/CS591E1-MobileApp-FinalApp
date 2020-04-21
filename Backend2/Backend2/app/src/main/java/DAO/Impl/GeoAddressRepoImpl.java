package DAO.Impl;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.backend.Activity.PostActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import DAO.GeoAddressRepository;
import Model.Item;

// get loation and update textview and item  address
public class GeoAddressRepoImpl implements GeoAddressRepository {
    private  Activity context;
    public static String mytag="mytag";
    int PERMISSION_ID = 44;
    List<Address> addresses=new ArrayList<>();
    FusedLocationProviderClient mFusedLocationClient;
    Geocoder gCoder ;
   public GeoAddressRepoImpl(Activity context){
        this.context=context;
        gCoder=new Geocoder(context, Locale.getDefault());
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(context);
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };


    @SuppressLint("MissingPermission")
    public void getLastLocation(TextView tv, Item item) {
        Log.i("mytag","in getlast");
            if (isLocationEnabled()) {
                Log.i("mytag","in loc");

                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    try {
                                        Log.i("mytag","suuu loc");

                                        addresses = gCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        if (addresses.size() != 0) {
                                            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                            item.setAddress(address);
                                            tv.setText(address);

                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }
                );
            }else{
                Log.i("mytag","kkkjj");
                requestPermissions();
            }

    }

    public void requestPermissions() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(context,Manifest.permission.ACCESS_FINE_LOCATION) ){
            Log.i("mytag","get allert ");
            new AlertDialog.Builder(context).setTitle("Permission needed!").setMessage("This Permission is needed for find posts nearby").setPositiveButton("OK",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i("mytag","onclikkk");
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_ID
                    );
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
dialog.dismiss();
                }
            }).create().show();
        }else {
            Log.i("mytag","nnallert");

            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ID
            );
        }

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
}
