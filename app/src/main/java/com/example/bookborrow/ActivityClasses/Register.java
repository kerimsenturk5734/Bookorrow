package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookborrow.Messages;
import com.example.bookborrow.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity implements LocationListener {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etRePassword;
    private Button btnRegister;
    private EditText etNameSurname;
    private TextView twHasAccount;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog registerProgress;
    private FirebaseFirestore mFireStore;
    private LocationManager locationManager;
    private double longt;
    private double lat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        etEmail = (EditText) findViewById(R.id.reg_et_email);
        etPassword = (EditText) findViewById(R.id.reg_et_password);
        etRePassword = (EditText) findViewById(R.id.reg_et_repassword);
        btnRegister = (Button) findViewById(R.id.reg_btn_register);
        etNameSurname = (EditText) findViewById(R.id.reg_et_namesurname);
        twHasAccount = (TextView) findViewById(R.id.reg_tw_hasaccount);
        registerProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        ///////////////////////LOKASYON İŞLEMLERİ/////////////////////////
       /* longt = 0;
        lat = 0;*/

        if(ContextCompat.checkSelfPermission(Register.this,Manifest.permission.ACCESS_FINE_LOCATION)
        !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Register.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},100);
            }


        //////////////////////////////////////////////////////////////////
        twHasAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twHasAccount.setTextColor(getResources().getColor(R.color.purple_200));
                finish();

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String repassword = etRePassword.getText().toString();
                String nameSurname = etNameSurname.getText().toString();
                getLocation();

                if (locationManager.isLocationEnabled()) {
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                            !TextUtils.isEmpty(repassword) && !TextUtils.isEmpty(nameSurname)) {
                        if (password.length() >= 6) {
                            if (password.equals(repassword)) {
                                registerProgress.setTitle("Kaydediliyor");
                                registerProgress.setMessage("Kullanıcı kaydediliyor");
                                registerProgress.setCanceledOnTouchOutside(false);
                                registerProgress.show();
                                registerUser(email, password, nameSurname);
                            } else {
                                Messages.warningMessage("Parolaların aynı oluduğundan emin olunuz", Register.this);
                            }
                        } else {
                            Messages.warningMessage("Parola en az 6 karakter olmalıdır", Register.this);
                        }
                    } else {
                        Messages.warningMessage("Lütfen bilgileri tam doldurunuz", Register.this);
                    }
                } else {
                    Messages.warningMessage("Kayıt olmak için lütfen konum\n bilgisini açınız.", Register.this);
                }
            }
        });
    }


    private void registerUser(String email, String password, String nameSurname) {
        mFireStore = FirebaseFirestore.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("email", email);
                userMap.put("password", password);
                userMap.put("nameSurname", nameSurname);
                userMap.put("longt", String.valueOf(longt));
                userMap.put("lat", String.valueOf(lat));
                mFireStore.collection("Users").document(mAuth.getUid()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            registerProgress.dismiss();
                            Toast.makeText(getApplicationContext(), "Kayıt Başarılı", Toast.LENGTH_LONG).show();
                            Intent intentToMain = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intentToMain);
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Kayıt olmak için lütfen konum\n bilgisini açınız.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {

            locationManager=(LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,1, (LocationListener) Register.this);
            Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat= location.getLatitude(); //noktadan sonras 3 basamak alınacak
            longt=location.getLongitude();
            //Toast.makeText(Register.this,""+lat+","+longt,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Toast.makeText(Register.this,""+location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_SHORT).show();

        lat= location.getLatitude();
        longt= location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}