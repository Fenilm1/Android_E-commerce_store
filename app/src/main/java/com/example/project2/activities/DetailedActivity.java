package com.example.project2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.models.CartModel;
import com.example.project2.models.CategoryModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class DetailedActivity extends AppCompatActivity {

    private TextView categoryNameTextView;
    private ImageView categoryImageView;
    private TextView categoryTypeTextView;
    private TextView categoryPriceTextView;
    private TextView quantityTextView;
    private Button addToCartButton;

    private CategoryModel categoryModel;
    private int quantity = 1;

    private FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = auth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        bottomNavigationView =  findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.logout) {
                    auth.signOut();
                    startActivity(new Intent(DetailedActivity.this, LoginActivity.class));
                    overridePendingTransition(0,0);
                    Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else if (itemId == R.id.cart) {
                    startActivity(new Intent(DetailedActivity.this, CartActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(DetailedActivity.this, Home.class));
                    overridePendingTransition(0,0);
                    return true;
                };
                return false;
         }
});


        categoryNameTextView = findViewById(R.id.detailName);
        categoryImageView = findViewById(R.id.detailImage);
        categoryTypeTextView = findViewById(R.id.detailType);
        categoryPriceTextView = findViewById(R.id.detailPrice);
        quantityTextView = findViewById(R.id.cartQuantity2);
        addToCartButton = findViewById(R.id.addToCart);


        if (getIntent() != null && getIntent().hasExtra("categoryModel")) {
            categoryModel = getIntent().getParcelableExtra("categoryModel", CategoryModel.class);
            if (categoryModel != null) {

                categoryNameTextView.setText(categoryModel.getName());
                Glide.with(this).load(categoryModel.getImg_url()).into(categoryImageView);
                categoryTypeTextView.setText("Description: " + categoryModel.getType());
                categoryPriceTextView.setText("Price: $" + categoryModel.getPrice());


                quantityTextView.setText(String.valueOf(quantity));


                ImageView addMoreImageView = findViewById(R.id.cartAddMore2);
                addMoreImageView.setOnClickListener(v -> {
                    quantity++;
                    quantityTextView.setText(String.valueOf(quantity));
                    updateTotalPrice();
                });


                ImageView reduceImageView = findViewById(R.id.cartReduce2);
                reduceImageView.setOnClickListener(v -> {
                    if (quantity > 1) {
                        quantity--;
                        quantityTextView.setText(String.valueOf(quantity));
                        updateTotalPrice();
                    }
                });


                addToCartButton.setOnClickListener(v -> {
                    addToCart();
                });
            }
        }
    }

    private void updateTotalPrice() {
        double totalPrice = categoryModel.getPrice() * quantity;
        categoryPriceTextView.setText("Total Price: $" + totalPrice);
    }


    private void addToCart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("cart")
                .whereEqualTo("name", categoryModel.getName())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        QueryDocumentSnapshot cartItemSnapshot = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(0);
                        String cartItemId = cartItemSnapshot.getId();
                        int existingQuantity = cartItemSnapshot.getLong("quantity").intValue();
                        double existingPrice = cartItemSnapshot.getDouble("price");


                        int newQuantity = existingQuantity + quantity;
                        double newTotalPrice = existingPrice + (categoryModel.getPrice() * quantity);


                        DocumentReference cartItemRef = db.collection("users")
                                .document(userId)
                                .collection("cart")
                                .document(cartItemId);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("quantity", newQuantity);
                        updates.put("price", newTotalPrice);

                        cartItemRef.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Item quantity updated in cart!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DetailedActivity.this, Home.class));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Failed to update item in cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {

                        addNewItemToCart();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Add new item to cart in Firestore
    private void addNewItemToCart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        // Create a new document in "cart" collection with product details
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("name", categoryModel.getName());
        cartItem.put("img_url", categoryModel.getImg_url());
        cartItem.put("type", categoryModel.getType());
        cartItem.put("price", categoryModel.getPrice() * quantity);
        cartItem.put("unitPrice", categoryModel.getPrice());
        cartItem.put("quantity", quantity);

        // Add document to Firestore under user's cart collection
        db.collection("users")
                .document(userId)
                .collection("cart")
                .add(cartItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailedActivity.this, Home.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add item to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}