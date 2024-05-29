package com.drpleaserespect.nyaamii.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.drpleaserespect.nyaamii.DataObjects.OrderObject;
import com.drpleaserespect.nyaamii.DataObjects.StoreItem;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {


    private static final String TAG = "CartActivity";
    private ListenerRegistration CartListener;
    private SharedPreferences sharedPref = null;


    private void SaveCart(FirebaseFirestore db, List<OrderObject> orders) {
        // Save the cart to the database
        db.collection("UserData").whereEqualTo("Username", sharedPref.getString("User", "DrPleaseRespect")).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String docid = task.getResult().getDocuments().get(0).getId();
                CollectionReference cart = db.collection("UserData").document(docid).collection("Cart");
                cart.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) for (DocumentSnapshot doc : task1.getResult()) {
                        if (orders.isEmpty()) {
                            cart.document(doc.getId()).delete();
                            continue;
                        }
                        boolean found = false;
                        for (OrderObject order : orders)
                            if (order.getDocumentId().equals(doc.getId())) {
                                Log.d(TAG, "SaveCart: " + order.getDocumentId() + " != " + doc.getId());
                                found = true;
                                cart.document(doc.getId()).update("Quantity", order.getQuantity());
                            }
                        if (!found) {
                            Log.d(TAG, "SaveCart: " + "Deleting Item" + ' ' + doc.getId());
                            cart.document(doc.getId()).delete();
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_cart);

        // Get the ViewModel
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        // Get the Shared Preferences
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);


        // Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("UserData").whereEqualTo("Username", sharedPref.getString("User", "DrPleaseRespect")).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String docid = task.getResult().getDocuments().get(0).getId();
                if (CartListener != null) CartListener.remove();
                CartListener = db.collection("UserData").document(docid).collection("Cart").addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    List<OrderObject> orders = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        DocumentReference docref = doc.getDocumentReference("Item");
                        if (docref == null) continue;
                        Task<DocumentSnapshot> doctask = docref.get();


                        doctask.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot itemDoc = task1.getResult();
                                StoreItem storeitem = new StoreItem(itemDoc);
                                OrderObject order = new OrderObject(
                                        storeitem,
                                        Objects.requireNonNull(doc.getLong("Quantity")).intValue(),
                                        doc.getId());


                                orders.add(order);
                            }
                        });
                        tasks.add(doctask);
                    }
                    Tasks.whenAllComplete(tasks).addOnCompleteListener(task1 -> {
                        ordersViewModel.setOrders(orders);
                    });
                });
            }
        });

        // Back Button Functionality
        findViewById(id.CartButtonLinearLayout).setOnClickListener(v -> {
            boolean verified = false;

            SaveCart(db, ordersViewModel.getOrders().getValue());
            finish();
        });

        // Text Animation
        TextView totalNum = findViewById(id.TotalNumber);
        totalNum.setAlpha(0.0f);
        ViewPropertyAnimator animator = totalNum.animate().alpha(1.0f).setDuration(500).setStartDelay(500);

        // Total Price Calculation
        ordersViewModel.getOrders().observe(this, orders -> {
            float total = 0;
            for (OrderObject order : orders)
                total += order.getItem().getPrice() * order.getQuantity();

            String CurrencySuffix = StoreItem.getCurrencySuffix();
            totalNum
                    .setText(
                            String.format(
                                    Locale.getDefault(),
                                    "%.2f %s",
                                    total,
                                    CurrencySuffix
                            )
                    );
            if (totalNum.getAlpha() == 0.0f) animator.start();
        });

        // Checkout Button Functionality
        findViewById(id.CheckoutButton).setOnClickListener(v -> {
            // Save the cart to the database
            SaveCart(db, ordersViewModel.getOrders().getValue());
            // Create Intent
            Intent intent = new Intent(this, TransactionActivity.class);
            // Start the transaction activity
            startActivity(intent);
        });

    }
}