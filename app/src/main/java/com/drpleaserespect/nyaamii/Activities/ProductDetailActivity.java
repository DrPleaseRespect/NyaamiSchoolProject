package com.drpleaserespect.nyaamii.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.drpleaserespect.nyaamii.DataObjects.StoreItem;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {

    private SharedPreferences sharedPref = null;
    private static final String TAG = "ProductDetailActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_product_detail);

        // Get the StoreItem object from the intent
        StoreItem item = getIntent().getParcelableExtra("StoreItem", StoreItem.class);

        // Get Shared Preferences
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);

        // Set Back Button
        findViewById(id.BackButton).setOnClickListener(v -> finish());

        // Get Loading Layout
        ConstraintLayout loadinglayout = findViewById(id.LoadingLayout);


        // Set Item Data
        ((TextView) findViewById(id.ItemName)).setText(item.getName());
        ((TextView) findViewById(id.ItemPrice)).setText(item.getPriceString());
        ((TextView) findViewById(id.ItemDescription)).setText(item.getDescription());
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
                .into((ImageView) findViewById(id.ItemImage));

        // Cart Button
        ImageView CartButton = findViewById(id.CartButton);
        CartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });

        // Add To Cart Button
        Button AddToCartButton = findViewById(id.AddToCartButton);
        AddToCartButton.setOnClickListener(v -> {
            // Get User
            String user = sharedPref.getString("User", "DrPleaseRespect");
            // Grab Firebase Firestore Instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Grab the User's Document
            AddToCart(item, user, db);
        });

        // Buy Now Button
        Button BuyNowButton = findViewById(id.BuyNowButton);
        BuyNowButton.setOnClickListener(v -> {
            // Get User
            String user = sharedPref.getString("User", "DrPleaseRespect");
            // Grab Firebase Firestore Instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Grab the User's Document
            AddToCart(item, user, db, true);
        });



        // Search Bar Functionality
        EditText SearchBar = findViewById(id.SearchBar);
        SearchBar.setImeOptions(EditorInfo.IME_ACTION_DONE);
        SearchBar.setImeActionLabel("Search", EditorInfo.IME_ACTION_DONE);
        SearchBar.setOnClickListener(v -> SearchBar.setCursorVisible(true));
        SearchBar.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "Action ID: " + actionId);
            Log.d(TAG, "Event: " + event);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.setCursorVisible(false);
                Intent intent = new Intent(this, CategoryActivity.class);
                intent.putExtra("SearchQuery", v.getText().toString());
                intent.putExtra("Category", "");
                startActivity(intent);
            }

            return false;
        });
    }
    private void AddToCart(StoreItem item, String user, FirebaseFirestore db) {
        AddToCart(item, user, db, false);
    }
    private void AddToCart(StoreItem item, String user, FirebaseFirestore db, boolean BuyNow) {
        db.collection("UserData").whereEqualTo("Username", user).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String docid = task.getResult().getDocuments().get(0).getId();
                // Get Item Reference
                db.collectionGroup("Items").whereEqualTo(FieldPath.documentId(), item.getDocumentPath()).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        DocumentReference itemRef = task1.getResult().getDocuments().get(0).getReference();
                        // Check if Item is already in Cart
                        db.collection("UserData").document(docid).collection("Cart").whereEqualTo("Item", itemRef).get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) if (task2.getResult().size() > 0) {
                                // Item is already in Cart
                                DocumentSnapshot doc = task2.getResult().getDocuments().get(0);
                                int quantity = doc.getLong("Quantity").intValue();
                                db.collection("UserData").document(docid).collection("Cart").document(item.getId()).update("Quantity", quantity + 1);
                                Toast.makeText(this, getString(string.AlreadyInCartText), Toast.LENGTH_SHORT).show();
                            } else {
                                // Add Item to Cart
                                Map<String, Object> cartItem = new HashMap<>();
                                cartItem.put("Item", itemRef);
                                cartItem.put("Quantity", 1);
                                db.collection("UserData").document(docid).collection("Cart").document(item.getId()).set(cartItem);
                                Toast.makeText(this, getString(string.AddedToCartText), Toast.LENGTH_SHORT).show();
                            }
                            if (BuyNow) {
                                Intent intent = new Intent(this, TransactionActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        });
    }

}