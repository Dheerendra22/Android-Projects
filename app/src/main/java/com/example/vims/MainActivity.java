package com.example.vims;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
Button btnLogout ;
ImageView profile ;
TextView name, department, year ;

FirebaseAuth firebaseAuth ;
FirebaseFirestore fireStore ;
String userId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        CardView cardViewProfile = findViewById(R.id.profileCard);
        LinearLayout outerLinearLayout = cardViewProfile.findViewById(R.id.l1);
        LinearLayout innerLinearLayout = outerLinearLayout.findViewById(R.id.l2);


        btnLogout = findViewById(R.id.logout);
        profile = findViewById(R.id.profileImg);
        name =  innerLinearLayout.findViewById(R.id.fullName);
        department = innerLinearLayout.findViewById(R.id.department);
        year =  innerLinearLayout.findViewById(R.id.year);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();


        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login_activity.class));
            finish();
        });


        DocumentReference docRef = fireStore.collection("Users").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                name.setText(documentSnapshot.getString("FullName"));
                department.setText(documentSnapshot.getString("Department"));
                year.setText(documentSnapshot.getString("Year"));
            }else {
                Toast.makeText(MainActivity.this, "Failed to fetch !", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());


    }
}