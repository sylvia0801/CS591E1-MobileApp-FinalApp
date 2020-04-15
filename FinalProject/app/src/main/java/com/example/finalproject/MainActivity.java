package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private Button profile;
    private Button item_detail;
    private RequestQueue mQueue;
    private Button ParseJson;
    private Double Total = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile = (Button) findViewById(R.id.CheckProfile);
        item_detail = (Button) findViewById(R.id.CheckDetail);
        ParseJson = (Button) findViewById(R.id.ParseJson);

        mQueue = Volley.newRequestQueue(this);

        profile.setOnClickListener(new profileListen());
        ParseJson.setOnClickListener(new parseListen());
        item_detail.setOnClickListener(new detailListen());

    }

    private class profileListen implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }

    private class detailListen implements  View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }

    public class parseListen implements  View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, RecomPriceActivity.class);
            MainActivity.this.startActivity(intent);
        }
    }

}
