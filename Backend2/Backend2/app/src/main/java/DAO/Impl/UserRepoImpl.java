package DAO.Impl;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.backend.Activity.LoginActivity;
import com.example.backend.Activity.MainPageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import DAO.UserRepository;
import Model.User;
import static com.example.backend.Activity.MainActivity.mytag;

/*
User table:
        - userId(as key to get user unique)
                -userId
                -email
                -userName
                -averageRate(double)
                -ratePeopleCount(int)
                -imageurl
                -status

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
                    String username=getUsernameFromEmail(cur.getEmail());

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            //     .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                            .build();

                    cur.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(mytag, "User profile updated.new username"+cur.getDisplayName());
                            }
                        }
                    });

                    User user=new User(cur.getUid(),cur.getEmail(),username);
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
                   Intent intent = new Intent(context, MainPageActivity.class);
                   // TODO MainPageActivity
                   context.startActivity(intent);
                   context.finish();

               }else {
                   Toast.makeText(context, "Login Failed"+task.getException(), Toast.LENGTH_LONG).show();
               }
           }
        });

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
                String imageurl=map.get("imageurl");
                String status=map.get("status");
                String averageRate=map.get("averageRate");
                String rpc=map.get("ratePeopleCount");
                 User re=new User(uid,email,username,Double.parseDouble(averageRate),Integer.parseInt(rpc),imageurl,status);
                 //  fornt end use re to do sth
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private User getUserBysnap(String id,DataSnapshot postSnapshot){
      // userRef.child(id).add
        User user=new User(id,postSnapshot.child("email").getValue().toString(),postSnapshot.child("userName").getValue().toString(),Double.parseDouble(postSnapshot.child("averageRate").getValue().toString()),Integer.parseInt(postSnapshot.child("ratePeopleCount").getValue().toString()),postSnapshot.child("imageurl").getValue().toString(),postSnapshot.child("status").getValue().toString());
        return user;
    }
}
