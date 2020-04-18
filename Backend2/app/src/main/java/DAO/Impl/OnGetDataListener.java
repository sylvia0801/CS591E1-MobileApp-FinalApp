package DAO.Impl;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import Model.Chat;

public interface OnGetDataListener {

     void onStart();
    List<Chat> onSuccess(DataSnapshot snapshot,String fromUserEmail,String toUserEmail);
    void onFailed(DatabaseError error);


}
