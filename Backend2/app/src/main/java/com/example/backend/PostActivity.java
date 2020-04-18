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


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import DAO.Impl.DownloadImageAsync;
import DAO.Impl.GeoAddressRepoImpl;
import DAO.Impl.ItemRepoImpl;
import Model.Item;

public class PostActivity extends AppCompatActivity {
    private Button btn_cancel;
    private Button btn_post;
    private EditText et_description;
    private EditText et_title;
    private EditText et_price;
    private Spinner spinnerTag;
    private ImageButton ib_camera;
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
    final int camera_get_code=111;
    final int album_get_code=999;
   // private  Context context;
    public static String mytag="mytag";
    int PERMISSION_ID = 44;
    private GeoAddressRepoImpl geo;
    private ItemRepoImpl itemService=new ItemRepoImpl();
    Item item=new Item();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        geo=new GeoAddressRepoImpl((Activity) this);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_post = (Button)findViewById(R.id.btn_post);
        et_description = (EditText)findViewById(R.id.et_description);
        et_title = (EditText)findViewById(R.id.et_title);
        ib_album=(ImageButton) findViewById(R.id.ib_album);
        et_price = (EditText)findViewById(R.id.et_price);
        spinnerTag = (Spinner)findViewById(R.id.spinnerTag);
        ib_camera = (ImageButton)findViewById(R.id.ib_camera);
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
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
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
                // update address
                geo.getLastLocation(tv_address,item);
                Log.i(mytag,"address:::"+item.getAddress());
                Log.i(mytag,"updated before"+item);
                Toast.makeText(getApplicationContext(), "Posting...", Toast.LENGTH_SHORT).show();
              // update url to database
                itemService.update(item,0);
                Log.i(mytag,"posteddddd after update"+item);
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

        @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geo.getLastLocation(tv_address,null);
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
            Log.i(mytag,"get album"+imageuri.toString());
          //  iv_picture.setImageURI(imageuri);
            // Log.i(mytag,"setimv");
            saveItemToDatabase(imageuri);
            //    uploadFile();  // if cancel post new then remove pics itemidxx;
            Log.i(mytag,"upload to data");

        }


    }

    public void saveItemToDatabase(Uri url){
      //  Item nitem=new Item();
       // ItemRepoImpl i=new ItemRepoImpl();
      itemService.saveToAllTable(item);
        String k=item.getItemId();
        finalkey=k;
       // Log.i(mytag,"ddd"+finalkey);
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
        Log.i(mytag,"myiddd"+itemidxxx);
        StorageTask uploasTask;
        StorageReference storageRef =    FirebaseStorage.getInstance().getReference("Pics");
        String itemid=itemidxxx;
        // Uri picurl=Uri.parse(url);
        if(picurl!=null){
            StorageReference picRef = storageRef.child(itemid).child(System.currentTimeMillis()+"."+getExtention(picurl));
            uploasTask= picRef.putFile(picurl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot >() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    finalurl=uri.toString();
                                    Log.i(mytag,"lluy"+finalkey+" "+finalurl);
                                   // it=new Item();
                                    item.setImageUrl(finalurl);
                                    item.setItemId(finalkey);
                                    Log.i(mytag,"imvss"+item.getImageUrl().toString());
                                    //  imv.setImageBitmap(getImageBitmapFromUrl(it.getImageUrl()));
                                   itemService.update(item,0);
                                    new DownloadImageAsync(iv_picture,item.getImageUrl()).execute();

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
    private String getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return path;
    }
}
