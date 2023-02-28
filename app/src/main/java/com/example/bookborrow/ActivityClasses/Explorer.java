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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Explorer extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private StorageReference storageRef;
    private FirebaseFirestore mFireStore;
    private GridView gridView;
    private AlertDialog.Builder alertDialog;
    private  ArrayList<Book> publishedBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(Explorer.this);
        gridView=(GridView) findViewById(R.id.main_gridView);
        publishedBooks=new ArrayList<>();

        getPublishedBooks();



        storageRef= FirebaseStorage.getInstance().getReference();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentToDealSelected=new Intent(Explorer.this, ExplorerDealSelected.class);
                intentToDealSelected.putExtra("selectedbook",publishedBooks.get(i));
                startActivity(intentToDealSelected);
            }
        });
    }



    private void setGrid(ArrayList<Book> bookArrayList){
        CustomGridAdapter adapter = new CustomGridAdapter(this, bookArrayList);

        gridView=(GridView)findViewById(R.id.main_gridView);
        gridView.setAdapter(adapter);

    }

    private ArrayList<Book> getPublishedBooks(){
        progressDialog.setTitle("Yükleniyor");
        progressDialog.setMessage("Kitaplar alınıyor");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        mFireStore= FirebaseFirestore.getInstance();


        mFireStore.collection("PublishedBooks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> documents=task.getResult().getDocuments();
                    mFireStore= FirebaseFirestore.getInstance();
                    for( int i=0;i< documents.size();i++){
                        if(!documents.get(i).get("userID")
                                .equals(mAuth.getCurrentUser().getUid())){
                            int finalI = i;
                            Book newBook=new Book();
                            mFireStore.collection("Books").document(documents.get(i).get("bookID").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        newBook.setBookID(task.getResult().getId());
                                        newBook.setAuthor(task.getResult().get("author").toString());
                                        newBook.setName(task.getResult().get("name").toString());
                                        newBook.setPage(task.getResult().get("page").toString());
                                        newBook.setImagePath(task.getResult().get("image").toString());
                                        newBook.setRecordID(documents.get(finalI).getId());

                                        storageRef.child("BooksImage/"+newBook.getImagePath()+".png").getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        if(uri!=null){
                                                            newBook.setImage(uri.toString());
                                                            setGrid(publishedBooks);
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
                                                        progressDialog.setMessage(e.getMessage().toString());
                                                    }
                                                });
                                    }
                                    else{
                                        Messages.warningMessage(task.getException().getMessage().toString(), Explorer.this);
                                    }

                                }
                            });
                            publishedBooks.add(newBook);
                        }
                    }
                }
                else{
                    Messages.warningMessage(task.getException().getMessage().toString(), Explorer.this);
                }
            }
        });

        return publishedBooks;
    }

}