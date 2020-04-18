package com.example.backend.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.backend.R;

import org.w3c.dom.Text;

import DAO.Impl.DownloadImageAsync;
import Model.Item;
import de.hdodenhof.circleimageview.CircleImageView;

public class ItemDetailActivity extends AppCompatActivity {
    private Button buy;
    private ImageView itemimage;
    private TextView price;
    private ImageView userimage;
    private TextView username;
    private ImageButton favorite;
    private TextView descirption;

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

        //set the components' information by the item object stored in the intent;
        Intent intent = getIntent();
        Item item = (Item) intent.getBundleExtra("clickitem").getParcelable("clickitem");
        boolean history = intent.getBooleanExtra("history", false);

        Glide.with(this).load(item.getImageUrl()).into(itemimage);
        Log.i("mytagdetail",item.getImageUrl());
        price.setText("  $  " + item.getPrice());
        userimage.setImageDrawable(getResources().getDrawable(R.drawable.favorite_item));
        username.setText(item.getSellerId());
        descirption.setText("  " + item.getDescription());

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
            ItemDetailActivity.this.finish();
        }
    }


}
