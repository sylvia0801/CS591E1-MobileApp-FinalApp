package com.example.backend.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.backend.Adapter.ItemAdapter;
import com.example.backend.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Model.Item;

public class ItemActivity extends AppCompatActivity {

    List<Item> itemList = new ArrayList<>();

    private Intent intent;
    private ItemAdapter adapter;
    private DatabaseReference itemRef= FirebaseDatabase.getInstance().getReference("Item");
    private  List<Item> res;
    private String type;
    String mytag="mytag";
  //  private FirebaseAuth auth=FirebaseAuth.getInstance();

    private void switchType(String type){
        res=new LinkedList<>();
        Log.i(MainActivity.mytag,"inside ");
      //  List<Item> res = new ArrayList<>();
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(MainActivity.mytag,"dddd"+dataSnapshot.getChildrenCount());
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
                    if(tag.equals(type)) {
                        Item i=new Item(id,tag,sellerId,buyerId,sellerName,buyerName,title,productName,price,description,url,address,status,postRating,rated);
                        res.add(i);
                    }
                }
                System.out.println("tag"+res.size());
                adapter = new ItemAdapter(res, ItemActivity.this);
                ListView listveiw = (ListView) findViewById(R.id.lv_items);
                listveiw.setAdapter(adapter);
                listveiw.setOnItemClickListener(new ItemListener());
                // to do frontend  all  items of one tag
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        intent = getIntent();
        String type= intent.getStringExtra("Type").toString();
//        titletext =type+" Items";
//        title = (TextView) findViewById(R.id.History_Title);
//        title.setText("My" + " " + titletext);
        // to do get displayname
        switchType(type);


    }

    private class ItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Item clickitem = (Item) adapter.getItem(position);
            Intent intent = new Intent(ItemActivity.this, ItemDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("clickitem", (Parcelable) clickitem);
            intent.putExtra("clickitem", bundle);
            ItemActivity.this.startActivity(intent);
        }
    }

}
