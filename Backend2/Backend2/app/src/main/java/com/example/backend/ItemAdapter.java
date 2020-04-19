package com.example.backend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import Model.Item;


public class ItemAdapter extends BaseAdapter {

    private List<Item> Items;
    private Context context;

    public ItemAdapter(List<Item> Items, Context context) {

        this.Items = Items;
        this.context = context;
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
            row = inflater.inflate(R.layout.custom_item_row, parent, false);
        }
        else
        {
            row = convertView;
        }

        ImageView itemimage = (ImageView) row.findViewById(R.id.imageView);
        TextView itemtitle = (TextView) row.findViewById(R.id.textView);
        TextView itemprice = (TextView) row.findViewById(R.id.priceView);

        //Log.d("MyTag", "set desginated views");
        Item item = Items.get(position);
        //System.out.println(item.getTitle());
        //TODO 从database里取相应item的image然后替换掉books
        itemimage.setImageResource(R.drawable.books);
        itemtitle.setText(item.getTitle());
        itemprice.setText(item.getPrice());
        //Log.d("MyTag", "set item details");
        return row;
    }
}