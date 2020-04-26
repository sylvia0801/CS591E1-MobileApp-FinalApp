package DAO.Impl;
import androidx.annotation.NonNull;
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
import Model.Item;


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



    -All Table( key is user):
             -Favourite Table
                      -username
                          -item
             -Posted Table
                     -username(sellername)
                          -item
             -Bought Table
                     -username(buyername)
                          -item
             -Sold Table
                     -username(sellerame)
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
//    @Override
//    public void getItemByItemId(String itemId) {
//       List<Item> re = new ArrayList<>();
//        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot item : dataSnapshot.getChildren()) {
//                    // do something with the individual "issues"
//                    HashMap<String, String> map = (HashMap<String, String>) item.getValue();
//                    String id = map.get("itemId");
//                    String tagId = map.get("tagId");
//                    String sellerId=map.get("sellerId");
//                    String buyerId=map.get("buyerId");
//                    String sellerName=map.get("sellerName");
//                    String buyerName=map.get("buyerName");
//                    String title=map.get("title");
//                    String productName=map.get("productName");
//                    String price=map.get("price");
//                    String description=map.get("description");
//                    String address=map.get("address");
//                    String status=map.get("status");
//                    String url=map.get("imageUrl");
//                    String postRating=map.get("postRating");
//                    String rated=map.get("rated");//"y" or "n"
//                    Item i=new Item(id,tagId,sellerId,buyerId,sellerName,buyerName,title,productName,price,description,url,address,status,postRating,rated);
//                    re.add(i);
//                }
//              if(re.size()>0){
//                  Item item=re.get(0);
//                  // to do item to do somthing
//              }else {
//                  // no such item
//              }
//
//
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

// add to item table and my Posted table
public Item saveToAllTable (Item item){
    String id = itemRef.push().getKey();
    item.setItemId(id);
    itemRef.child(id).setValue(item);
   userAllRef.child("Posted").child(item.getSellerName()).child(id).setValue(item);
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
// delete one record in user history list
    public void  deleteFromAllTableByUsername(String itemid, String tabletype){
    String name=auth.getCurrentUser().getDisplayName();
    userAllRef.child(tabletype).child(name).child(itemid).removeValue();

    }

    // updated item value by item id
    // type 0: update all value, 1: needs to be add to bought
    // params: updated buyer item
    @Override

public void update(Item item,int type){
        String k=item.getItemId();
        String fu=item.getSellerName();
        String tu=item.getBuyerName();
    itemRef.child(item.getItemId()).setValue(item);
    userAllRef.child("Posted").child(fu).child(k).setValue(item);

    DatabaseReference ref=userAllRef.child("Favourite");
    ref.addListenerForSingleValueEvent(new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              for(DataSnapshot uu:dataSnapshot.getChildren()){
                 if( uu.child(item.getItemId()).exists()){
                     ref.child(uu.getKey()).child(item.getItemId()).setValue(item);

                 }
              }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    if(type==1){
        userAllRef.child("Sold").child(fu).child(k).setValue(item);
        userAllRef.child("Posted").child(fu).child(k).removeValue();
        userAllRef.child("Bought").child(tu).child(k).setValue(item);
    }
}

}
