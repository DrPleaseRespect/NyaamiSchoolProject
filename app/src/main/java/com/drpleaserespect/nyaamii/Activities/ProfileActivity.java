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

import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.OrderItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class ProfileActivity extends AppCompatActivity implements OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPref = null;
    private Disposable HistoryListener = null;
    private final String TAG = "ProfileActivity";

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
                        Log.d(TAG, "onCreate: " + orders);
                    }
                    ordersViewModel.postOrders(orders);
                }, throwable -> {
                    Log.e(TAG, "Order History Data: " + throwable.getMessage());
                    throw throwable;
                });




        // For back button use finish()
        LinearLayout back = findViewById(id.CartButtonLinearLayout);
        back.setOnClickListener(v -> finish());

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