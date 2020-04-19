package com.example.backend.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.backend.Fragment.buttom_fragment;
import com.example.backend.R;

public class ChatActivity extends AppCompatActivity implements buttom_fragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
