package com.example.backend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.backend.Fragment.buttom_fragment;
import com.example.backend.R;
import com.example.backend.Tag;
import com.example.backend.TagAdapter;
import com.example.backend.Fragment.buttom_fragment;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements buttom_fragment.OnFragmentInteractionListener {

    private List<Tag> tagList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        final TagAdapter adapter = new TagAdapter(MainPageActivity.this);
        ListView listView = (ListView)findViewById(R.id.lv_tags);
        listView.setAdapter(adapter);

        Log.d("MyTag", "adapter set");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = (String) adapter.getItem(i);
                Intent intent = new Intent(MainPageActivity.this, com.example.backend.ItemActivity.class);
                switch (s) {
                    case "Clothes":
                        intent.putExtra("Title", "Clothes");
                        intent.putExtra("Type", "0");
                        break;

                    case "Books":
                        intent.putExtra("Title", "Books");
                        intent.putExtra("Type", "1");
                        break;

                    case "Electronics":
                        intent.putExtra("Title", "Electronics");
                        intent.putExtra("Type", "2");
                        break;

                    case "Furnitures":
                        intent.putExtra("Title", "Furnitures");
                        intent.putExtra("Type", "3");
                        break;

                }
                startActivity(intent);

            }
        });



    }


    public void onFragmentInteraction(Uri uri) {

    }
}