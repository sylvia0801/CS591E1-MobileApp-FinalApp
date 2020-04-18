package DAO;

import android.app.Activity;

import com.example.backend.Adapter.HistoryItemAdapter;

import java.util.List;

import Model.Favourite;
import Model.Item;

public interface ItemRepository {
    void getItemByItemId(String itemId); // show item details page
   // void getItemByTagId(String tagId, HistoryItemAdapter adapter);  // get all tag posts

    // list of posts of a tag
    void getItemByTagId(String tagId);

    Item saveToAllTable(Item item);
    //void save(Item item);
    //void deleteByItemId(String itemId);
    void update(Item item,int type);// when type==1, the buyer has set so, need also update the bought, sold table
    void deleteBought(String userid,String itemid);// current user
    void deleteSold(String userid,String itemid);

}
