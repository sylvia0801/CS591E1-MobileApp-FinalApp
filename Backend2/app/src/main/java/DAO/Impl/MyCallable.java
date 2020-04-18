package DAO.Impl;

import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;

import Model.Chat;

import static com.example.backend.MainActivity.mytag;

// no use
public class MyCallable implements Callable<List<Chat>> {
    ChatRepoImpl charSevice1=new ChatRepoImpl();
    String e1="",e2="";
  public  MyCallable(String e1,String e2){
        this.e1=e1;
        this.e2=e2;
    }

    public  List<Chat> call() throws Exception {
      Log.i(mytag,"cccc");
        return   charSevice1.getChatByIdTask(e1,e2);
        //   return "Call me maybe";
    }
}

