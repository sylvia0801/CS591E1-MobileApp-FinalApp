package com.example.backend;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

import DAO.Impl.ChatRepoImpl;
import DAO.Impl.DownloadImageAsync;
import DAO.Impl.FavouriteRepoImpl;
import DAO.Impl.ItemRepoImpl;
import DAO.Impl.MyCallable;
import DAO.Impl.UserRepoImpl;
import Model.Chat;
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


//     public String getExtention(Uri uri){
//        ContentResolver cs=getContentResolver();
//        MimeTypeMap mime=MimeTypeMap.getSingleton();
//        return  mime.getExtensionFromMimeType(cs.getType(uri));
//
//    }

//
//
//    private void openFileChooser(){
//         Log.i(mytag,"open firechooser");
//        Intent i=new Intent();
//       i.setType("image/*");
//      i.setAction(Intent.ACTION_GET_CONTENT);
//       startActivityForResult(i,999);
//
//
//    }

    //@Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==999&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
//            imageuri=data.getData();
//
//Log.i(mytag,"get phone"+imageuri.toString());
//
//            imv.setImageURI(imageuri);
//     // Log.i(mytag,"setimv");
//      saveItemToDatabase(imageuri);
//
//
//  //    uploadFile();  // if cancel post new then remove pics itemidxx;
//      Log.i(mytag,"upload to data");
//
//        }
//    }

//
//    public void saveItemToDatabase(Uri url){
//         Item nitem=new Item();
//
//       ItemRepoImpl i=new ItemRepoImpl();
//       nitem=i.saveToAllTable(nitem);
//
//       String k=nitem.getItemId();
//       finalkey=k;
//       uploadFile(finalkey,url);
//
//    }


//    public void uploadFile(String itemidxxx, Uri picurl){
//      Log.i(mytag,"myiddd"+itemidxxx);
//        StorageTask uploasTask;
//        StorageReference storageRef =    FirebaseStorage.getInstance().getReference("Pics");
//        String itemid=itemidxxx;
//       // Uri picurl=Uri.parse(url);
//        if(picurl!=null){
//            StorageReference picRef = storageRef.child(itemid).child(System.currentTimeMillis()+"."+getExtention(picurl));
//            uploasTask= picRef.putFile(picurl)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot >() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            picRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    finalurl=uri.toString();
//                                    Log.i(mytag,"lluy"+finalkey+" "+finalurl);
//                                    it=new Item();
//                                    it.setImageUrl(finalurl);
//                                    it.setItemId(finalkey);
//                                    Log.i(mytag,"imvss"+it.getImageUrl().toString());
//                                  //  imv.setImageBitmap(getImageBitmapFromUrl(it.getImageUrl()));
//                                    new ItemRepoImpl().update(it,0);
//                                    new DownloadImageAsync(imv,it.getImageUrl()).execute();
//
//                                }
//                            });
//
//                      // String uri=     taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//
//
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception exception) {
//                            // Handle unsuccessful uploads
//                            // ...
//                        }
//                    });
//
//        }
//
//    }

    // click save new item(finalkey ... finalurl);
    // get
    // request.auth != null
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // imv=(ImageView) findViewById(R.id.imv);
        //openFileChooser();


//        FirebaseDatabase.getInstance().getReference("All").removeValue();
//        FirebaseDatabase.getInstance().getReference("Item").removeValue();
      //  FirebaseDatabase.getInstance().getReference("Favourite").removeValue();


        ItemRepoImpl i=new ItemRepoImpl();


//        i.deleteBought("u2","-M504nQjS1JxVtkBdPNn");
//        i.deleteSold("u1","-M504nQjS1JxVtkBdPNn");

     //   i.itemRef.removeValue();
//        Item i1=new Item("-M504nQjS1JxVtkBdPNn","0","u1","u2","i1","it1","10.0","my item1","xy","1","2.1","y");
//        Item i2=new Item("-M504nQpnWNaVlXSwjG6","0","u1","u2","i2","it2","10.0","my item2","xy","1","0","n");
//        Item i3=new Item("-M504nQpnWNaVlXSwjG7","1","u2","none","i3","it3","3.0","my item3","xy","0","4.5","y");
       //i.getItemByItemId("-M4v-sVD8wdK-IUGZOp5");
//        i.saveToAllTable(i1);
//        i.saveToAllTable(i2);
//
//        i.saveToAllTable(i3);
//        i.update(i1,1);
//        i.update(i3,0);
      //  i.getItemByTagId("0");


//        Favourite f1=new Favourite("u1",i1);
//        Favourite f3=new Favourite("u1",i2);
//        Favourite f2=new Favourite("u2","-M504nQjS1JxVtkBdPNn");
        FavouriteRepoImpl fim=new FavouriteRepoImpl();
//        fim.getFavouritesByUserId("u1");
//        fim.save(f1);
//        fim.save(f3);

//        fim.deleteFav(f3);
//        fim.save(f3);

     //   fim.favRef.removeValue();
//        fim.save(f1);
//       fim.save(f3);
//       fim.save(f2);
//       fim.save(f3);
  //    fim.getFavouritesByUserId("u1");
//        Log.i(mytag,"ll");
//        new TableInit().execute();
//        Log.i(mytag,"22");
        Log.i(mytag,"main");
        //  ChatRepoImpl charSevice=new ChatRepoImpl();
        //  charSevice.chatRef.removeValue();
//          Chat c1=new Chat("u1","u2","0","0");
//          Chat c2=new Chat("u1","u3","0","0");
//          Chat c3=new Chat("u2","u1","1","1");
//        Chat c5=new Chat("u5","u1","0","0");
//Chat c4=new Chat("u3","u1","1","1");
//List<Chat> l=new LinkedList<>();
//l.add(c3);
//l.add(c4);

          UserRepoImpl userSevice=new UserRepoImpl();
     //   User user1=new User("test1","123","test1name",0.0,0);
    //    Log.i(mytag,"1111");

      //      Log.i(mytag,"zzzkk");
        //   userSevice.register(user1);

     //  Log.i(mytag,"2222");
//          charSevice.save(c1);
//         charSevice.save(c2);
//       charSevice.saveListChat(l);
//        charSevice.save(c5);
// re=new LinkedList<Chat>();
//
//Log.i(mytag,"mainbefore");
//List<Chat> re=charSevice.getChatByIdTask("u1","u2");
//Log.i(mytag,"mainnn"+re.size());
     //   new MyCallable("u1","u2");

//


//        User user2=new User("u2","12","u2name",0,0);
//        User user3=new User("u3","1","u3name",3.0,1);
//
//        userSevice.register(user2);
//
//        userSevice.register(user3);
//       userSevice.login("u1","4");
        //userSevice.login("u1","123");
      // userSevice.login("u2","123");
     //  userSevice.updateRate("u1",2);

    }

}
