package com.example.backend.Activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.backend.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import DAO.Impl.DownloadImageAsync;
import DAO.Impl.FavouriteRepoImpl;
import DAO.Impl.ItemRepoImpl;
import DAO.Impl.UserRepoImpl;
import Model.Favourite;
import Model.Item;
import Model.User;

public class MainActivity extends AppCompatActivity{
    public static String mytag="mytag";
private ImageView imv;
public   String finalurl;
public String finalkey;
Item it;
    Uri imageuri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // imv=(ImageView) findViewById(R.id.imv);
        //openFileChooser();


//        FirebaseDatabase.getInstance().getReference("All").removeValue();
//        FirebaseDatabase.getInstance().getReference("Item").removeValue();
      //  FirebaseDatabase.getInstance().getReference("Favourite").removeValue();


        ItemRepoImpl i=new ItemRepoImpl();


    }

}
