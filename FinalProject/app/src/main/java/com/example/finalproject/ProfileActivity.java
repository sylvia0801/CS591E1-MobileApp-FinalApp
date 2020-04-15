package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements buttom_fragment.OnFragmentInteractionListener {

    private final static String[] ItemsArray = {"Posted Items", "Sold Items", "Bought Items", "Liked Items"};
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        HistoryAdapter adapter = new HistoryAdapter(this.getBaseContext());
        ListView listveiw = (ListView) findViewById(R.id.Item_List);
        listveiw.setAdapter(adapter);

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
