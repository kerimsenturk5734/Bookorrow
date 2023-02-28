package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SelectBook extends AppCompatActivity {

    private GridView gridView;
    private CustomGridAdapter adapter;
    private ProgressDialog progressDialog;
    private FirebaseFirestore mFireStore;
    private ArrayList<Book> myBooks;
    private FirebaseAuth mAuth;
    private AlertDialog.Builder alertDialog;
    private ArrayList<Book> temp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_book);

        getSupportActionBar().hide();

        gridView=(GridView) findViewById(R.id.sbook_gridview);
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(SelectBook.this);
        myBooks=new ArrayList<>();
        getAllBooks();
        Bundle bundle=getIntent().getExtras();
        temp=new ArrayList<>();
        temp=(ArrayList<Book>) bundle.get("bookOfDeals");


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               //SEÇİLEN KİTABI DEALSELECTED A GÖNDER
                Intent intent = new Intent();
                Book newBook=myBooks.get(i);
                temp.remove(1);
                temp.add(newBook);
                intent.putExtra("offerbook",temp);
                setResult(123,intent);
                finish();
            }
        });

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
                if(task.isSuccessful()){
                    List<DocumentSnapshot> books = task.getResult().getDocuments();

                    for(int i=0;i<books.size();i++){
                        Book newBook=new Book();
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
                                            setGrid(myBooks);
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
                        progressDialog.dismiss();
                    }
                }
                else{
                    Messages.warningMessage(task.getException().getMessage().toString(), SelectBook.this);
                }
            }

        });
        return myBooks;
    }

    private void setGrid(ArrayList<Book> bookArrayList){
        CustomGridAdapter adapter = new CustomGridAdapter(SelectBook.this, bookArrayList);
        gridView.setAdapter(adapter);
    }
}