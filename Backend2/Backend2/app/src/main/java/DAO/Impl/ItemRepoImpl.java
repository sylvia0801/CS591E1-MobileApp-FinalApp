package DAO.Impl;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.backend.LoginActivity;
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
                        -sellerName
                        -buyerName
                        -title
                        -productName
                        -price
                        -description
                        -imageUrl
                        -address
                        -status  //0:on sell, 1: wait for respond and no more request for buying, 2:sold
                        -postRating
                        -rated("y"/"n")
                        -latitude
                        -longtitude



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
    private FirebaseAuth auth;
    public ItemRepoImpl(){
         userAllRef= FirebaseDatabase.getInstance().getReference("All");
        itemRef= FirebaseDatabase.getInstance().getReference("Item");
        auth=FirebaseAuth.getInstance();


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
                    String sellerId=map.get("sellerId");
                    String buyerId=map.get("buyerId");
                    // to do add jingweifdu
                    String sellerName=map.get("sellerName");
                    String buyerName=map.get("buyerName");

                    String title=map.get("title");
                    String productName=map.get("productName");
                    String price=map.get("price");
                    String description=map.get("description");
                    String address=map.get("address");
                    String status=map.get("status");
                    String url=map.get("imageUrl");
                    String postRating=map.get("postRating");
                    String rated=map.get("rated");//"y" or "n"
                    Item i=new Item(id,tagId,sellerId,buyerId,sellerName,buyerName,title,productName,price,description,url,address,status,postRating,rated);
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
                    String sellerName=map.get("sellerName");
                    String buyerName=map.get("buyerName");
                    String price=map.get("price");
                    String description=map.get("description");
                    String url=map.get("imageUrl");
                    String address=map.get("address");
                    String status=map.get("status");
                    String postRating=map.get("postRating");
                    String rated=map.get("rated");//"y" or "n"
                    if(tag.equals(tagId)) {

                        Item i=new Item(id,tagId,sellerId,buyerId,sellerName,buyerName,title,productName,price,description,url,address,status,postRating,rated);
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
    item.setItemId(id);
    //auth=FirebaseAuth.getInstance();
    String funame=auth.getCurrentUser().getDisplayName();

    String fuid=auth.getCurrentUser().getUid();
    item.setSellerId(fuid );//auth.getCurrentUser().getUid()
    item.setSellerName(funame);
    Log.i(mytag,"display kkname"+funame+fuid);

    itemRef.child(id).setValue(item);
   userAllRef.child("Posted").child(funame).child(id).setValue(item);
   return item;
}
// fu is current user displayname
    // delete from item table and posted table
public void deleteItemByItemId(String itemid){
    String fromUsername=auth.getCurrentUser().getDisplayName();
    itemRef.child(itemid).removeValue();
    userAllRef.child("Posted").child(fromUsername).child(itemid).removeValue();

}
    @Override

    public void  deleteFromAllTableByUsername(String itemid, String tabletype){
  // FirebaseAuth auth=FirebaseAuth.getInstance();
    String name=auth.getCurrentUser().getDisplayName();
        Log.i(mytag,"allllll"+tabletype+itemid+name);
    userAllRef.child(tabletype).child(name).child(itemid).removeValue();

    }

    // updated item value by item id
    // type 0: update all value, 1: needs to be add to bought
    // params: updated buyer item
    @Override

public void update(Item item,int type){
    Log.i(mytag,"update"+item);
        String k=item.getItemId();
        String fu=item.getSellerName();
        String tu=item.getBuyerName();
    itemRef.child(item.getItemId()).setValue(item);
    userAllRef.child("Posted").child(fu).child(k).setValue(item);
    if(type==1){
        Log.i(mytag,"bb"+tu);
        userAllRef.child("Sold").child(fu).child(k).setValue(item);
        userAllRef.child("Posted").child(fu).child(k).removeValue();
        userAllRef.child("Bought").child(tu).child(k).setValue(item);
    }
}

}
