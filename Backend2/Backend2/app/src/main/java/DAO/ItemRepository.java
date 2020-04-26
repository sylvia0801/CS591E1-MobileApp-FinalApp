package DAO;

import android.app.Activity;

import com.example.backend.Adapter.HistoryItemAdapter;

import java.util.List;

import Model.Favourite;
import Model.Item;
/*
   Item table:
            -itemId (self -generated unique)
                        -itemId
                        -tagId
                        -sellerId
                        -buyerId
                        -sellerName
                        -buyerName
                        -title
                        -productName
                        -price
                        -description
                        -imageUrl
                        -address
                        -status  //0:on sell, 1: wait for respond and no more request for buying, 2:sold
                        -postRating
                        -rated("y"/"n")



    -All Table( key is user):
             -Favourite Table
                      -username
                          -item
             -Posted Table
                     -username(sellername)
                          -item
             -Bought Table
                     -username(buyername)
                          -item
             -Sold Table
                     -username(sellerame)
                         -item


 */
public interface ItemRepository {
   // void getItemByItemId(String itemId); // show item details page
    // save item to item and my Posted table
    Item saveToAllTable(Item item);
    // delete one history record, tabletype: historytype
   void  deleteFromAllTableByUsername(String itemid, String tabletype);
     void deleteItemByItemId(String itemid);
    // when type==1, the buyer has set so, need also update the bought, sold table
    // type=0, only update Item table and my Posted table and favorite table, item info
    void update(Item item,int type);


}
