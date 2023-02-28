package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookborrow.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private AlertDialog.Builder alertDialog;
    private ProgressDialog progressDialog;
    private ImageView borrowBook;
    private ImageView library;
    private ImageView deals;
    private ImageView locations;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       getSupportActionBar().setTitle("Bookorrow");

        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(MainActivity.this);

        if(mAuth.getCurrentUser()==null){
            Intent intentToLogin=new Intent(getApplicationContext(),Login.class);
            startActivity(intentToLogin);
            Toast.makeText(getApplicationContext(),"Lütfen Giriş Yapın ",Toast.LENGTH_SHORT).show();
            finish();
        }
        borrowBook=(ImageView) findViewById(R.id.main_img_borrowbook);
        library=(ImageView) findViewById(R.id.main_img_publishbook);
        deals=(ImageView) findViewById(R.id.main_img_deals);
        locations=(ImageView) findViewById(R.id.main_img_locations);

        borrowBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this,Explorer.class);
                startActivity(intent);
            }
        });
        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this, StreamBook.class);
                startActivity(intent);
            }
        });
        deals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this, MyDeals.class);
                startActivity(intent);
            }
        });

        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(MainActivity.this, Locations.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //MainActivitys de geri butonunu işlevsiz kılar
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.action_logout){
            alertDialog=new AlertDialog.Builder(MainActivity.this);
            alertDialog
                    .setTitle("Çıkış Yap")
                    .setMessage("Çıkış yapmak istediğinize emin misiniz?")
                    .setIcon(R.drawable.logout)
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
                            mAuth.signOut();
                            Intent intentToLogin=new Intent(MainActivity.this,Login.class);
                            progressDialog.setTitle("Çıkış");
                            progressDialog.setMessage("Çıkış Yapılıyor");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setIcon(R.drawable.logout);
                            startActivity(intentToLogin);
                            finish();
                        }
                    }).show();

        }
        return true;
    }

}