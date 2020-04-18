package com.example.backend.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.backend.R;
import java.util.ArrayList;
import java.util.List;

import DAO.Impl.DownloadImageAsync;
import DAO.Impl.ItemRepoImpl;
import Model.Item;
import de.hdodenhof.circleimageview.CircleImageView;
public class HistoryItemAdapter extends BaseAdapter {
    private List<Item> Items;
    private Context context;
    private boolean show;
    private String category;
    private ItemRepoImpl itemService=new ItemRepoImpl();


    public HistoryItemAdapter(List<Item> res, boolean show, String category, Context context) {
        this.Items = res;
        this.context = context;
        this.show = show;
        this.category = category;

    }

    public HistoryItemAdapter(Context context, boolean show){

        Items = new ArrayList<>();
        this.context = context;
        this.show = show;
    }



    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_list_row, parent, false);
        }
        else
        {
            row = convertView;
        }

        CircleImageView  userimage = (CircleImageView) row.findViewById(R.id.Histroy_UserImage);
        ImageView itemimage = (ImageView) row.findViewById(R.id.History_Item_Image);

        TextView username = (TextView) row.findViewById(R.id.History_UserName);
        TextView imageDescription = (TextView) row.findViewById(R.id.History_Item_Description);

        Button edit = (Button) row.findViewById(R.id.History_Edit);
        Button delete = (Button) row.findViewById(R.id.History_Delete);
        TextView price=(TextView) row.findViewById(R.id.History_Item_price);
        //TextView Des
        delete.setOnClickListener(new deleteListener(position));

        if(!show){
            edit.setVisibility(View.GONE);
        }

        if(Items.size() > 0){
            //To do, need to provide a function return the image
            Item item = Items.get(position);

            Glide.with(context).load(item.getImageUrl()).into(itemimage);

            userimage.setImageResource(R.drawable.favorite_item);
        //    itemimage.setImageResource(R.drawable.favorite_item);

            //To do need to provide a function return the buyer or seller name
            switch (category){
                case "Post Items":
                    username.setText(item.getSellerId());
                    break;

                case "Sold Items":
                    username.setText(item.getBuyerId());
                    break;

                case "Bought Items":
                    username.setText(item.getSellerId());
                    break;

                case "Favorite Items":
                    username.setText(item.getSellerId());
                    break;
            }
            imageDescription.setText(item.getDescription());
            price.setText(item.getPrice());
           // Log.i()
        }

        //hard code the image and textview part, need to comment
//        else{
//            userimage.setImageResource(R.drawable.favorite_item);
//            itemimage.setImageResource(R.drawable.favorite_item);
//
//            username.setText("Username");
//            imageDescription.setText("Here is Description");
//        }
        return row;
    }

    private class deleteListener implements View.OnClickListener{
        private int position;

        public deleteListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Items.remove(position);
            //To do, need to delete the history item in the database

            notifyDataSetChanged();
        }
    }
}
