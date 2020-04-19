package com.example.backend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.backend.Activity.ItemDetailActivity;

import java.util.ArrayList;
import java.util.List;

import Model.Item;

public class ItemActivity extends AppCompatActivity {

    List<Item> itemList = new ArrayList<>();

    private Intent intent;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        /* To Do:
            1.get the itemImage uri array
            2.get the itemTitle array
            3.get the itemPrice array
         */

        intent = getIntent();
        List<Item> items = new ArrayList<>();

        adapter = new ItemAdapter(items, this.getBaseContext());
        ListView listview = (ListView) findViewById(R.id.lv_items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new ItemListener());

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
