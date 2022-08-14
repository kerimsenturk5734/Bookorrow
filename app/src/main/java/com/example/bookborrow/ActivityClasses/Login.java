package com.example.bookborrow.ActivityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class Login extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private TextView twRegister;
    private Button btnSignIn;
    private ProgressDialog loginProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail=(EditText) findViewById(R.id.login_et_email);
        etPassword=(EditText) findViewById(R.id.login_et_password);
        btnSignIn=(Button) findViewById(R.id.login_btn_signin);
        twRegister=(TextView) findViewById(R.id.login_tw_register);
        loginProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        twRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twRegister.setTextColor(getResources().getColor(R.color.purple_200));
                Intent intentToRegister=new Intent(getApplicationContext(),Register.class);
                startActivity(intentToRegister);

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=etEmail.getText().toString();
                String password=etPassword.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    loginProgress.setTitle("Giriş");
                    loginProgress.setMessage("Giriş Yapılıyor");
                    loginProgress.setCanceledOnTouchOutside(false);
                    loginProgress.show();
                    loginUser(email,password);

                }
                else{
                    Messages.warningMessage("Lütfen bilgileri tam doldurunuz",Login.this);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Giriş Başarılı",Toast.LENGTH_LONG).show();
                    Intent intentToMain=new Intent(Login.this, MainActivity.class);
                    startActivity(intentToMain);
                    finish();
                }
                else{
                    loginProgress.dismiss();
                    Messages.warningMessage("Yanlış email veya şifre \nHATA:" +task.getException().getMessage().toString(),Login.this);
                }
            }
        });
    }




}