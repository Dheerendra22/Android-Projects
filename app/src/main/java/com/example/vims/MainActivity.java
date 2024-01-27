package com.example.vims;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
Button btnLogout ;
ImageView profile ;
TextView name, department, year ;

SharedPreferences preferences ;

FirebaseAuth firebaseAuth ;



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


        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login_activity.class));
            finish();
        });


        preferences = getSharedPreferences("Profile",MODE_PRIVATE);
        name.setText(preferences.getString("FullName", " "));
        department.setText(preferences.getString("Department", " "));
        year.setText(preferences.getString("Year", " "));




    }
}