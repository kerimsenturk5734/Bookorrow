package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookborrow.Adapter.CustomGridAdapter;
import com.example.bookborrow.Messages;
import com.example.bookborrow.R;
import com.example.bookborrow.entity.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamBook extends AppCompatActivity {

    private ArrayList<Book> myBooks;
    private FirebaseFirestore mFireStore;
    private GridView gridView;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private AlertDialog.Builder alertDialog;
    private Bitmap my_image;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_book);
        getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(StreamBook.this);
        gridView=(GridView)findViewById(R.id.mb_gridview);

        myBooks=new ArrayList<>();

        getAllBooks();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int item, long l) {
                alertDialog=new AlertDialog.Builder(StreamBook.this);
                alertDialog
                        .setTitle("Kitap Yayınla")
                        .setMessage("Bu kitabı yayınlamak istediğinize emin misiniz?")
                        .setIcon(R.drawable.publishbook)
                        .setCancelable(false)
                        .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setMessage("Kitap Yayınlanıyor");
                                progressDialog.show();
                                String bookID=myBooks.get(item).getBookID();

                                Map<String, Object> book = new HashMap<>();
                                book.put("userID", mAuth.getCurrentUser().getUid());
                                book.put("bookID", bookID);

                                mFireStore=FirebaseFirestore.getInstance();
                                mFireStore.collection("PublishedBooks").document().set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.setMessage("Kitap Yayınlandı");
                                        progressDialog.dismiss();
                                        alertDialog.setMessage("Kitabınız yayınlandı")
                                                .setNegativeButton("Tamam", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                })
                                                .setPositiveButton(null,null).show();

                                    }
                                });
                            }
                        }).show();
            }
        });

    }
    private void setGrid(ArrayList<Book> bookArrayList){
        CustomGridAdapter adapter = new CustomGridAdapter(StreamBook.this, bookArrayList);
        gridView.setAdapter(adapter);
    }

    public ArrayList<Book> getAllBooks(){
        progressDialog.setTitle("Yükleniyor");
        progressDialog.setMessage("Kitaplar alınıyor");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mFireStore= FirebaseFirestore.getInstance();

        mFireStore.collection("Books").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<DocumentSnapshot> books = task.getResult().getDocuments();

                    for (int i = 0; i < books.size(); i++) {
                        Book newBook = new Book();
                        newBook.setBookID(books.get(i).getId());
                        progressDialog.setMessage("Kitaplar alındı");
                        newBook.setName(books.get(i).getString("name"));
                        newBook.setPage(books.get(i).getString("page"));
                        newBook.setAuthor(books.get(i).getString("author"));
                        newBook.setImagePath(books.get(i).getString("image"));


                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        storageRef.child("BooksImage/"+newBook.getImagePath()+".png").getDownloadUrl()
                               .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                      if(uri!=null){

                                          newBook.setImage(uri.toString());
                                          progressDialog.setMessage(uri.toString());
                                          setGrid(myBooks);
                                          progressDialog.dismiss();
                                      }
                                      else{
                                          progressDialog.setMessage("uri null");
                                      }
                                   }
                       })
                               .addOnFailureListener(new OnFailureListener() {
                                   @Override
                                   public void onFailure(@NonNull Exception e) {
                                       //info.setText("Lütfen yayınlamak istediğiniz kitabı seçiniz...");
                                       progressDialog.setMessage(e.getMessage().toString());
                                   }
                               });
                        myBooks.add(newBook);
                        //progressDialog.dismiss();
                    }
                }
                else{
                    Messages.warningMessage(task.getException().getMessage().toString(), StreamBook.this);
                }
            }
         });
        return myBooks;
    }
}