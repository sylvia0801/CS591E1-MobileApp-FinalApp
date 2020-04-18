package DAO.Impl;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import DAO.PictureRepository;
import Model.Chat;
import Model.Picture;

import static com.example.backend.MainActivity.mytag;

public class PictureRepoImpl implements PictureRepository {
    public static DatabaseReference picRef;

    public PictureRepoImpl() {
        picRef = FirebaseDatabase.getInstance().getReference("Picture");
    }

    @Override
    public List<Picture> getPictureByItemId(String itemId) {
        List<Picture> res = new ArrayList<>();
        Log.i(mytag,"get");
        picRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(mytag,"changed");
                for (DataSnapshot picture : dataSnapshot.getChildren()) {
                    // do something with the individual "issues"
                    HashMap<String, String> map = (HashMap<String, String>) picture.getValue();
                    String id = map.get("itemId");
                    String path = map.get("picturePath");
                    if(id.equals(itemId)) {
                        Log.i(mytag, id + " " + path);
                        Picture pic = new Picture(id, path);
                        res.add(pic);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return res;
    }

    @Override
    public void save(Picture picture) {
        String id=picRef.push().getKey();
        picture.setItemId(id);
        picRef.child(id).setValue(picture);
    }

}
