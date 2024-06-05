package com.drpleaserespect.nyaamii.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.drpleaserespect.nyaamii.DataObjects.OrderObject;
import com.drpleaserespect.nyaamii.DataObjects.StoreItem;
import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.CrossRefs.UserHistoryCrossRef;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactionActivity extends AppCompatActivity {

    private String TAG = "TransactionActivity";
    private Disposable CartListener;
    private CompositeDisposable mDisposable = new CompositeDisposable();
    private SharedPreferences sharedPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_transaction);

        // Get the ViewModel
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        // Get the Shared Preferences
        sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);


        // DB
        NyaamiDatabase db = NyaamiDatabase.getInstance(this);
        UserDao userDao = db.userDao();

        // Obtain the Cart Data
        CartListener =  userDao.watchCart(sharedPref.getString("User", "DrPleaseRespect"))
                        .subscribeOn(Schedulers.io())
                        .subscribe(orders -> {
                            ordersViewModel.postOrders(orders);
                        });

        // Grab all the orders/data from the database
        //db.collection("UserData").whereEqualTo("Username", sharedPref.getString("User", "DrPleaseRespect")).limit(1).get().addOnCompleteListener(task -> {
        //    if (task.isSuccessful()) {
        //        String docid = task.getResult().getDocuments().get(0).getId();
        //        if (CartListener != null) CartListener.remove();
        //        CartListener = db.collection("UserData").document(docid).collection("Cart").addSnapshotListener((value, error) -> {
        //            if (error != null) return;
        //            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        //            List<OrderObject> orders = new ArrayList<>();
        //            for (DocumentSnapshot doc : value.getDocuments()) {
        //                DocumentReference docref = doc.getDocumentReference("Item");
        //                if (docref == null) continue;
        //                Task<DocumentSnapshot> doctask = docref.get();
        //
        //
        //                doctask.addOnCompleteListener(task1 -> {
        //                    if (task1.isSuccessful()) {
        //                        DocumentSnapshot itemDoc = task1.getResult();
        //                        StoreItem storeitem = new StoreItem(itemDoc);
        //                        OrderObject order = new OrderObject(
        //                                storeitem,
        //                                Objects.requireNonNull(doc.getLong("Quantity")).intValue(),
        //                                doc.getId());
        //
        //
        //                        orders.add(order);
        //                    }
        //                });
        //                tasks.add(doctask);
        //            }
        //            Tasks.whenAllComplete(tasks).addOnCompleteListener(task1 -> {
        //                ordersViewModel.setOrders(orders);
        //            });
        //        });
        //        db.collection("UserData").document(docid).get().addOnCompleteListener(task1 -> {
        //            if (task1.isSuccessful()) {
        //                DocumentSnapshot doc = task1.getResult();
        //                ((TextView) findViewById(R.id.ShippingName)).setText(doc.getString("Name"));
        //                ((TextView) findViewById(R.id.ShippingAddress)).setText(doc.getString("Address"));
        //                ((TextView) findViewById(R.id.ShippingNumber)).setText(doc.getString("Number"));
        //            }
        //        });
        //    }
        //});

        // Back Button
        findViewById(R.id.BackButtonLinearLayout).setOnClickListener(v -> {
            finish();
        });



        // Checkout Button
        findViewById(R.id.CheckoutButton).setOnClickListener(v -> {
            // Save the order to history
            //db.collection("UserData").whereEqualTo("Username", sharedPref.getString("User", "DrPleaseRespect")).limit(1).get().addOnCompleteListener(task -> {
            //    if (task.isSuccessful()) {
            //        String docid = task.getResult().getDocuments().get(0).getId();
            //        db.collection("UserData").document(docid).collection("Cart").get().addOnCompleteListener(task1 -> {
            //            if (task1.isSuccessful()) {
            //                for (DocumentSnapshot doc : task1.getResult().getDocuments()) {
            //                    try {
            //                        Map<String, Object> map = new HashMap<>();
            //                        map.put("Item", doc.getDocumentReference("Item"));
            //                        db.collection("UserData")
            //                                .document(docid)
            //                                .collection("History")
            //                                .document("Order" + System.currentTimeMillis())
            //                                .set(map);
            //                        doc.getReference().delete();
            //
            //                    } catch (NullPointerException e) {
            //
            //                    }
            //                }
            //            }
            //        });
            //    }
            //});

            // Save the cart to the History
            List<OrderWithItem> history_values = ordersViewModel.getOrders().getValue();
            for (OrderWithItem order : history_values) {
                mDisposable.add(userDao.insertHistory(
                        new UserHistoryCrossRef(
                                sharedPref.getString("User", "DrPleaseRespect"),
                                order.item.ItemID)
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io()).subscribe(() -> {
                    mDisposable.add(userDao.deleteOrder(order.order)
                            .subscribeOn(Schedulers.io()).
                            observeOn(Schedulers.io()).
                            subscribe(() -> {
                        //Toast.makeText(this, getString(R.string.AddedToCartText), Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                                Log.e(TAG, "Error: " + throwable.getMessage());
                            }));
                }, throwable -> {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                        }));

            }

            Intent intent = new Intent(this, SuccessfulOrder.class);
            startActivity(intent);

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CartListener.dispose();
    }
}