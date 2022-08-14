package com.example.bookborrow.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
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

public class CustomGridAdapter2 extends BaseAdapter{

    private Context context;
    private ArrayList<String> arrayList;

    public CustomGridAdapter2(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
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
                    inflate(R.layout.activity_griditem2, viewGroup, false);

        }

        TextView dealName = (TextView)view.findViewById(R.id.textView9);


        dealName.setText(arrayList.get(i));
        dealName.setBackgroundColor(12312);


        return view;
    }
}