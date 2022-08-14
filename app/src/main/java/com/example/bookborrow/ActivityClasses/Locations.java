package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bookborrow.Adapter.CustomGridAdapter2;
import com.example.bookborrow.Adapter.CustomGridAdapter3;
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
import java.util.Locale;

public class Locations extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private FirebaseFirestore mFireStore;
    private CustomGridAdapter3 adapter;
    private List<Address> addresses;

    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        getSupportActionBar().hide();

        /////////ADRES ISLEMLERI///////////////
        Geocoder geocoder;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses=new ArrayList<>();



        gridView=(GridView) findViewById(R.id.loc_gridview);

        mFireStore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(Locations.this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fullAddress=addresses.get(i).getCountryName();
                fullAddress+=", "+addresses.get(i).getPostalCode();
                fullAddress+=", "+addresses.get(i).getFeatureName();
                Messages.infoMessage(fullAddress,Locations.this);
            }
        });

        mFireStore.collection("Locations").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           List<DocumentSnapshot> documents=task.getResult().getDocuments();

                           for(int i=0;i< documents.size();i++){
                               if(mAuth.getCurrentUser().getUid()
                                       .equals(documents.get(i).get("receiverID"))){
                                   //senderLat ve senderLongt değerleri çekilecek
                                   Double senderLat=Double.valueOf(documents.get(i).getString("senderLat"));
                                   Double senderLongt=Double.valueOf(documents.get(i).getString("senderLongt"));

                                   //cast işlemi yapamıyor
                                   try{
                                       addresses.add(geocoder.getFromLocation(senderLat,senderLongt, 1).get(0));
                                       Toast.makeText(Locations.this,geocoder
                                               .getFromLocation(senderLat,senderLongt, 1)
                                               .get(0).getCountryName(),Toast.LENGTH_SHORT).show();
                                   }
                                   catch (Exception e){
                                        Messages.warningMessage(e.getMessage(),Locations.this);
                                   }
                               }
                               else if(mAuth.getCurrentUser().getUid()
                                       .equals(documents.get(i).get("senderID"))){
                                   //receiverLat ve receiverLongt değerleri çekilecek
                                   Double receiverLat=Double.parseDouble(documents.get(i).getString("receiverLat"));
                                   Double receiverLongt=Double.parseDouble(documents.get(i).getString("receiverLongt"));
                                   try{
                                       addresses.add(geocoder.getFromLocation(receiverLat,receiverLongt, 1).get(0));
                                   }
                                   catch (Exception e){
                                       Messages.warningMessage(e.getMessage(),Locations.this);
                                   }

                               }
                               else if(documents.isEmpty()){
                                   Messages.infoMessage("Kullanıcıya ait konum bilgisi bulunamadı"
                                           ,Locations.this);
                                   AlertDialog.Builder alert=new AlertDialog.Builder(Locations.this);
                                   alert
                                           .setTitle("Bilgi")
                                           .setMessage("Kullanıcıya ait konum bilgisi bulunamadı")
                                           .setIcon(R.drawable.info)
                                           .setCancelable(false)
                                           .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                               @Override
                                               public void onClick(DialogInterface dialogInterface, int i) {
                                                   dialogInterface.dismiss();
                                                   onBackPressed();
                                               }
                                           }).show();
                               }

                           }
                           adapter=new CustomGridAdapter3(Locations.this,addresses);
                           gridView.setAdapter(adapter);
                       }
                       else{
                           Messages.warningMessage(task.getException().getMessage()
                                   ,Locations.this);
                       }

                    }

                });



    }

}