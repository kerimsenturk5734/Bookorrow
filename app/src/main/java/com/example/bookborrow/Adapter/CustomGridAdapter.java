package com.example.bookborrow.Adapter;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustomGridAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Book> books;

    public CustomGridAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }


    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int i) {
        return books.get(i);
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
                    inflate(R.layout.activity_griditem, viewGroup, false);

        }


        Book currentBook = (Book) getItem(i);


        TextView bookName = (TextView)view.findViewById(R.id.grid_tw_bookname);
        TextView bookAuthor = (TextView)view.findViewById(R.id.grid_tw_bookauthor);
        TextView bookPage=(TextView) view.findViewById(R.id.grid_tw_bookpage);
        ImageView bookImage = (ImageView)view.findViewById(R.id.grid_img_bookimage);

        if(currentBook!=null){
            bookName.setText(currentBook.getName());
            bookAuthor.setText(currentBook.getAuthor());
            bookPage.setText(currentBook.getPage());
            bookAuthor.setText(currentBook.getAuthor());
            Glide.with(context).load(currentBook.getImage()).into(bookImage);
        }


        return view;
    }
}