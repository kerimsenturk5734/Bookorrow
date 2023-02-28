package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookborrow.Adapter.CustomGridAdapter;
import com.example.bookborrow.Messages;
import com.example.bookborrow.R;
import com.example.bookborrow.entity.Book;
import com.example.bookborrow.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MySelectedDeal extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView offerName;
    private TextView offerAdress;
    private Button deny;
    private Button accept;
    private ProgressDialog progressDialog;
    private StorageReference storageRef;
    private FirebaseFirestore mFireStore;
    private GridView gridView;
    private AlertDialog.Builder alertDialog;
    private String dealRecordID;
    private String recordID;
    private ArrayList<Book> bookArrayList;
    private CustomGridAdapter adapter;
    private String publishID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_selected_deal);
        getSupportActionBar().hide();

        gridView=(GridView) findViewById(R.id.msd_gridView);
        offerName=(TextView) findViewById(R.id.msd_tw_offername);
        offerAdress=(TextView) findViewById(R.id.msd_tw_offeradress);
        deny=(Button) findViewById(R.id.msd_btn_deny);
        accept=(Button) findViewById(R.id.msd_btn_accept);
        bookArrayList=new ArrayList<>();
        //customGridAdapter=new CustomGridAdapter()
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(MySelectedDeal.this);
        storageRef = FirebaseStorage.getInstance().getReference();
        mFireStore=FirebaseFirestore.getInstance();
        adapter =new CustomGridAdapter(MySelectedDeal.this,bookArrayList);



        Bundle bundle=getIntent().getExtras();
        dealRecordID = bundle.getString("dealrecordID");
        recordID=bundle.getString("recordID");
        mFireStore.collection("Deals").document(dealRecordID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    mFireStore.collection("Books").document(task.getResult().getString("bookID")).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Book newBook=new Book();
                            newBook.setBookID(task.getResult().getId());
                            newBook.setAuthor(task.getResult().get("author").toString());
                            newBook.setName(task.getResult().get("name").toString());
                            newBook.setPage(task.getResult().get("page").toString());
                            newBook.setImagePath(task.getResult().get("image").toString());
                            bookArrayList.add(newBook);

                            storageRef.child("BooksImage/"+newBook.getImagePath()+".png").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if(uri!=null){
                                                newBook.setImage(uri.toString());
                                                gridView.setAdapter(adapter);
                                            }
                                            else{

                                            }
                                        }
                                    });

                        }
                    });
                    mFireStore.collection("Users").document(task.getResult().getString("senderID")).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    offerName.setText(task.getResult().getString("nameSurname"));
                                    offerAdress.setText(task.getResult().getString("email"));

                                }
                            });

                }
            }
        });

        mFireStore=FirebaseFirestore.getInstance();

        mFireStore.collection("PublishedBooks").document(recordID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    mFireStore.collection("Books").document(task.getResult().getString("bookID")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Book newBook=new Book();
                            newBook.setBookID(task.getResult().getId());
                            newBook.setAuthor(task.getResult().get("author").toString());
                            newBook.setName(task.getResult().get("name").toString());
                            newBook.setPage(task.getResult().get("page").toString());
                            newBook.setImagePath(task.getResult().get("image").toString());
                            bookArrayList.add(newBook);

                            storageRef.child("BooksImage/"+newBook.getImagePath()+".png").getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if(uri!=null){
                                                newBook.setImage(uri.toString());
                                                gridView.setAdapter(adapter);
                                            }
                                            else{

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    });
                }
                else{
                    Messages.warningMessage(task.getException().getMessage(),MySelectedDeal.this);
                }
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog=new AlertDialog.Builder(MySelectedDeal.this);
                alertDialog
                        .setTitle("Teklifi kabul et")
                        .setMessage("Teklifi kabul etmek istiyor musunuz?")
                        .setIcon(R.drawable.deals)
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
                                //karşı tarafa benim konum
                                //bana karşı tarafın konumu gelecek
                                mFireStore.collection("PublishedBooks").document(recordID).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mFireStore.collection("Deals").document(dealRecordID).get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        String senderID=task.getResult().getString("senderID");

                                                                        mFireStore.collection("Users").document(senderID).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        if(task.isSuccessful()){


                                                                                            mFireStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                                                                            if(task.isSuccessful()){
                                                                                                               /* HashMap hashMap=new HashMap();
                                                                                                                hashMap.put("receiverLat",task.getResult().getString("lat"));
                                                                                                                hashMap.put("receiverLongt",task.getResult().getString("longt"));
                                                                                                                hashMap.put("senderLat",task1.getResult().getString("lat"));
                                                                                                                hashMap.put("senderLongt",task1.getResult().getString("longt"));
                                                                                                                hashMap.put("senderID",senderID);
                                                                                                                hashMap.put("receiverID",mAuth.getCurrentUser().getUid());*/
                                                                                                                HashMap hashMap=new HashMap();
                                                                                                                hashMap.put("receiverLat",task1.getResult().getString("lat"));
                                                                                                                hashMap.put("receiverLongt",task1.getResult().getString("longt"));
                                                                                                                hashMap.put("senderLat",task.getResult().getString("lat"));
                                                                                                                hashMap.put("senderLongt",task.getResult().getString("longt"));
                                                                                                                hashMap.put("senderID",senderID);
                                                                                                                hashMap.put("receiverID",mAuth.getCurrentUser().getUid());

                                                                                                                mFireStore.collection("Locations")
                                                                                                                        .document().set(hashMap);
                                                                                                                Intent intent=new Intent(MySelectedDeal.this,MainActivity.class);
                                                                                                                startActivity(intent);
                                                                                                            }
                                                                                                            else{
                                                                                                                Messages
                                                                                                                        .warningMessage(task1.getException().getMessage(),
                                                                                                                                MySelectedDeal.this);
                                                                                                            }
                                                                                                        }
                                                                                                    });



                                                                                            mFireStore.collection("Deals").document(dealRecordID).delete()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if(task.isSuccessful()){
                                                                                                                Toast.makeText(MySelectedDeal.this,"Teklif kabul edildi." +
                                                                                                                        "\nKonum bilgisine konumlardan erişebilirsiniz",Toast.LENGTH_LONG).show();

                                                                                                            }
                                                                                                            else{
                                                                                                                Messages.warningMessage(task.getException().getMessage(),MySelectedDeal.this);
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                        else{
                                                                                            Messages
                                                                                                    .warningMessage(task.getException().getMessage(),
                                                                                                            MySelectedDeal.this);
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                    else{
                                                                        Messages.warningMessage(task.getException().getMessage(),MySelectedDeal.this);
                                                                    }
                                                                }
                                                            });


                                                }
                                                else{
                                                    Messages.warningMessage(task.getException().getMessage(),MySelectedDeal.this);
                                                }
                                            }
                                        });

                            }
                        }).show();
            }
        });

        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog=new AlertDialog.Builder(MySelectedDeal.this);
                alertDialog
                        .setTitle("Teklifi reddet")
                        .setMessage("Teklifi reddetmek istiyor musunuz?")
                        .setIcon(R.drawable.deals)
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
                                //konumlara konum gönderilecek
                                mFireStore.collection("Deals").document(dealRecordID).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(MySelectedDeal.this,"Teklif reddedildi",Toast.LENGTH_LONG).show();
                                                    Intent intent=new Intent(MySelectedDeal.this,MainActivity.class);
                                                    startActivity(intent);
                                                }
                                                else{
                                                    Messages.warningMessage(task.getException().getMessage(),MySelectedDeal.this);
                                                }
                                            }
                                        });
                            }
                        }).show();
            }
        });
    }


}