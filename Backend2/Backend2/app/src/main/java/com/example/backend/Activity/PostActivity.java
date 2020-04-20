package com.example.backend.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
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
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.example.backend.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
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
    private Button btn_address;
    String description;
    String title;
    String price;
    public   String finalurl;
    public String finalkey;
    Uri imageuri;
   // final int camera_get_code=111;
    final int album_get_code=999;
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
        btn_address=(Button)findViewById(R.id.btn_address);
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
                    item.setTagId("3");
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
                AlertDialog.Builder bld= new AlertDialog.Builder(PostActivity.this);
                bld. setTitle("Alert");
                bld.setMessage("Are you sure to leave this page?");
                bld.setCancelable(true);
                bld.setPositiveButton("Yes, I want to leave.",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which) {
                        item=new Item();
                        imageuri=null;
                        finalkey="";
                        Intent intent = new Intent(PostActivity.this, MainPageActivity.class);
                        startActivity(intent);
                    }
                });
                bld.setNegativeButton("No",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                bld.show();

            }
        });

        ib_album.setOnClickListener(new View.OnClickListener() {
            @Override
            // update image uri
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
                if(validateForm()) {
                    description = et_description.getText().toString();
                    title = et_title.getText().toString();
                    price = et_price.getText().toString();
                    item.setDescription(description);
                    item.setPrice(price);
                    item.setTitle(title);
                    FirebaseUser cuser = FirebaseAuth.getInstance().getCurrentUser();
                    item.setSellerName(cuser.getDisplayName());
                    item.setSellerId(cuser.getUid());
                      if(imageuri==null){
                        AlertDialog.Builder bld= new AlertDialog.Builder(PostActivity.this);
                        bld. setTitle("Alert");
                        bld.setMessage("Are you sure to post this item without image?");
                        bld.setCancelable(true);
                        bld.setPositiveButton("Yes, post without image.",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which) {
                                // save item record  to database
                                saveItemToDatabase(imageuri);
                                Toast.makeText(PostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PostActivity.this, MainPageActivity.class);
                                startActivity(intent);
                            }
                        });
                        bld.setNegativeButton("No",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        bld.show();
                    }else{
                          // save item record  to database
                          saveItemToDatabase(imageuri);
                          Toast.makeText(PostActivity.this, "Posted!", Toast.LENGTH_SHORT).show();
                          Intent intent = new Intent(PostActivity.this, MainPageActivity.class);
                          startActivity(intent);
                      }


                }
            }
        });

        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if(ActivityCompat.checkSelfPermission(PostActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                     geo.requestPermissions();
                 }
                 else {
                     geo.requestPermissions();

                 }

            }
        });
    }
    private   boolean validateForm(){
        boolean result = true;
        if (TextUtils.isEmpty(et_description.getText().toString())) {
            et_description.setError("Required");
            result = false;
        }
        else if(TextUtils.isEmpty(et_title.getText().toString())){
            et_title.setError("Required");
            result = false;
        }else  if (TextUtils.isEmpty(et_price.getText().toString())) {
            et_price.setError("Required");
            result = false;
        }else  if (TextUtils.isEmpty(tv_address.getText().toString())) {
            tv_address.setError("Permisson Required");
            result = false;
        }else {
            try{
                Double.parseDouble(et_price.getText().toString());
            }catch (Exception e){
                et_price.setError("Please enter Number!");
                result=false;
            }
        }

        return result;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                geo.getLastLocation(tv_address,item);

            }else{
                geo.requestPermissions();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
            iv_picture.setImageURI(imageuri);
        }


    }

    public void saveItemToDatabase(Uri url){
      itemService.saveToAllTable(item);
        finalkey=item.getItemId();
        // upload uri to firebase storage
        uploadFile(finalkey,url);

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
                                    // update item info in database with firebase storage url
                                    itemService.update(item,0);

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

    public String getExtention(Uri uri){
        ContentResolver cs=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cs.getType(uri));

    }

    private void openFileChooser(){
        Intent i=new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,album_get_code);
    }
//    private String getImageUri(Context context, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
//        return path;
//    }
}
