package com.example.backend.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

        if(!show){
            edit.setVisibility(View.GONE);
        }
        if(Items.size() > 0){

            Item item = Items.get(position);
            //update  the image
            Glide.with(context).load(item.getImageUrl()).into(itemimage);
            userimage.setImageResource(R.drawable.favorite_item);
            // return the buyer or seller name

            switch (category){
                case "Posted":
                    username.setText(item.getSellerName());
                    break;

                case "Sold":
                    username.setText(item.getBuyerName());
                    break;

                case "Bought":
                    username.setText(item.getSellerName());
                    break;

                case "Favourite":
                    username.setText(item.getSellerName());
                    break;
            }
            imageDescription.setText(item.getDescription());
            price.setText(item.getPrice());
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder bld= new AlertDialog.Builder(context);
                bld. setTitle("Alert");
                bld.setMessage("Are you sure to delete this record?");
                bld.setCancelable(true);
                bld.setPositiveButton("Yes, I want to delete.",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog,int which) {
                        Item remove_item = Items.remove(position);
                        String itemid = remove_item.getItemId();
                        itemService.deleteFromAllTableByUsername(itemid,category);
                        notifyDataSetChanged();

                    }
                });
                bld.setNegativeButton("No",new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                bld.show();
            }
        });
        return row;
    }

    //delete the history item from the database and update listview

    private class editListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            // to do transfer to edit post and update page
        }
    }

    private class deleteListener implements View.OnClickListener{
        private int position;
        public deleteListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder bld= new AlertDialog.Builder(context);
            bld. setTitle("Alert");
            bld.setMessage("Are you sure to delete this record?");
            bld.setCancelable(true);
            bld.setPositiveButton("Yes, I want to delete.",new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog,int which) {

                    Item to_remove=   Items.remove(position);
                    String itemid=to_remove.getItemId();
                    itemService.deleteFromAllTableByUsername(itemid,category);
                    notifyDataSetChanged();
                }
            });
            bld.setNegativeButton("No",new DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            bld.create().show();


        }
    }

}
