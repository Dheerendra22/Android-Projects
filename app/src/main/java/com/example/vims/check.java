//package com.example.vims;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.Objects;
//
//public class Login_activity extends AppCompatActivity {
//
//    private EditText emailInput, passwordInput;
//    private TextView goToRegister, forgot;
//    private Button login;
//    private ProgressBar pgBar;
//
//    private FirebaseAuth fAuth;
//    private FirebaseFirestore fireStore;
//    private String name, department, year, userId;
//    private SharedPreferences sharedPreferences;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        fAuth = FirebaseAuth.getInstance();
//        setupUI();
//        setClickListeners();
//
//        if (fAuth.getCurrentUser() != null) {
//            userId = fAuth.getCurrentUser().getUid();
//            fetchData();
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }
//    }
//
//    private void setupUI() {
//        // For Full screen
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//        setContentView(R.layout.activity_login);
//
//        // Find Ids of Components
//        emailInput = findViewById(R.id.email);
//        passwordInput = findViewById(R.id.password);
//        login = findViewById(R.id.btnLogin);
//        goToRegister = findViewById(R.id.Register);
//        pgBar = findViewById(R.id.progressbar);
//        forgot = findViewById(R.id.forgotPassword);
//
//        fireStore = FirebaseFirestore.getInstance();
//        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
//    }
//
//    private void setClickListeners() {
//        // On Click listener in sign-in Button
//        login.setOnClickListener(v -> {
//            String email = emailInput.getText().toString().trim();
//            String password = passwordInput.getText().toString().trim();
//
//            if (TextUtils.isEmpty(email)) {
//                emailInput.setError("Email is required!");
//                return;
//            }
//            if (TextUtils.isEmpty(password)) {
//                passwordInput.setError("Please enter Password");
//                return;
//            }
//            if (password.length() < 6) {
//                passwordInput.setError("Password must be 6 letters or more! ");
//                return;
//            }
//
//            pgBar.setVisibility(View.VISIBLE);
//
//            // Authenticate the user!
//            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    fetchData();
//                    Toast.makeText(Login_activity.this, "Logged in Successfully. ", Toast.LENGTH_SHORT).show();
//                    pgBar.setVisibility(View.GONE);
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                    finish();
//                } else {
//                    Toast.makeText(Login_activity.this, "Something Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                    pgBar.setVisibility(View.GONE);
//                }
//            });
//        });
//
//        // Set click listener on Register TextView.
//        goToRegister.setOnClickListener(v -> {
//            startActivity(new Intent(getApplicationContext(), Register_Activity.class));
//            finish();
//        });
//
//        forgot.setOnClickListener(v -> {
//            EditText resetMail = new EditText(v.getContext());
//            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
//            passwordResetDialog.setTitle("Reset Password");
//            passwordResetDialog.setMessage("Enter your Email to receive reset password link.");
//            passwordResetDialog.setView(resetMail);
//
//            passwordResetDialog.setPositiveButton("Send", (dialog, which) -> {
//                String email = resetMail.getText().toString();
//                fAuth.sendPasswordResetEmail(email)
//                        .addOnCompleteListener(task -> Toast.makeText(Login_activity.this, "Link sent to your email!", Toast.LENGTH_SHORT).show())
//                        .addOnFailureListener(e -> Toast.makeText(Login_activity.this, "Reset link Not sent ! " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            });
//
//            passwordResetDialog.setNegativeButton("Cancel", (dialog, which) -> {
//                // Do nothing on cancel
//            });
//
//            passwordResetDialog.create().show();
//        });
//    }
//
//    private void fetchData() {
//
//        if (userId != null) {
//
//            DocumentReference dataRef = fireStore.collection("Users").document(userId);
//            dataRef.addSnapshotListener(this, (value, error) -> {
//                if (value != null) {
//                    name = value.getString("FullName");
//                    department = value.getString("Department");
//                    year = value.getString("Year");
//
//                    assert year != null;
//                    Log.d("Tag", year);
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("FullName", name);
//                    editor.putString("Department", department);
//                    editor.putString("Year", year);
//                    editor.apply();
//                    Log.d("Tag", "Data fetched successfully");
//                }
//            });
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }
//}
