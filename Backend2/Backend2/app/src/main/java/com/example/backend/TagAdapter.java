package com.example.backend;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends BaseAdapter {
    private String[] TextArray;
    private ArrayList<Integer> ImageList;
    Context context;

    public TagAdapter(Context aContext) {
        context = aContext;
        TextArray = context.getResources().getStringArray(R.array.ItemCategory);
        ImageList = new ArrayList<>();
        ImageList.add(R.drawable.clothes);
        ImageList.add(R.drawable.books);
        ImageList.add(R.drawable.electronics);
        ImageList.add(R.drawable.furnitures);

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
        if (convertView == null) {  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_tag_row, parent, false);
        } else {
            row = convertView;
        }

        ImageView image = (ImageView) row.findViewById(R.id.imageView);
        TextView text = (TextView) row.findViewById(R.id.textView);

        image.setImageResource(ImageList.get(index).intValue());
        text.setText(TextArray[index]);

        return row;
    }
}

/*
public class TagAdapter extends ArrayAdapter<Tag> {


    // adapter method 2: has log but no view on phone
    private int resourceId;

    public TagAdapter(Context context, int textViewResourceId, List<Tag> object) {
        super(context, textViewResourceId, object);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Tag tag = getItem(position);
        //
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);

        ImageView tagImage = (ImageView) view.findViewById(R.id.imageView);

        TextView tagName = (TextView) view.findViewById(R.id.textView);

        tagImage.setImageResource(tag.getImageName());

        tagName.setText(tag.getTitle());
        Log. d("myTag", "create view");

        return  view;
    }

     */

/*
    // Adapter Method 1 - no entry to Log
    private String[] tagList;
    private Integer[] imageList;
    private Activity context;

    public TagAdapter(Activity context, String[] tagList, Integer[] imageList) {
        super(context, R.layout.custom_tag_row);

        this.context = context;
        this.tagList = tagList;
        this.imageList = imageList;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        viewHolder viewHolder = null;

        if (r == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.custom_tag_row, null, true);
            viewHolder = new viewHolder(r);
            r.setTag(viewHolder);
        }
        else {
            viewHolder = (viewHolder)r.getTag();
        }
        Log. d("myTag", "create r");
        viewHolder.imageView.setImageResource(imageList[position]);
        viewHolder.textView.setText(tagList[position]);
        Log. d("myTag", "imageView&textView");
        return r;
    }

    class viewHolder {
        ImageView imageView;
        TextView textView;

        viewHolder (View v) {
            imageView = (ImageView)v.findViewById(R.id.imageView);
            textView = (TextView)v.findViewById(R.id.textView);
        }
    }
 */