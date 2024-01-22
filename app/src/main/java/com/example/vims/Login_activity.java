package com.example.vims;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login_activity extends AppCompatActivity {
EditText email , password ;
TextView goToRegister ,forgot;
Button login ;
ProgressBar pgBar ;

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

        // Find Ids of Components

       login = findViewById(R.id.btnLogin);
       email = findViewById(R.id.email);
       password = findViewById(R.id.password);
       goToRegister = findViewById(R.id.Register);
       pgBar = findViewById(R.id.progressbar);
       forgot = findViewById(R.id.forgotPassword);
       fAuth = FirebaseAuth.getInstance();

       // On Click listener in sign in Button

       login.setOnClickListener(v -> {

           String mEmail = email.getText().toString().trim();
           String mPassword = password.getText().toString().trim();

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

          pgBar.setVisibility(View.VISIBLE);

           // Authenticate the user !

           fAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(task -> {
               if(task.isSuccessful()){
                   Toast.makeText(Login_activity.this, "Logged in Successfully. ", Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(getApplicationContext(), MainActivity.class));
                   finish();
               }else {
                   Toast.makeText(Login_activity.this, "Something Error!"+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                   pgBar.setVisibility(View.GONE);
               }
           });
       });

       // set click listener on Register TextView.

       goToRegister.setOnClickListener(v -> {
           startActivity(new Intent(getApplicationContext(), Register_Activity.class));
           finish();
       });

       forgot.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               EditText resetMail = new EditText(v.getContext());
               AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
               passwordResetDialog.setTitle("Reset Password");
               passwordResetDialog.setMessage("Enter your Email to receive reset password link.");
               passwordResetDialog.setView(resetMail);

               passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String email = resetMail.getText().toString();
                       fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               Toast.makeText(Login_activity.this, "Link sent to your email!", Toast.LENGTH_SHORT).show();

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Login_activity.this, "Reset link Not sent ! "+e.getMessage(), Toast.LENGTH_SHORT).show();

                           }
                       });

                   }
               });
               passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
               passwordResetDialog.create().show();
           }

       });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}