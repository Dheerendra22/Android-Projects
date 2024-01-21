package com.example.vims;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Register_Activity extends AppCompatActivity {
    EditText fullName,email,password,con_password,phone ;
    Button register ;
    TextView goToSign ;
    ProgressBar pg ;

   FirebaseAuth faut ;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        setContentView(R.layout.activity_register);


       //  find Id

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.ConPassword);
        phone = findViewById(R.id.phoneNumber);
        register = findViewById(R.id.btnRegister);
        goToSign =findViewById(R.id.gotoSign_in);
        pg = findViewById(R.id.progressbar);


       //  Get instance

        faut = FirebaseAuth.getInstance();





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

    }
    if (mPhone.length() < 10) {
        phone.setError("Enter correct phone number!");
    }


});




    }





    }
