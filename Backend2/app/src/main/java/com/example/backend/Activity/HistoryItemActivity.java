package com.example.backend.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.backend.R;
import com.example.backend.Adapter.HistoryItemAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import DAO.Impl.ItemRepoImpl;
import Model.Item;


public class HistoryItemActivity extends AppCompatActivity {
    private TextView title;
    private Intent intent;
    private HistoryItemAdapter adapter;
    public DatabaseReference itemRef;
    private DatabaseReference allRef=FirebaseDatabase.getInstance().getReference("All");
    private final String Posted="Posted";
    private final String Bought="Bought";
    private final String Sold="Sold";
    private final String Favourite="Favourite";
    private final List<Item> res=new LinkedList<>();
    private String titletext;
    private ItemRepoImpl itemService=new ItemRepoImpl();
String mytag="mytag";

private void switchType(String type,String userid){
      allRef.child(type).child(userid).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Log.i(mytag,type+" "+userid+dataSnapshot.getChildrenCount());
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

                      Item i=new Item(id,tag,sellerId,buyerId,title,productName,price,description,url,address,status,postRating,rated);
                      res.add(i);
                      Log.i(mytag,"mytag"+i.toString());

              }

              System.out.println(type+userid+res.size());
              adapter = new HistoryItemAdapter(res,intent.getBooleanExtra("ShowEdit", true), titletext, HistoryItemActivity.this);
              //to do
//             itemService.getItemByTagId("0");
             // switchType(type,"u9");
              ListView listveiw = (ListView) findViewById(R.id.History_Item_List);
              listveiw.setAdapter(adapter);
              listveiw.setOnItemClickListener(new HistoryItemListener());

              //   Log.i(activity)
              //  Intent intent = new Intent(activity,  (new HistoryItemActivity(res)).getClass());
              // Intent intent = new Intent(activity,HistoryItemActivity.class );
//                intent.putExtra("Title", "Post Items");
//                intent.putExtra("ShowEdit", true);
//                activity.startActivity(intent);



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
        setContentView(R.layout.item_lists);

        intent = getIntent();
        String type= intent.getStringExtra("Title").toString();

        titletext =type+" Items";
        title = (TextView) findViewById(R.id.History_Title);
        title.setText("My" + " " + titletext);
        switchType(type,"u9");

    }

    private class histroyListener implements  AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        }
    }

    private class HistoryItemListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Item clickitem = (Item) adapter.getItem(position);
            Intent intent = new Intent(HistoryItemActivity.this, ItemDetailActivity.class);

            Bundle bundle = new Bundle();
            bundle.putParcelable("clickitem", clickitem);

            intent.putExtra("clickitem", bundle);
            intent.putExtra("history", true);

            HistoryItemActivity.this.startActivity(intent);
        }
    }

}
