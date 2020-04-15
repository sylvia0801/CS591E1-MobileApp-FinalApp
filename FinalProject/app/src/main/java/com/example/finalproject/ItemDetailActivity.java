package com.example.finalproject;

import android.content.ClipData;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDetailActivity extends AppCompatActivity {
    private Button buy;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);

        buy = (Button) findViewById(R.id.buy);
        buy.setOnClickListener(new buyListener());
    }

    private class buyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ItemDetailActivity.this.finish();
        }
    }


}
