package com.drpleaserespect.nyaamii.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.UserWithHistory;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProfileActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPref = null;
    private Disposable HistoryListener = null;
    private String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_profile_page);
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        SetData(sharedPref);

        // Get the ViewModel
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);
        ordersViewModel.setCartMode(false);

        // Obtain the Order History Data
        NyaamiDatabase db = NyaamiDatabase.getInstance(this);
        HistoryListener = db.userDao()
                .watchUserWithHistory("DrPleaseRespect")
                .subscribe(historyObj -> {
            List<OrderWithItem> orders = new ArrayList<>();
            for (StoreItem order : historyObj.history) {
                OrderWithItem orderObject = new OrderWithItem(
                        new OrderItem(order.ItemID, 1, historyObj.user.getUserName()),
                        order

                );
                orders.add(orderObject);
                Log.d(TAG, "onCreate: " + orders.toString());
            }
            ordersViewModel.postOrders(orders);
        }, throwable -> {
                    Log.e(TAG, "Order History Data: " + throwable.getMessage());
                    throw throwable;
                });


        // Obtain the Order History Data
        //FirebaseFirestore db = FirebaseFirestore.getInstance();
        //db.collection("UserData").whereEqualTo("Username", sharedPref.getString("User", "DrPleaseRespect")).limit(1).get().addOnCompleteListener(task -> {
        //    if (task.isSuccessful()) {
        //        String docid = task.getResult().getDocuments().get(0).getId();
        //        if (HistoryListener != null) HistoryListener.remove();
        //        HistoryListener = db.collection("UserData").document(docid).collection("History").orderBy(FieldPath.documentId()).addSnapshotListener((value, error) -> {
        //            if (error != null) return;
        //            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        //            List<OrderObject> orders = new ArrayList<>();
        //
        //            for (DocumentSnapshot doc : value.getDocuments()) {
        //                DocumentReference docref = doc.getDocumentReference("Item");
        //                if (docref == null) continue;
        //                Task<DocumentSnapshot> doctask = docref.get();
        //
        //                doctask.addOnCompleteListener(task1 -> {
        //                    if (task1.isSuccessful()) {
        //                        DocumentSnapshot itemDoc = task1.getResult();
        //                        StoreItem storeitem = new StoreItem(itemDoc);
        //                        OrderObject order = new OrderObject(
        //                                storeitem,
        //                                1,
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
        //    }
        //});


        // For back button use finish()
        LinearLayout back = findViewById(id.CartButtonLinearLayout);
        back.setOnClickListener(v -> finish());

        Button DebugButton = findViewById(id.DebugButton);
        DebugButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DebuggingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        SetData(sharedPreferences);
    }

    private void SetData(SharedPreferences sharedPreferences) {
        TextView NameView = findViewById(id.ProfileNameText);
        TextView EmailView = findViewById(id.ProfileEmailText);

        String User = sharedPreferences.getString("User", "Null");
        String Email = sharedPreferences.getString("Email", "Null");

        // Set the user's data
        NameView.setText(User);
        EmailView.setText(Email);
    }
}