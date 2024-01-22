package com.example.vims;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register_Activity extends AppCompatActivity {
    EditText fullName,email,password,con_password,phone , department ,year;
    Button register ;
    TextView goToSign ;
    ProgressBar pg ;
   FirebaseAuth fAuth ;
   FirebaseFirestore fireStore ;

   String userId ;


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
        department = findViewById(R.id.department);
        register = findViewById(R.id.btnRegister);
        goToSign =findViewById(R.id.gotoSign_in);
        year = findViewById(R.id.year);
        pg = findViewById(R.id.progressbar);


        //  Get instance

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        //Check If already Login or not ?

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //set Onclick listener on Register Button.

        register.setOnClickListener(v -> {

             String mName = fullName.getText().toString() ;
             String mEmail = email.getText().toString().trim();
             String mPassword = password.getText().toString().trim();
             String conPassword = con_password.getText().toString().trim();
             String mPhone = phone.getText().toString().trim();
             String mDepartment = department.getText().toString().trim();
             String mYear =  year.getText().toString().trim();


    if(TextUtils.isEmpty(mName)){
        fullName.setError("Full name is required!");
        return;
    }else if (TextUtils.isEmpty(mEmail)) {
        email.setError("Email is required!");
        return;
    }else if (TextUtils.isEmpty(mPassword)) {
        password.setError("Please enter Password");
        return;
    }else if (mPassword.length() < 6) {
        password.setError("Password must be 6 letters or more! ");
        return;
    }else if (!mPassword.equals(conPassword)) {
        con_password.setError("Enter password Correctly!");
        return;
    }else if (mPhone.length() != 10) {
        phone.setError("Enter correct phone number!");
        return;
    }else if(TextUtils.isEmpty(mDepartment)){
        department.setError("Please enter your department!");
        return;
    }else if(TextUtils.isEmpty(mYear)){
        year.setError("Please enter your year!");
        return;
    }



    pg.setVisibility(View.VISIBLE);

    // Create User in Firebase !

    fAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(task -> {
        if(task.isSuccessful()){
//            Toast.makeText(Register_Activity.this, "User Created Successfully.", Toast.LENGTH_SHORT).show();
            userId = fAuth.getCurrentUser().getUid();
            DocumentReference df = fireStore.collection("Users").document(userId);
            Map<String,Object> user = new HashMap<>();
            user.put("FullName",mName);
            user.put("Email",mEmail);
            user.put("Phone",mPhone);
            user.put("Department",mDepartment);
            user.put("Year",mYear);
            df.set(user).addOnCompleteListener(task1 -> {
                Log.d("Tag","User profile Created");
                Toast.makeText(Register_Activity.this, "User Profile Created Successfully.", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> {
                Toast.makeText(Register_Activity.this, "Error! "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            });

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
