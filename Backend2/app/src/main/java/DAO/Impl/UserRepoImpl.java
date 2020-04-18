package DAO.Impl;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.backend.LoginActivity;
import com.example.backend.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.concurrent.Executor;

import DAO.UserRepository;
import Model.User;
import static com.example.backend.MainActivity.mytag;

/*
User table:
        - userId(as key to get user unique)
                -userId
                -email
                -userName
                -averageRate(double)
                -ratePeopleCount(int)

 */

// user table service impl
public class UserRepoImpl implements UserRepository {
   public static DatabaseReference userRef; //  refer to table of the user
    public static FirebaseAuth auth;
    public UserRepoImpl(){
        userRef =FirebaseDatabase.getInstance().getReference("User");
      auth=FirebaseAuth.getInstance();
    }

    private String getUsernameFromEmail(String email) {
            return email.split("@")[0];
    }


    //User user
    public  void register(String email,String password,Activity context){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( context, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i(mytag,task.toString());
                if(task.isSuccessful()){
                    Toast.makeText(context, "Successfully registered", Toast.LENGTH_LONG).show();
                    FirebaseUser cur=auth.getCurrentUser();
                    User user=new User(cur.getUid(),cur.getEmail(),getUsernameFromEmail(cur.getEmail()));
                    save(user);
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                    context.finish();

                }else {
                    Log.w(mytag, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(context, "auth Failed"+task.getException(), Toast.LENGTH_LONG).show();
                }
            }

        }) ;

    }
    /*
    public int register(User user,Activity context){
        DatabaseReference emailRef=userRef.child(user.getEmail());
         int[] re=new int[]{1};
        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue()!=null) {
                   // Log.i(mytag,"email already exsit");
                    Toast.makeText(context, "email already exsit!", Toast.LENGTH_SHORT).show();
                      }

                else{
                    authUserTodatabase(user,context);
                    // frontend check password same then pass; so directly save new user to User Table
                    // password wrong frontend show message
                     save(user);
                  //  to do frontend new intent   main page;
                    Toast.makeText(context, "You have sucessfully registered.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
// can delete
        return re[0];

    }

     */

    @Override
    // update the rating of the user when his posts gets one new rating
    public void updateRate(String id, double rating){
        DatabaseReference idRef=userRef.child(id);  // refer to the user
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue()!=null) {
                        User updateUser=getUserBysnap(id,snapshot);
                        int ppl=updateUser.getRatePeopleCount();
                        double aveRate=updateUser.getAverageRate();
                        double nrate=(double)(rating+ppl*aveRate)/(ppl+1);
                        updateUser.setRatePeopleCount(ppl+1);
                        updateUser.setAverageRate(nrate);
                        // key so no duplicates
                    // update
                        save(updateUser);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    // current user
    public void login(String email,String passWord,Activity context){
       auth.signInWithEmailAndPassword(email, passWord).addOnCompleteListener( context, new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()) {
                   Toast.makeText(context, "Successfully Logged In", Toast.LENGTH_LONG).show();
                   Intent intent = new Intent(context, MainActivity.class);
                   // TODO MainPageActivity
                   context.startActivity(intent);
                   context.finish();

               }else {
                   Toast.makeText(context, "Login Failed"+task.getException(), Toast.LENGTH_LONG).show();
               }
           }
        });
/*
        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()>0){
                        if(snapshot.child("passWord").getValue().toString().equals(passWord)){
                            // password match
                            // to do frontend new main page
                            // get current user  needed
                         re[0]=getUserByEmail(email,snapshot);
                            Toast.makeText(context, "You have sucessfully logged in.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            // TODO MainPageActivity
                            context.startActivity(intent);
                         //   Log.i(mytag,"login success");
                        }
                        else{
                            // to do frontend
                            Toast.makeText(context, "password wrong", Toast.LENGTH_SHORT).show();
                            Log.i(mytag,"password wrong");
                        }

                }else{
                    // to do frontend show massage
                    Log.i(mytag,"no account");
                }
           }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */


    }
    // current user
    public User save(User user){
       // Log.i(mytag,userRef.toString()+"sss"+user);
        userRef.child(user.getUserId()).setValue(user);
          return user;
    }
    public void getUserById(String id){
        userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                String uid = map.get("userId");
                String email=map.get("email");
                String username=map.get("userName");
                String averageRate=map.get("averageRate");
                String rpc=map.get("ratePeopleCount");
                 User re=new User(uid,email,username,Double.parseDouble(averageRate),Integer.parseInt(rpc));
                 //  fornt end use re to do sth
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private User getUserBysnap(String id,DataSnapshot postSnapshot){
      // userRef.child(id).add

        User user=new User(id,postSnapshot.child("email").getValue().toString(),postSnapshot.child("userName").getValue().toString(),Double.parseDouble(postSnapshot.child("averageRate").getValue().toString()),Integer.parseInt(postSnapshot.child("ratePeopleCount").getValue().toString()));
        return user;
    }
}
