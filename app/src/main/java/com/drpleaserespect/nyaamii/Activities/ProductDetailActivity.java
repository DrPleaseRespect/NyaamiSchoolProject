package com.drpleaserespect.nyaamii.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.drpleaserespect.nyaamii.DataObjects.StoreItem;
import com.drpleaserespect.nyaamii.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private SharedPreferences sharedPref = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Get the StoreItem object from the intent
        StoreItem item = getIntent().getParcelableExtra("StoreItem", StoreItem.class);

        // Get Shared Preferences
        sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);

        // Set Back Button
        findViewById(R.id.BackButton).setOnClickListener(v -> finish());

        // Get Loading Layout
        ConstraintLayout loadinglayout = findViewById(R.id.LoadingLayout);


        // Set Item Data
        ((TextView) findViewById(R.id.ItemName)).setText(item.getName());
        ((TextView) findViewById(R.id.ItemPrice)).setText(item.getPriceString());
        ((TextView) findViewById(R.id.ItemDescription)).setText(item.getDescription());
        Glide.with(this)
                .load(item.getImageUrl())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        loadinglayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into((ImageView) findViewById(R.id.ItemImage));

        // Cart Button
        ImageView CartButton = findViewById(R.id.CartButton);
        CartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        // Add To Cart Button
        Button AddToCartButton = findViewById(R.id.AddToCartButton);
        AddToCartButton.setOnClickListener(v -> {
            // Get User
            String user = sharedPref.getString("User", "DrPleaseRespect");
            // Grab Firebase Firestore Instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Grab the User's Document
            db.collection("UserData").whereEqualTo("Username", user).limit(1).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String docid = task.getResult().getDocuments().get(0).getId();
                    // Get Item Reference
                    db.collectionGroup("Items").whereEqualTo(FieldPath.documentId(), item.getDocumentPath()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentReference itemRef = task1.getResult().getDocuments().get(0).getReference();
                            // Check if Item is already in Cart
                            db.collection("UserData").document(docid).collection("Cart").whereEqualTo("Item", itemRef).get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    if (task2.getResult().size() > 0) {
                                        // Item is already in Cart
                                        DocumentSnapshot doc = task2.getResult().getDocuments().get(0);
                                        int quantity = doc.getLong("Quantity").intValue();
                                        db.collection("UserData").document(docid).collection("Cart").document(item.getId()).update("Quantity", quantity + 1);
                                    } else {
                                        // Add Item to Cart
                                        Map<String, Object> cartItem = new HashMap<>();
                                        cartItem.put("Item", itemRef);
                                        cartItem.put("Quantity", 1);
                                        db.collection("UserData").document(docid).collection("Cart").document(item.getId()).set(cartItem);
                                    }
                                }
                            });
                            // Add Item to Cart
                            //Map<String, Object> cartItem = new HashMap<>();
                            //cartItem.put("Item", itemRef);
                            //cartItem.put("Quantity", 1);
                            //db.collection("UserData").document(docid).collection("Cart").add(cartItem);
                        }
                    });
                }
            });
        });
    }

}