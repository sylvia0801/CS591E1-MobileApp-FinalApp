package com.example.backend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class MainPageActivity extends AppCompatActivity {

    private ListView lv_categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        lv_categoryList = (ListView)findViewById(R.id.lv_categoryList);
        final String[] category = {"Clothes", "Books", "Electronics", "Furnitures"};

        CustomAdapter adapter = new CustomAdapter(this.getBaseContext());
        lv_categoryList.setAdapter(adapter);
/*
        lv_categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: {
                }
            }
        });
 */
    }
}
