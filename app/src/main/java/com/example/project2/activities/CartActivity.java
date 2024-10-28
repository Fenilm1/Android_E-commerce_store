package com.example.project2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2.R;
import com.example.project2.adapters.CartAdapter;
import com.example.project2.models.CartModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity{

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartModel> cartItems;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth;
    TextView totalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        auth = FirebaseAuth.getInstance();

        bottomNavigationView =  findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.logout) {
                    auth.signOut();
                    startActivity(new Intent(CartActivity.this, LoginActivity.class));
                    overridePendingTransition(0,0);
                    Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else if (itemId == R.id.cart) {
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(CartActivity.this, Home.class));
                    overridePendingTransition(0,0);
                    return true;
                };
                return false;
            }
        });

        cartRecyclerView = findViewById(R.id.cartRec);
        Button checkoutBtn = findViewById(R.id.checkoutBtn);
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(this,cartItems);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
        totalPrice = findViewById(R.id.totalPriceCart);

        // Load cart items from Firestore
        loadCartItems();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("My Total Amount"));

        checkoutBtn.setOnClickListener(v -> {
            startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    // BroadcastReceiver to update total amount
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            int totalBillAmount = intent.getIntExtra("Total Amount", 0);
            totalPrice.setText("Total Amount is $" + totalBillAmount);
        }
    };


    private void loadCartItems() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    int initialTotalAmount = 0;
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String itemId = documentSnapshot.getId();
                        CartModel cartItem = documentSnapshot.toObject(CartModel.class);
                        cartItem.setItemId(itemId);
                        cartItems.add(cartItem);
                        initialTotalAmount += cartItem.getPrice();
                        totalPrice.setText("Total Amount is $" + initialTotalAmount);
                    }
                    cartAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartActivity", "Error loading cart items: " + e.getMessage());
                });
    }
}