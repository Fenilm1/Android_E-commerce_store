package com.example.project2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckoutActivity extends AppCompatActivity {

    private EditText nameEditText, addressEditText, phoneEditText, cardNumberEditText, expiryDateEditText, cvvEditText;
    private Button thankYouBtn;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        auth = FirebaseAuth.getInstance();

        bottomNavigationView =  findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.logout) {
                    auth.signOut();
                    startActivity(new Intent(CheckoutActivity.this, LoginActivity.class));
                    overridePendingTransition(0,0);
                    Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                } else if (itemId == R.id.cart) {
                    startActivity(new Intent(CheckoutActivity.this, CartActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                } else if (itemId == R.id.home) {
                    startActivity(new Intent(CheckoutActivity.this, Home.class));
                    overridePendingTransition(0,0);
                    return true;
                };
                return false;
            }
        });

        nameEditText = findViewById(R.id.nameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        expiryDateEditText = findViewById(R.id.expiryDateEditText);
        cvvEditText = findViewById(R.id.cvvEditText);
        thankYouBtn = findViewById(R.id.thankYouBtn);

        thankYouBtn.setOnClickListener(v -> {
            if (validateFields()) {
                startActivity(new Intent(CheckoutActivity.this, ThankYouActivity.class));

                clearCart();
            }
        });
    }

    private boolean validateFields() {
        String name = nameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String cardNumber = cardNumberEditText.getText().toString().trim();
        String expiryDate = expiryDateEditText.getText().toString().trim();
        String cvv = cvvEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(expiryDate) || TextUtils.isEmpty(cvv)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            Toast.makeText(this, "Invalid name. Use only letters and spaces", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address.length() < 5) {
            Toast.makeText(this, "Address is too short", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidCreditCardNumber(cardNumber)) {
            Toast.makeText(this, "Invalid card number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidExpiryDate(expiryDate)) {
            Toast.makeText(this, "Invalid expiry date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidCVV(cvv)) {
            Toast.makeText(this, "Invalid CVV", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }

    private boolean isValidCreditCardNumber(String cardNumber) {
        return cardNumber.length() >= 12 && cardNumber.length() <= 19 && cardNumber.matches("[0-9]+");
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (!expiryDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            return false; // Invalid format
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        sdf.setLenient(false); // Disable lenient parsing (strict mode)

        try {
            Date parsedExpiryDate = sdf.parse(expiryDate);

            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.DAY_OF_MONTH, 1);
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);

            if (parsedExpiryDate != null) {
                Calendar expiryCalendar = Calendar.getInstance();
                expiryCalendar.setTime(parsedExpiryDate);

                return !expiryCalendar.before(currentDate);
            } else {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidCVV(String cvv) {
        return cvv.length() == 3 || cvv.length() == 4;
    }

    private void clearCart() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference cartRef = db.collection("users").document(userId).collection("cart");

        WriteBatch batch = db.batch();

        cartRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                DocumentReference cartItemRef = documentSnapshot.getReference();

                batch.delete(cartItemRef);
            }

            batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cart cleared successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}