package DAO;

import android.app.Activity;

import com.example.backend.Adapter.HistoryItemAdapter;

import java.util.List;

import Model.Favourite;
import Model.Item;

public interface ItemRepository {
    void getItemByItemId(String itemId); // show item details page
    // list of posts of a tag
    void getItemByTagId(String tagId);
    Item saveToAllTable(Item item);
    void  deleteFromAllTableByUsername(String itemid, String tabletype);
    //void save(Item item);
     void deleteItemByItemId(String itemid);
    void update(Item item,int type);// when type==1, the buyer has set so, need also update the bought, sold table


}
