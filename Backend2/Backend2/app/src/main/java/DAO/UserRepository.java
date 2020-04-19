package DAO;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;

import Model.User;

// user table service
public interface UserRepository {
    // add auth to firebase database and save to user table
    void register( String email, String passWord,Activity context);
    // update user's rating
    void updateRate(String id,double rating);
    void login(String email, String passWord, Activity context);
   void getUserById(String uid);
}
