package com.example.vims;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

    // UI elements
    EditText fullName, email, password, con_password, phone , rollNumber , enrollmentNumber;
    Button register;
    TextView goToSign;
    ProgressBar pg;
    Spinner spinnerDepart;
    Spinner spinnerYear;

    // Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;

    // User ID
    String userId;

    // Shared Preferences
    SharedPreferences preferences;

    // Constants
    String[] courses = {"BCA", "BCOM", "BSC"};
    String[] years = {"1st_Year", "2nd_Year", "3rd_Year"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screen settings
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

        // Initialize UI elements
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        con_password = findViewById(R.id.ConPassword);
        phone = findViewById(R.id.phoneNumber);
        enrollmentNumber = findViewById(R.id.enrollNumber);
        rollNumber = findViewById(R.id.rollNumber);
        register = findViewById(R.id.btnRegister);
        goToSign = findViewById(R.id.gotoSign_in);
        pg = findViewById(R.id.progressbar);
        spinnerDepart = findViewById(R.id.department);
        spinnerYear = findViewById(R.id.year);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        preferences = getSharedPreferences("Profile", MODE_PRIVATE);

        // Check if already logged in
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // Set up spinners
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        spinnerDepart.setAdapter(adapter1);
        spinnerDepart.setSelection(0);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        spinnerYear.setAdapter(adapter2);
        spinnerYear.setSelection(0);

        // Set onClickListener on Register Button.
        register.setOnClickListener(v -> {
            String mName = fullName.getText().toString();
            String mEmail = email.getText().toString().trim();
            String mPassword = password.getText().toString().trim();
            String conPassword = con_password.getText().toString().trim();
            String mYear = spinnerYear.getSelectedItem().toString();
            String mDepartment = spinnerDepart.getSelectedItem().toString();
            String mPhone = phone.getText().toString().trim();
            String mEnrollment = enrollmentNumber.getText().toString().trim();
            String mRollNumber = rollNumber.getText().toString().trim();
            String countryCode = "91";

            if (TextUtils.isEmpty(mName) || TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) ||
                    TextUtils.isEmpty(conPassword) || TextUtils.isEmpty(mPhone)) {
                Toast.makeText(Register_Activity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mPassword.length() < 6) {
                password.setError("Password must be 6 letters or more!");
                password.requestFocus();
                return;
            }

            if (!mPassword.equals(conPassword)) {
                con_password.setError("Password does not match!");
                password.requestFocus();
                return;
            }

            if (!PhoneNumberValidator.isValidPhoneNumber(mPhone, countryCode)) {
                // The phone number is not valid, show an error message
                phone.setError("Enter a valid phone number for India!");
                phone.requestFocus();
                return;
            }
            if(mRollNumber.length() != 10){
                rollNumber.setError("Enter Correct RollNumber!");
                rollNumber.requestFocus();
                return;
            }
            if(mEnrollment.length() != 12){
                rollNumber.setError("Enter Correct EnrollmentNumber!");
                rollNumber.requestFocus();
                return;
            }

            pg.setVisibility(View.VISIBLE);

            // Create User in Firebase
            fAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    String doc = (mName + "_" + userId).trim();
                    String col = (mDepartment + "_" + mYear);


                    DocumentReference dataRef = fireStore.collection("Users").document(userId);
                    Map<String, Object> user1 = new HashMap<>();
                    user1.put("FullName",mName);
                    user1.put("Department", mDepartment);
                    user1.put("Year", mYear);
                    dataRef.set(user1);

                    DocumentReference df = fireStore.collection(col).document(doc);

                    // Map the user's data
                    Map<String, Object> user2 = new HashMap<>();
                    user2.put("FullName", mName);
                    user2.put("Email", mEmail);
                    user2.put("Password", mPassword);
                    user2.put("Phone", mPhone);
                    user2.put("Department", mDepartment);
                    user2.put("Year", mYear);
                    user2.put("RollNumber",mRollNumber);
                    user2.put("EnrollmentNumber",mEnrollment);

                    // Set the user's data
                    df.set(user2).addOnCompleteListener(task1 -> {

                        // Create shared preference data storage
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("FullName", mName);
                        editor.putString("Phone", mPhone);
                        editor.putString("Department", mDepartment);
                        editor.putString("Year", mYear);
                        editor.putString("Email", mEmail);
                        editor.putString("RollNumber",mRollNumber);
                        editor.putString("EnrollmentNumber",mEnrollment);
                        editor.apply();

                        Toast.makeText(Register_Activity.this, "User Profile Created Successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }).addOnFailureListener(e -> {
                        Toast.makeText(Register_Activity.this, "Error! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        pg.setVisibility(View.GONE);
                    });

                } else {
                    Toast.makeText(Register_Activity.this, "Something went wrong! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    pg.setVisibility(View.GONE);
                }
            });
        });

        // Set click listener on sign-in TextView.
        goToSign.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login_activity.class));
            finish();
        });
    }







}
