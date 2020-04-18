package DAO.Impl;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import DAO.ImageAlbumRepository;
import Model.Item;

import static android.app.Activity.RESULT_OK;

public class ImageAlbumRepoImpl implements ImageAlbumRepository {
    public static String mytag="mytag";
    private ImageView imv;
    public   String finalurl;
    public String finalkey;
    Context context;
    Item it;
    Uri imageuri;
    public ImageAlbumRepoImpl(Activity context){
        this.context=context;
    }
    public String getExtention(Uri uri){
        ContentResolver cs=context.getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(cs.getType(uri));

    }


//
//    private void openFileChooser(){
//        Log.i(mytag,"open firechooser");
//        Intent i=new Intent();
//        i.setType("image/*");
//        i.setAction(Intent.ACTION_GET_CONTENT);
//        context.startActivityForResult(i,999);
//
//
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==999&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
//            imageuri=data.getData();
//            Log.i(mytag,"get phone"+imageuri.toString());
//            imv.setImageURI(imageuri);
//            // Log.i(mytag,"setimv");
//            saveItemToDatabase(imageuri);
//            //    uploadFile();  // if cancel post new then remove pics itemidxx;
//            Log.i(mytag,"upload to data");
//
//        }
//    }


    public void saveItemToDatabase(Uri url){
        Item nitem=new Item();
        ItemRepoImpl i=new ItemRepoImpl();
        nitem=i.saveToAllTable(nitem);
        String k=nitem.getItemId();
        finalkey=k;
        uploadFile(finalkey,url);

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
                                    it=new Item();
                                    it.setImageUrl(finalurl);
                                    it.setItemId(finalkey);
                                    Log.i(mytag,"imvss"+it.getImageUrl().toString());
                                    //  imv.setImageBitmap(getImageBitmapFromUrl(it.getImageUrl()));
                                    new ItemRepoImpl().update(it,0);
                                    new DownloadImageAsync(imv,it.getImageUrl()).execute();

                                }
                            });

                            // String uri=     taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();



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

}
