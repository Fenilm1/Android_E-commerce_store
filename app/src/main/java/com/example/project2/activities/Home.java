package com.example.project2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.project2.R;
import com.example.project2.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Fragment homeFragment;
    BottomNavigationView bottomNavigationView;

    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
        auth = FirebaseAuth.getInstance();

        bottomNavigationView =  findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.logout) {
                    auth.signOut();
                    startActivity(new Intent(Home.this, LoginActivity.class));
                    overridePendingTransition(0,0);
                    Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else if (itemId == R.id.cart) {
                    startActivity(new Intent(Home.this, CartActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.home) {
                    return true;
                };
                return false;
            }
        });
    }

    private void loadFragment(Fragment homeFragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container,homeFragment);
        transaction.commit();
    }
}