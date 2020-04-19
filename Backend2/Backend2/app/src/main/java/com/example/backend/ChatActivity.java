package com.example.backend;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;

import com.example.backend.Fragment.buttom_fragment;

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
