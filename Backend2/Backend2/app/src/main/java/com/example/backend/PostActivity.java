package com.example.backend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import DAO.Impl.GeoAddressRepoImpl;
import DAO.Impl.ItemRepoImpl;
import Model.Item;

// post a new item to the platform
public class PostActivity extends AppCompatActivity {
    private Button btn_cancel;
    private Button btn_post;
    private EditText et_description;
    private EditText et_title;
    private EditText et_price;
    private Spinner spinnerTag;
   // private ImageButton ib_camera;
    private ImageButton ib_album;
    private ImageView iv_picture;
    private TextView tv_address;
    String description;
    String title;
    String price;
    private ImageView imv;
    public   String finalurl;
    public String finalkey;
    Uri imageuri;
   // final int camera_get_code=111;
    final int album_get_code=999;
    public static String mytag="mytag";
    private int PERMISSION_ID = 44;
    private GeoAddressRepoImpl geo;
    private ItemRepoImpl itemService=new ItemRepoImpl();
    private StorageReference storageRef ;
    Item item=new Item();// the newly posted item to be added to database
    private String picname; // picture name in the database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        storageRef=  FirebaseStorage.getInstance().getReference("Pics");
        geo=new GeoAddressRepoImpl((Activity) this);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_post = (Button)findViewById(R.id.btn_post);
        et_description = (EditText)findViewById(R.id.et_description);
        et_title = (EditText)findViewById(R.id.et_title);
        ib_album=(ImageButton) findViewById(R.id.ib_album);
        et_price = (EditText)findViewById(R.id.et_price);
        spinnerTag = (Spinner)findViewById(R.id.spinnerTag);
        //ib_camera = (ImageButton)findViewById(R.id.ib_camera);
        iv_picture = (ImageView)findViewById(R.id.iv_picture);
        tv_address=(TextView) findViewById(R.id.et_address);
        String[] tags = new String[]{"Clothes", "Books", "Electronics", "Furnitures"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tags);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTag.setAdapter(adapter);

        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String result = parent.getItemAtPosition(position).toString();

                if (result.equals("Clothes")) {
                    item.setTagId("0");
                }
                else if (result.equals("Books")) {
                    item.setTagId("1");
                }
                else if (result.equals("Electronics")) {
                    item.setTagId("2");
                }
                else if (result.equals("Furnitures")) {
                    item.setTagId("0");
                }
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               storageRef.child(finalkey).child(picname).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
              public void onSuccess(Void aVoid) {
            FirebaseAuth auth=FirebaseAuth.getInstance();
            String username=auth.getCurrentUser().getDisplayName();
            itemService.deleteItemByItemId(finalkey);
            Intent intent = new Intent(PostActivity.this, MainActivity.class);
            startActivity(intent);
            // Success
             }
             }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Error
        }
         });

            }
        });
        ib_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

//        ib_camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, camera_get_code);
//            }
//        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = et_description.getText().toString();
                title = et_title.getText().toString();
                price = et_price.getText().toString();
                item.setDescription(description);
                item.setPrice(price);
                item.setTitle(title);
                item.setSellerName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                Log.i(mytag,"address::: before"+item);
                // update address
                geo.getLastLocation(tv_address,item,PostActivity.this);


              // update url to database
//                itemService.update(item,0);
//                Intent intent = new Intent(PostActivity.this, MainActivity.class);
//                startActivity(intent);
            }
        });
    }

        @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geo.getLastLocation(tv_address,null,PostActivity.this);
            }
        }
    }

    //@Override
//    public void onResume(){
//        super.onRescume();
//        if (checkPermissions()) {
//            getLastLocation();
//        }
//
//    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==camera_get_code) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            iv_picture.setImageBitmap(bitmap);
//            item.setImageUrl(getImageUri(this, bitmap));
//            Log.i(mytag, "zzz" + item.getImageUrl());
//        }
        if(requestCode==album_get_code&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            imageuri=data.getData();
            saveItemToDatabase(imageuri);
        }


    }

    public void saveItemToDatabase(Uri url){
      itemService.saveToAllTable(item);
        String k=item.getItemId();
        finalkey=k;
        uploadFile(finalkey,url);

    }
    public String getExtention(Uri uri){
        ContentResolver cs=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cs.getType(uri));

    }



    private void openFileChooser(){
       Log.i(mytag,"open firechooser");
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,album_get_code);
    }
    public void uploadFile(String itemidxxx, Uri picurl){
        StorageTask uploasTask;
        String itemid=itemidxxx;
        if(picurl!=null){
             picname=System.currentTimeMillis()+"."+getExtention(picurl);
            StorageReference picRef = storageRef.child(itemid).child(picname);
            uploasTask= picRef.putFile(picurl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot >() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    finalurl=uri.toString();
                                    item.setImageUrl(finalurl);
                                    item.setItemId(finalkey);
                                    Log.i(mytag,"imvss"+item.getImageUrl().toString());
                                    itemService.update(item,0);
                                    Glide.with(PostActivity.this).load(item.getImageUrl()).into(iv_picture);

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                        }
                    });

        }

    }
//    private String getImageUri(Context context, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
//        return path;
//    }
}
