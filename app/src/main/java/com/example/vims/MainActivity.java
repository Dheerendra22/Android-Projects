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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Button btnLogout;
    private ImageView profile;
    private TextView name, department, year, email, phone;

    private SharedPreferences preferences;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Initialize UI elements
        initUI();

        // Initialize Firebase
        initFirebase();

        // Set user details from SharedPreferences
        setUserDetails();

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
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phoneNumber);
    }

    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        preferences = getSharedPreferences("Profile", MODE_PRIVATE);
    }

    private void setUserDetails() {
        name.setText(preferences.getString("FullName", ""));
        department.setText(preferences.getString("Department", ""));
        year.setText(preferences.getString("Year", ""));
        email.setText(preferences.getString("Email", ""));
        phone.setText(preferences.getString("Phone", ""));
    }

    private void loadProfileImage() {
        StorageReference storeRef = storageReference.child("Users_Profile_Images/"
                + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/profile");

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
                + Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid() + "/profile");

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
}
