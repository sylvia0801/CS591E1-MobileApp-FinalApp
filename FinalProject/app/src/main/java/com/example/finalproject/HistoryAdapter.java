package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
    private String[] TextArray;
    private ArrayList<Integer> ImageList;

    Context context;

    public HistoryAdapter(Context aContext) {
        context = aContext;

        TextArray = context.getResources().getStringArray(R.array.ItemHistory);

        ImageList = new ArrayList<>();
        ImageList.add(R.drawable.posted_item);
        ImageList.add(R.drawable.sold_item);
        ImageList.add(R.drawable.bought_item);
        ImageList.add(R.drawable.favorite_item);

    }

    @Override
    public int getCount() {
        return TextArray.length;
    }

    @Override
    public Object getItem(int position) {
        return TextArray[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;

        final int index = position;

        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_row1, parent, false);
        }
        else
        {
            row = convertView;
        }

        ImageView image = (ImageView) row.findViewById(R.id.leftimage);
        TextView text = (TextView) row.findViewById(R.id.Item_Text);

        image.setImageResource(ImageList.get(index).intValue());
        text.setText(TextArray[index]);

        return row;
    }
}
