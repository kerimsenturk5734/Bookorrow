package com.example.bookborrow.ActivityClasses;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class ExplorerDealSelected extends AppCompatActivity {

    private ArrayList<Book> booksOfDeals;
    private GridView gridView;
    private CustomGridAdapter adapter;
    private FirebaseFirestore mFireStore;
    private TextView offerName;
    private TextView offerAdress;
    private Button giveOffer;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer_deal_selected);

        getSupportActionBar().hide();

        gridView=(GridView) findViewById(R.id.ds_gridview);

        offerName=(TextView) findViewById(R.id.ds_tw_offername);
        offerAdress=(TextView) findViewById(R.id.ds_tw_offeradress);

        giveOffer=(Button) findViewById(R.id.ds_btn_giveoffer);
        progressDialog=new ProgressDialog(ExplorerDealSelected.this);

        mFireStore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();


        booksOfDeals=new ArrayList<>();
        adapter=new CustomGridAdapter(ExplorerDealSelected.this,booksOfDeals);

        Bundle bundle=getIntent().getExtras();
        Book book=(Book) bundle.get("selectedbook");
        booksOfDeals.add(book);
        booksOfDeals.add(null);

        mFireStore.collection("PublishedBooks").document(booksOfDeals.get(0).getRecordID()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if(task.isSuccessful()){
                           mFireStore.collection("Users").document(task.getResult().getString("userID")).get()
                                   .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                           if(task.isSuccessful()){
                                               offerName.setText(task.getResult().getString("nameSurname"));
                                               offerAdress.setText(task.getResult().getString("email"));

                                           }
                                           else{
                                               Messages.warningMessage(task.getException().getMessage(),ExplorerDealSelected.this);
                                           }
                                       }
                                   });
                       }
                       else{
                           Messages.warningMessage(task.getException().getMessage(),ExplorerDealSelected.this);
                       }

                    }
                });
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1){
                    Intent intent=new Intent(ExplorerDealSelected.this,SelectBook.class);
                    intent.putExtra("bookOfDeals",booksOfDeals);
                    activityResultLaunch.launch(intent);

                }

            }
        });

        giveOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(booksOfDeals.get(1)!=null){
                    alertDialog=new AlertDialog.Builder(ExplorerDealSelected.this);
                    alertDialog
                            .setTitle("Teklif Yap")
                            .setMessage("Teklif yapmak istediğinize emin misiniz?")
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
                                    User newUser1=new User();
                                    User newUser2=new User();
                                    Book newBook1=new Book();
                                    Book newBook2=new Book();
                                    mFireStore.collection("PublishedBooks").document(booksOfDeals.get(0).getRecordID()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(ExplorerDealSelected.this,
                                                        "Teklif yapıldı\n Kabul edildiğinde konum bilgisine\n " +
                                                                "konumlar sayfasından ulaşabilirsiniz",Toast.LENGTH_LONG).show();

                                                newUser1.setUserID(task.getResult().getString("userID"));
                                                newBook1.setBookID(task.getResult().getString("bookID"));
                                                newBook2.setBookID(booksOfDeals.get(1).getBookID());
                                                newUser2.setUserID(mAuth.getCurrentUser().getUid());


                                                HashMap map1=new HashMap();
                                                map1.put("receiverID",newUser1.getUserID());
                                                map1.put("senderID",newUser2.getUserID());
                                                map1.put("bookID",newBook2.getBookID());
                                                map1.put("recordID",booksOfDeals.get(0).getRecordID());

                                                mFireStore.collection("Deals").document().set(map1)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            //Toast.makeText(ExplorerDealSelected.this,"Teklif yapıldı",Toast.LENGTH_SHORT).show();
                                                            //ÇALIŞIYOR
                                                        }
                                                        else{
                                                            Toast.makeText(ExplorerDealSelected.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });


                                            }
                                            else{
                                                Toast.makeText(ExplorerDealSelected.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                                            }
                                            onBackPressed();
                                        }
                                    });
                                }
                            }).show();
                    //teklif gönderilecek


                }
                else{
                    Toast.makeText(ExplorerDealSelected.this,"Lütfen bir kitap seçiniz",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    ActivityResultLauncher<Intent> activityResultLaunch = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 123) {
                        booksOfDeals=(ArrayList<Book>) result.getData().getExtras().get("offerbook");
                        adapter=new CustomGridAdapter(ExplorerDealSelected.this,booksOfDeals);
                        gridView.setAdapter(adapter);
                        //Toast.makeText(ExplorerDealSelected.this,booksOfDeals.get(1).getName(),Toast.LENGTH_SHORT).show();

                    } else{
                        Toast.makeText(ExplorerDealSelected.this,"Lütfen bir kitap seçiniz",Toast.LENGTH_SHORT).show();
                    }
                }
            });
}