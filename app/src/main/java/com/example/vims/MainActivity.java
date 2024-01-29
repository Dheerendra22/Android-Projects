package com.example.vims;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private ImageView profile;
    private TextView name, department, year, rollNumber, phone;

    private SharedPreferences sharedPreferences;
    private FirebaseAuth fAuth;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private StorageReference storageReference;
    private String userId;

    private FirebaseFirestore fireStore;
    private String mName, mDepartment, mYear, mEmail, mPhone, mRollNumber, mEnrollment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }

        // Initialize UI elements
        initUI();

        // Initialize Firebase
        initStorage();

        // Set user details from SharedPreferences
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);

        // Fetch and set user details
        fetchData();

        // Load profile image
        loadProfileImage();

        // Set up ActivityResultLauncher for gallery
        setupGalleryLauncher();

        // Set click listeners
        setClickListeners();
    }

    private void initUI() {
        btnLogout = findViewById(R.id.logout);
        profile = findViewById(R.id.profileImg);
        name = findViewById(R.id.fullName);
        department = findViewById(R.id.department);
        year = findViewById(R.id.year);
        rollNumber = findViewById(R.id.rollNumber);
        phone = findViewById(R.id.phoneNumber);
    }

    private void initStorage() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void fetchData() {
        if (userId != null) {
            // Fetch user details
            DocumentReference userRef = fireStore.collection("Users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    mName = documentSnapshot.getString("FullName");
                    mDepartment = documentSnapshot.getString("Department");
                    mYear = documentSnapshot.getString("Year");

                    name.setText(mName);
                    department.setText(mDepartment);
                    year.setText(mYear);

                    // Save details to SharedPreferences
                    saveToSharedPreferences("FullName", mName);
                    saveToSharedPreferences("Department", mDepartment);
                    saveToSharedPreferences("Year", mYear);

                    // Fetch additional details from the second collection
                    fetchAdditionalDetails();
                }
            }).addOnFailureListener(e -> handleError("Error fetching user data: " + e.getMessage()));
        }
    }

    private void fetchAdditionalDetails() {
        if (mName != null && userId != null) {
            String doc = (mName + "_" + userId).trim();
            String col = (mDepartment + "_" + mYear);

            // Fetch additional details from the second collection
            DocumentReference additionalDetailsRef = fireStore.collection(col).document(doc);
            additionalDetailsRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    mEmail = documentSnapshot.getString("Email");
                    mPhone = documentSnapshot.getString("Phone");
                    mRollNumber = documentSnapshot.getString("RollNumber");
                    mEnrollment = documentSnapshot.getString("EnrollmentNumber");

                    rollNumber.setText(mRollNumber);
                    phone.setText(mPhone);

                    // Save additional details to SharedPreferences
                    saveToSharedPreferences("Email", mEmail);
                    saveToSharedPreferences("RollNumber", mRollNumber);
                    saveToSharedPreferences("EnrollmentNumber", mEnrollment);
                }
            }).addOnFailureListener(e -> handleError("Error fetching additional details: " + e.getMessage()));
        }
    }

    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void loadProfileImage() {
        StorageReference storeRef = storageReference.child("Users_Profile_Images/"
                + userId + "/profile");

        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            profile.setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(uri).into(profile);
        }).addOnFailureListener(e ->
                Toast.makeText(MainActivity.this, "Unable to load Profile Image!" + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri imageUri = data.getData();
                    uploadImageToFirebase(imageUri);
                }
            }
        });
    }

    private void setClickListeners() {
        profile.setOnClickListener(v -> openGallery());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void openGallery() {
        Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(openGallery);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageReference.child("Users_Profile_Images/"
                + Objects.requireNonNull(fAuth.getCurrentUser()).getUid() + "/profile");

        fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profile.setBackgroundColor(Color.TRANSPARENT);
                Picasso.get().load(uri).into(profile);
            });
            Toast.makeText(MainActivity.this, "Image Uploaded Successfully.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Image Not Uploaded! " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), Login_activity.class));
        finish();
    }

    private void handleError(String message) {
        // Handle the error
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
