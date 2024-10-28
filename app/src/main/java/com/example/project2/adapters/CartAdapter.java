package com.example.project2.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2.R;
import com.example.project2.models.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    private Context context;
    private List<CartModel> cartModels;
    private FirebaseFirestore db;

    private double unitPrice;

    public CartAdapter(Context context, List<CartModel> cartModels) {
        this.context = context;
        this.cartModels = cartModels;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel cartModel = cartModels.get(position);


        holder.bindCartItem(cartModel);


        holder.cartAddMoreImageView.setOnClickListener(v -> {
            System.out.println("Hello 00");
            updateQuantity(cartModel, cartModel.getQuantity() + 1);
            updateTotalAmount();
        });

        holder.cartReduceImageView.setOnClickListener(v -> {
            if (cartModel.getQuantity() > 1) {
                System.out.println("Hello XX");
                updateQuantity(cartModel, cartModel.getQuantity() - 1);
                updateTotalAmount();
            }
        });

        holder.removeItemButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {

                removeItem(adapterPosition);
                updateTotalAmount();
            }
        });
    }

    private void updateQuantity(CartModel cartModel, int newQuantity) {

        double unitPrice = cartModel.getUnitPrice();
        double totalPrice = unitPrice * newQuantity;

        cartModel.setQuantity(newQuantity);
        cartModel.setPrice(totalPrice);

        DocumentReference cartItemRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("cart")
                .document(cartModel.getItemId());

        Map<String, Object> updates = new HashMap<>();
        updates.put("quantity", newQuantity);
        updates.put("totalPrice", totalPrice);

        cartItemRef.update(updates)
                .addOnSuccessListener(aVoid -> {

                    notifyItemChanged(cartModels.indexOf(cartModel));


                })
                .addOnFailureListener(e -> {

                    cartModel.setQuantity(cartModel.getQuantity());

                    notifyItemChanged(cartModels.indexOf(cartModel));
                    Toast.makeText(context, "Failed to update cart item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return cartModels.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartNameTextView, cartPriceTextView, cartQuantityTextView;
        ImageView cartImageView, cartAddMoreImageView, cartReduceImageView;

        Button removeItemButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cartNameTextView = itemView.findViewById(R.id.cartName);
            cartPriceTextView = itemView.findViewById(R.id.cartPrice);
            cartQuantityTextView = itemView.findViewById(R.id.cartQuantity2);
            cartImageView = itemView.findViewById(R.id.cartImg);
            cartAddMoreImageView = itemView.findViewById(R.id.cartAddMore);
            cartReduceImageView = itemView.findViewById(R.id.cartReduce);
            removeItemButton = itemView.findViewById(R.id.remove);
        }

        public void bindCartItem(CartModel cartItem) {
            cartNameTextView.setText(cartItem.getName());
            cartPriceTextView.setText(String.format(Locale.US, "$%.2f", cartItem.getPrice()));
            cartQuantityTextView.setText(String.valueOf(cartItem.getQuantity()));
            Glide.with(itemView).load(cartItem.getImgUrl()).into(cartImageView);
        }
    }

    private void updateTotalAmount() {
        int totalAmount = 0;
        for (CartModel item : cartModels) {
            totalAmount += item.getPrice();
        }

        Intent intent = new Intent("My Total Amount");
        intent.putExtra("Total Amount", totalAmount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void removeItem(int position) {
        CartModel cartModel = cartModels.get(position);


        cartModels.remove(position);
        notifyItemRemoved(position);


        DocumentReference cartItemRef = db.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("cart")
                .document(cartModel.getItemId());

        cartItemRef.delete()
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(context, "Failed to remove item: " + e.getMessage(), Toast.LENGTH_SHORT).show();


                    cartModels.add(position, cartModel);
                    notifyItemInserted(position);
                });
    }
}
