package com.example.backend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;

import DAO.Impl.FavouriteRepoImpl;
import DAO.Impl.UserRepoImpl;
import Model.Favourite;

public class LoginActivity extends AppCompatActivity {
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//                FirebaseDatabase.getInstance().getReference("Item").removeValue();
//        FirebaseDatabase.getInstance().getReference("All").child("Bought").removeValue();
//        FirebaseDatabase.getInstance().getReference("All").child("Sold").removeValue();
//        FirebaseDatabase.getInstance().getReference("All").child("Posted").removeValue();
              //  FirebaseDatabase.getInstance().getReference("All").child("Favourite").removeValue();

        et_email = (EditText)findViewById(R.id.et_email);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_register = (Button)findViewById(R.id.btn_register);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserRepoImpl().login(et_email.getText().toString(),et_password.getText().toString(),LoginActivity.this);

            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
