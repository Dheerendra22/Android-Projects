package com.example.vims;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Register_Activity extends AppCompatActivity {
    EditText fullName,email,password,con_password,phone ;
    Button register ;
    TextView goToSign ;
    ProgressBar pg ;
   FirebaseAuth fAuth ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For Full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        setContentView(R.layout.activity_register);


       //  find Ids

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.ConPassword);
        phone = findViewById(R.id.phoneNumber);
        register = findViewById(R.id.btnRegister);
        goToSign =findViewById(R.id.gotoSign_in);
        pg = findViewById(R.id.progressbar);


        //  Get instance

        fAuth = FirebaseAuth.getInstance();

        //Check If already Login or not ?

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //set Onclick listener on Register Button.

        register.setOnClickListener(v -> {

             String name = fullName.getText().toString() ;
             String mEmail = email.getText().toString().trim();
             String mPassword = password.getText().toString().trim();
             String conPassword = con_password.getText().toString().trim();
             String mPhone = phone.getText().toString().trim();


    if(TextUtils.isEmpty(name)){
        fullName.setError("Full name is required!");
        return;
    }
    if (TextUtils.isEmpty(mEmail)) {
        email.setError("Email is required!");
        return;
    }
    if (TextUtils.isEmpty(mPassword)) {
        password.setError("Please enter Password");
        return;
    }
    if (mPassword.length() < 6) {
        password.setError("Password must be 6 letters or more! ");
        return;
    }
    if (!mPassword.equals(conPassword)) {
        con_password.setError("Enter password Correctly!");
        return;
    }
    if (mPhone.length() < 10) {
        phone.setError("Enter correct phone number!");
        return;
    }

    pg.setVisibility(View.VISIBLE);

    // Create User in Firebase !

    fAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(task -> {
        if(task.isSuccessful()){
            Toast.makeText(Register_Activity.this, "User Created Successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            Toast.makeText(Register_Activity.this, "Something Error!"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            pg.setVisibility(View.GONE);
        }
     });

  });
        // set click listener on sign in textview.

        goToSign.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login_activity.class));
            finish();
        });


    }

}
