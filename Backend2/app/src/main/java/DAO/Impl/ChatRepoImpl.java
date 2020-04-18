package DAO.Impl;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.logging.Handler;
import java.util.logging.LogRecord;
import DAO.ChatRepository;
import Model.Chat;
import Model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.os.Handler;
import static androidx.core.content.ContextCompat.startActivity;
import static com.example.backend.MainActivity.mytag;

/*
Chat table tree:
     -self_generaated_key(make sure dialog  unique)
                     -fromUserEmail
                     -toUserEmail
                     -index(time)(tobesort)
                     -dialog

 */
public class ChatRepoImpl implements ChatRepository {
    public static DatabaseReference chatRef;
    public ChatRepoImpl(){
        chatRef =FirebaseDatabase.getInstance().getReference("Chat");

    }
    // save chat to Chat table
    @Override
    public void save(Chat chat) {
        String id=chatRef.push().getKey();
        chatRef.child(id).setValue(chat);
    }
    public void saveListChat( List<Chat> l){
        // don't add before ones into chat list l
        for(Chat c:l){
            save(c);
        }
    }
    /*

    public void mReadDataOnce(String child,final OnGetDataListener lis){
        lis.onStart();
        FirebaseDatabase.getInstance().getReference().child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
           lis.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
lis.onFailed(databaseError);
            }

        });
    }

    public void mgetInfoInServer(String child){
      DatabaseReference d=  FirebaseDatabase.getInstance().getReference().child(child);
         mReadDataOnce(child, new OnGetDataListener() {
             @Override
             public void onStart() {
                 Log.i(mytag,"onstart");
             }

             @Override

             public List<Chat> onSuccess(DataSnapshot snapshot,String fromUserEmail,String toUserEmail) {
                 List<Chat> re=new LinkedList<Chat>();
                 for (DataSnapshot postsanpshot : snapshot.getChildren()) {
                     HashMap<String, String> h = (HashMap<String, String>) postsanpshot.getValue();
                     String fe=h.get("fromUserEmail");
                     String te=h.get("toUserEmail");
                     // if they  are the person talk to each other , add to re
                     if((fe.equals(fromUserEmail)&&te.equals(toUserEmail))||(te.equals(fromUserEmail)&&fe.equals(toUserEmail)))
                     {
                         Chat t=getChatByIndexandSnap(postsanpshot);
                         Log.i(mytag,"add to chat of two: "+t);
                         re.add(t);

                     }

                 }
                 return re;
             }

             @Override
             public void onFailed(DatabaseError error) {

             }
         });
    }
    */
    public List<Chat> getChatByIdTask(String fromUserEmail, String toUserEmail) {
        List<Chat> re=new LinkedList<>();

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot postsanpshot : snapshot.getChildren()) {
                        HashMap<String, String> h = (HashMap<String, String>) postsanpshot.getValue();
                        String fe=h.get("fromUserEmail");
                        String te=h.get("toUserEmail");
                        // if they  are the person talk to each other , add to re
                        if((fe.equals(fromUserEmail)&&te.equals(toUserEmail))||(te.equals(fromUserEmail)&&fe.equals(toUserEmail)))
                        {
                            Chat t=getChatByIndexandSnap(postsanpshot);
                            Log.i(mytag,"add to chat of two: "+t);
                            re.add(t);

                        }

                    }

                    chatRef.removeEventListener(this);
                // get the full list
                // to do frontend show this info
                // if empty no history


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        /*
         Handler handler = new Handler();
        int delay = 1000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                if(!re.isEmpty())//checking if the data is loaded or not
                {
                    Log.i(mytag,"pppp111");
                    return;
                   // return re;
                    Intent intent = new Intent(getApplicationContext(), StoryActivity.class);
              //      Bundle extras = new Bundle();
                  //  extras.putInt("PROJECT_ID",0);
                  //  extras.putInt("STORY_POSITION",
                         //   re.get(position).getPosition());
//                    extras.putInt("STORY_ID", model.getPosition_on_tree());
//                    extras.putInt("CHAPTER_SIZE", model.getChapter_size());
                   // intent.putExtras(extras);

    //                finish();
                }

                else
                    handler.postDelayed(this, delay);
            }
        }, delay);
*/

// can be void
        return re;
    }


    // get datetime
//        Date date = new Date();
////        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
////        Log.i(mytag,sdf.format(date));

    // to be changed  the frontend give get time.
    public Chat getChatByIndexandSnap(DataSnapshot postSnapshot){
        Chat chat=new Chat( postSnapshot.child("fromUserEmail").getValue().toString() ,postSnapshot.child("toUserEmail").getValue().toString(),postSnapshot.child("index").getValue().toString(),postSnapshot.child("dialog").getValue().toString());
        return chat;
    }
}
