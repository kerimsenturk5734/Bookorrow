package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bookborrow.Adapter.CustomGridAdapter2;
import com.example.bookborrow.Messages;
import com.example.bookborrow.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyDeals extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private FirebaseFirestore mFireStore;
    private CustomGridAdapter2 adapter;
    private ArrayList<String> nameList;

    private ArrayList<String> dealRecordID;
    private ArrayList<String> recordID;
    private GridView gridView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deals);

        getSupportActionBar().hide();
        gridView=(GridView) findViewById(R.id.md_gridView);
        progressDialog=new ProgressDialog(MyDeals.this);
        nameList =new ArrayList<>();

        dealRecordID =new ArrayList<>();
        recordID =new ArrayList<>();
        adapter=new CustomGridAdapter2(MyDeals.this, nameList);

        mAuth=FirebaseAuth.getInstance();
        mFireStore=FirebaseFirestore.getInstance();

        mFireStore.collection("Deals").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<DocumentSnapshot> documents=task.getResult().getDocuments();
                    for(int i=0;i< documents.size();i++){
                        if(documents.get(i).get("receiverID").equals(mAuth.getCurrentUser().getUid())){
                            //Messages.warningMessage("Kullanıcıya gelen teklif bulundu",MyDeals.this);
                            dealRecordID.add(documents.get(i).getId());
                            recordID.add(documents.get(i).get("recordID").toString());
                            mFireStore.collection("Users").document(documents.get(i).get("senderID").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                   if(task.isSuccessful()){
                                       nameList.add(task.getResult().getString("nameSurname")+" kullanıcısından gelen teklif");
                                       gridView.setAdapter(adapter);

                                   }
                                   else{
                                       Messages.warningMessage(task.getException().getMessage(),MyDeals.this);
                                   }


                                }
                            });

                        }
                    }
                }
                else{
                    Messages.warningMessage(task.getException().getMessage().toString(),MyDeals.this);
                }

            }

        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MyDeals.this,MySelectedDeal.class);
                intent.putExtra("dealrecordID", dealRecordID.get(i));
                intent.putExtra("recordID", recordID.get(i));
                startActivity(intent);

            }
        });

    }

    public static  void infoMessageOnBackPressed(String message, Context activity){



    }
}