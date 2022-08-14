package com.example.bookborrow.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookborrow.ActivityClasses.StreamBook;
import com.example.bookborrow.entity.Book;
import com.example.bookborrow.R;
import com.example.bookborrow.entity.User;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomGridAdapter3 extends BaseAdapter{

    private Context context;
    private List<Address> list;
    public ImageView imageView;

    public CustomGridAdapter3(Context context,List<Address> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // inflate the layout for each list row
        if (view == null) {
            view = LayoutInflater.from(context).
                    inflate(R.layout.activity_griditem3, viewGroup, false);

        }

        ImageView imgLoc = (ImageView)view.findViewById(R.id.img_loc);
        Glide.with(context).load(R.drawable.locationlogo).into(imgLoc);

        return view;
    }
}