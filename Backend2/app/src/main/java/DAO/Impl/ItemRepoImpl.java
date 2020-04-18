package DAO.Impl;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.NonNull;

import com.example.backend.Activity.HistoryItemActivity;
import com.example.backend.Activity.ProfileActivity;
import com.example.backend.Adapter.HistoryItemAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import DAO.ItemRepository;
import Model.Favourite;
import Model.Item;
import Model.Picture;
import static com.example.backend.MainActivity.mytag;


/*
   Item table:
            -itemId (self -generated unique)
                        -itemId
                        -tagId
                        -sellerId
                        -buyerId
                        -title
                        -productName
                        -price
                        -description
                        -imageUrl
                        -address
                        -status  //0:on sell, 1: wait for respond and no more request for buying, 2:sold
                        -postRating
                        -rated("y"/"n")



    -All Table( key is user):
             -Favourite Table
                      -userId
                          -item

             -Posted Table
                     -userId(sellerId)
                          -item
             -Bought Table
                     -userId(buyerId)
                          -item
             -Sold Table
                     -userId(seller Id)
                         -item


 */


public class ItemRepoImpl implements ItemRepository {
    public DatabaseReference itemRef;
    public DatabaseReference userAllRef;
    public ItemRepoImpl(){
         userAllRef= FirebaseDatabase.getInstance().getReference("All");
        itemRef= FirebaseDatabase.getInstance().getReference("Item");

    }
    @Override
    public void getItemByItemId(String itemId) {
       List<Item> re = new ArrayList<>();
        Log.i(mytag,"get");
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(mytag,"changed");
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    // do something with the individual "issues"
                    HashMap<String, String> map = (HashMap<String, String>) item.getValue();
                    String id = map.get("itemId");
                    String tagId = map.get("tagId");
                    String sellerEmail=map.get("sellerEmail");
                    String buyerEmail=map.get("buyerEmail");
                    String title=map.get("title");
                    String productName=map.get("productName");
                    String price=map.get("price");
                    String description=map.get("description");
                    String address=map.get("address");
                    String status=map.get("status");
                    String url=map.get("imageUrl");
                    String postRating=map.get("postRating");
                    String rated=map.get("rated");//"y" or "n"
                    Item i=new Item(id,tagId,sellerEmail,buyerEmail,title,productName,price,description,url,address,status,postRating,rated);
                    re.add(i);
                }
              if(re.size()>0){
                  Item item=re.get(0);
                  // to do item to do somthing
              }else {
                  // no such item
              }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       // return res;
    }

    @Override
    // list of posts of a tag
    public void getItemByTagId(String tagId) {

        Log.i(mytag,"inside");
        List<Item> res = new ArrayList<>();
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(mytag,"dddd"+dataSnapshot.getChildrenCount());
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    HashMap<String, String> map = (HashMap<String, String>) item.getValue();
                    String id = map.get("itemId");
                    String tag = map.get("tagId");
                    String sellerId=map.get("sellerId");
                    String buyerId=map.get("buyerId");
                    String title=map.get("title");
                    String productName=map.get("productName");
                    String price=map.get("price");
                    String description=map.get("description");
                    String url=map.get("imageUrl");
                    String address=map.get("address");
                    String status=map.get("status");
                    String postRating=map.get("postRating");
                    String rated=map.get("rated");//"y" or "n"
                    if(tag.equals(tagId)) {

                        Item i=new Item(id,tagId,sellerId,buyerId,title,productName,price,description,url,address,status,postRating,rated);
                        res.add(i);
                        Log.i(mytag,"bytag"+i.toString());
                    }
                }

                System.out.println("Post"+res.size());




                // to do frontend  all  items of one tag
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

      //  return res;
    }
/*
    @Override

    public List<Item> getItems() {
        List<Item> res = new ArrayList<>();

        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    // do something with the individual "issues"
                    HashMap<String, String> map = (HashMap<String, String>) item.getValue();
                    String id = map.get("itemId");
                    String tagId = map.get("tagId");
                    String sellerEmail=map.get("sellerEmail");
                    String buyerEmail=map.get("buyerEmail");
                    String title=map.get("title");
                    String productName=map.get("productName");
                    String price=map.get("price");
                    String description=map.get("description");
                    String address=map.get("address");
                    String status=map.get("status");
                    String postRating=map.get("postRating");
                    String rated=map.get("rated");//"y" or "n"

                    Item i=new Item(id,tagId,sellerEmail,buyerEmail,title,productName,price,description,address,status,postRating,rated);
                    Log.i(mytag,"get all item,add"+i.toString());
                    res.add(i);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return res;
    }

 */

// add to item table then add to posted table
public Item saveToAllTable (Item item){
    String id = itemRef.push().getKey();
    Log.i(mytag, "save"+id);
    item.setItemId(id);
    item.setSellerId("u9" );//auth.getCurrentUser().getUid()
    itemRef.child(id).setValue(item);
    String fu=item.getSellerId();
   userAllRef.child("Posted").child(fu).child(id).setValue(item);
   return item;
}
    @Override
   public void deleteBought(String userid,String itemid){ // current user
       userAllRef.child("Bought").child(userid).child(itemid).removeValue();
    }
    @Override
   public void deleteSold(String userid,String itemid){
       userAllRef.child("Sold").child(userid).child(itemid).removeValue();
    }


// public void deleteItematAllTable(Item item){
//    String k=item.getItemId();
//    userAllRef.child("Posted").child(item.getSellerId()).child(k).removeValue();
//    userAllRef.child("Bought").child(item.getBuyerId()).child(k).removeValue();
//    userAllRef.child("Fav").child(it)
//
// }


    // updated item value by item id
    // type 0: update all value, 1: needs to be add to bought
    @Override
public void update(Item item,int type){
    Log.i(mytag,"updaupdatiiisss"+item);
        String k=item.getItemId();
        String fu=item.getSellerId();
        String tu=item.getBuyerId();
    itemRef.child(item.getItemId()).setValue(item);
    userAllRef.child("Posted").child(fu).child(k).setValue(item);

    if(type==1){
        Log.i(mytag,"bb"+tu);
        userAllRef.child("Sold").child(fu).child(k).setValue(item);
        userAllRef.child("Posted").child(fu).child(k).removeValue();
        userAllRef.child("Bought").child(tu).child(k).setValue(item);
    }
}

//    @Override
//    public void save(Item item) {
//
//        String id = itemRef.push().getKey();
//        Log.i(mytag, "save"+id);
//        item.setItemId(id);
//        itemRef.child(id).setValue(item);
//
//    }

//    @Override
//    public void deleteByItemId(String itemId) {
//        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Item").child(itemId);
//        Log.i(mytag, "remove"+itemId);
//        dR.removeValue();
//    }


}
