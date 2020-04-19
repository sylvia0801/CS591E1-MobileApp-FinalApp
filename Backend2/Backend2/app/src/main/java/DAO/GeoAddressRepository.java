package DAO;

import android.app.Activity;
import android.widget.TextView;

import Model.Item;

public interface GeoAddressRepository {
     void getLastLocation(TextView tv, Item item) ;
}
