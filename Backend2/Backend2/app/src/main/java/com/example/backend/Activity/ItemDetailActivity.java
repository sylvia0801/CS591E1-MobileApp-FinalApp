package com.example.backend.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.backend.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import DAO.Impl.FavouriteRepoImpl;
import Model.Favourite;
import Model.Item;
import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

// the page show all the item detail info
public class ItemDetailActivity extends AppCompatActivity {
    private Button buy;
    private ImageView itemimage;
    private TextView price;
    private ImageView userimage;
    private TextView username;
    private ImageButton favorite;
    private TextView descirption;
    private ImageButton btn_chat;
    private Item item;
    private FavouriteRepoImpl favoriteService=new FavouriteRepoImpl();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        buy = (Button) findViewById(R.id.Detail_buy);
        itemimage = (ImageView) findViewById(R.id.Detail_Item_Image);
        price = (TextView) findViewById(R.id.Detail_price);
        userimage = (CircleImageView) findViewById(R.id.Detail_User_Image);
        username = (TextView) findViewById(R.id.Detail_User_Name);
        favorite = (ImageButton) findViewById(R.id.Detail_favorite_button);
        descirption = (TextView) findViewById(R.id.Detail_Item_Description);
        btn_chat = (ImageButton)findViewById(R.id.btn_chat);
        favorite.setOnClickListener(new favoriteListener());
        //set the components' information by the item object stored in the intent;
        Intent intent = getIntent();
         item = (Item) intent.getBundleExtra("clickitem").getParcelable("clickitem");
        boolean history = intent.getBooleanExtra("history", false);
        btn_chat.setOnClickListener(new chatListener());

        databaseReference = FirebaseDatabase.getInstance().getReference("User").child(item.getSellerId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageurl().equals("default")){
                    userimage.setImageResource(R.drawable.icon);
                } else{
                    Glide.with(getBaseContext()).load(user.getImageurl()).into(userimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Glide.with(ItemDetailActivity.this).load(item.getImageUrl()).into(itemimage);
        
        price.setText("  $  " + item.getPrice());
        userimage.setImageDrawable(getResources().getDrawable(R.drawable.favorite_item));
        username.setText(item.getSellerName());
        descirption.setText("  " + item.getDescription());

        //determine whether the item is sold
        if(item.getStatus() == "1"){
            System.out.println("Here is status" + item.getStatus());
            buy.setText("Sold");
            buy.setEnabled(false);
        }

        if(history){
            buy.setVisibility(View.GONE);
        }
        else{
            buy.setOnClickListener(new buyListener());
        }

    }

    private class buyListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            //To do go to the payment page
            Intent intent = new Intent(ItemDetailActivity.this, PayActivity.class);
            intent.putExtra("payitem", item);

            startActivity(intent);
        }
    }
    // add item to my fav list
    private class favoriteListener implements  View.OnClickListener{
        @Override
        public void onClick(View v) {
            Favourite to_add=new Favourite(auth.getCurrentUser().getDisplayName(),item);
            favoriteService.save(to_add);
            Toast.makeText(getApplicationContext(),"Added to My Favourite!",Toast.LENGTH_SHORT).show();

        }
    }

    private class chatListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            FirebaseUser cuser = FirebaseAuth.getInstance().getCurrentUser();
            if (cuser.getUid().equals(item.getSellerId())){
                Toast.makeText(ItemDetailActivity.this, "You can't talk to yourself.", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(ItemDetailActivity.this, MessageActivity.class);
                intent.putExtra("userid", item.getSellerId());
                startActivity(intent);
            }
        }
    }


}
