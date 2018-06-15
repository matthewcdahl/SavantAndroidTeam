package com.savant.savantandroidteam.profile;

import android.content.Context;
import android.graphics.Picture;
import android.os.UserManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.savant.savantandroidteam.R;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    public ImageAdapter(Context c){
        mContext = c;
    }

    public int getCount(){
        return mThumbIds.length;
    }

    public Object getItem(int pos){
        return mThumbIds[pos];
    }

    public long getItemId(int pos){
        return pos;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

        Glide.with(mContext).load(mThumbIds[position]).into(imageView);


        //imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    private Integer[] mThumbIds = {
            R.drawable.profile_icon_1, R.drawable.profile_icon_2, R.drawable.profile_icon_3,
            R.drawable.profile_icon_4, R.drawable.profile_icon_5, R.drawable.profile_icon_6,
            R.drawable.profile_icon_7, R.drawable.profile_icon_8, R.drawable.profile_icon_9,
            R.drawable.profile_icon_10, R.drawable.profile_icon_11, R.drawable.profile_icon_12,
            R.drawable.profile_icon_13, R.drawable.profile_icon_14, R.drawable.profile_icon_15,
            R.drawable.profile_icon_16, R.drawable.profile_icon_17, R.drawable.profile_icon_18,
            R.drawable.profile_icon_19, R.drawable.profile_icon_20, R.drawable.profile_icon_21,
            R.drawable.profile_icon_22, R.drawable.profile_icon_23, R.drawable.profile_icon_24,
            R.drawable.profile_icon_25, R.drawable.profile_icon_26, R.drawable.profile_icon_27,
            R.drawable.profile_icon_28, R.drawable.profile_icon_29, R.drawable.profile_icon_30,
            R.drawable.profile_icon_31, R.drawable.profile_icon_32, R.drawable.profile_icon_33,
            R.drawable.profile_icon_34, R.drawable.profile_icon_35, R.drawable.profile_icon_36,
            R.drawable.profile_icon_37, R.drawable.profile_icon_38, R.drawable.profile_icon_39,
            R.drawable.profile_icon_40, R.drawable.profile_icon_41, R.drawable.profile_icon_42,
            R.drawable.profile_icon_43, R.drawable.profile_icon_44, R.drawable.profile_icon_45,
            R.drawable.profile_icon_46, R.drawable.profile_icon_47, R.drawable.profile_icon_48,
            R.drawable.profile_icon_49, R.drawable.profile_icon_50
    };



}

