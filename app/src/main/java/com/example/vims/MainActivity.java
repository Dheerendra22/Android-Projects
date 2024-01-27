package com.example.vims;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
Button btnLogout ;
ImageView profile ;
TextView name, department, year ,email , phone;

SharedPreferences preferences ;

FirebaseAuth firebaseAuth ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);



        btnLogout = findViewById(R.id.logout);
        profile = findViewById(R.id.profileImg);
        name =  findViewById(R.id.fullName);
        department = findViewById(R.id.department);
        year =  findViewById(R.id.year);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phoneNumber);

        firebaseAuth = FirebaseAuth.getInstance();


        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login_activity.class));
            finish();
        });


        preferences = getSharedPreferences("Profile",MODE_PRIVATE);
        name.setText(preferences.getString("FullName", " "));
        department.setText(preferences.getString("Department", " "));
        year.setText(preferences.getString("Year", " "));
        email.setText(preferences.getString("Email", " "));
        phone.setText(preferences.getString("Phone", " "));




    }
}