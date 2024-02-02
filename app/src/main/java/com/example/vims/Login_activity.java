package com.example.vims;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login_activity extends AppCompatActivity {
    EditText email, password;
    TextView goToRegister, forgot;
    Button login;
    ProgressBar pgBar;

    FirebaseAuth fAuth;

    SharedPreferences sharedPreferences ;

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
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);

        // On Click listener in sign-in Button
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

            // Authenticate the user!
            fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login_activity.this, "Logged in Successfully. ", Toast.LENGTH_SHORT).show();
                    pgBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(Login_activity.this, "Something Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    pgBar.setVisibility(View.GONE);
                }
            });
        });

        // Set click listener on Register TextView.
        goToRegister.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Register_Activity.class));
            finish();
        });

        forgot.setOnClickListener(v -> {

            EditText resetMail = new EditText(v.getContext());
            resetMail.setBackgroundResource(R.drawable.edit_text);
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset Password");
            passwordResetDialog.setMessage("Please enter your registered email address to receive a link for resetting your password.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Send", (dialog, which) -> {
                String resetEmail = resetMail.getText().toString();
                String email = sharedPreferences.getString("Email","");
                if(resetEmail.equals(email)){
                    fAuth.sendPasswordResetEmail(resetEmail)
                            .addOnCompleteListener(task -> Toast.makeText(Login_activity.this, "Link sent to your email, Please check your nail.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(Login_activity.this, "Reset link Not sent ! " + e.getMessage(), Toast.LENGTH_SHORT).show());

                }else {
                    resetMail.setError("Please Enter Registered Email!");
                    Toast.makeText(this, "Please Enter Registered Email!", Toast.LENGTH_LONG).show();
                }

            });
            passwordResetDialog.setNegativeButton("Cancel", (dialog, which) -> {
                // Do nothing on cancel
            });

            passwordResetDialog.create().show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
