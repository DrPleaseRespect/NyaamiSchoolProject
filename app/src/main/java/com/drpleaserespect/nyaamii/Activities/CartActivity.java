package com.drpleaserespect.nyaamii.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.drpleaserespect.nyaamii.Database.DAOs.UserDao;
import com.drpleaserespect.nyaamii.Database.DataEntites.DataClasses.OrderWithItem;
import com.drpleaserespect.nyaamii.Database.DataEntites.StoreItem;
import com.drpleaserespect.nyaamii.Database.NyaamiDatabase;
import com.drpleaserespect.nyaamii.R.id;
import com.drpleaserespect.nyaamii.R.layout;
import com.drpleaserespect.nyaamii.R.string;
import com.drpleaserespect.nyaamii.ViewModels.OrdersViewModel;

import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {


    private static final String TAG = "CartActivity";
    private Disposable CartListener;
    private SharedPreferences sharedPref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_cart);

        // Get the ViewModel
        OrdersViewModel ordersViewModel = new ViewModelProvider(this).get(OrdersViewModel.class);

        // Get the Shared Preferences
        sharedPref = getSharedPreferences(getString(string.ProfileState), MODE_PRIVATE);


        // DB
        NyaamiDatabase db = NyaamiDatabase.getInstance(this);
        UserDao userDao = db.userDao();
        // Cart Listener
        CartListener = userDao.watchCart(sharedPref.getString("User", "DrPleaseRespect"))
                .subscribeOn(Schedulers.io())
                .subscribe(orders -> {
                            ordersViewModel.postOrders(orders);
                        }
                );

        // Back Button Functionality
        findViewById(id.CartButtonLinearLayout).setOnClickListener(v -> {
            boolean verified = false;

            finish();
        });

        // Text Animation
        TextView totalNum = findViewById(id.TotalNumber);
        totalNum.setAlpha(0.0f);
        ViewPropertyAnimator animator = totalNum.animate().alpha(1.0f).setDuration(500).setStartDelay(500);

        // Total Price Calculation
        ordersViewModel.getOrders().observe(this, orders -> {
            float total = 0;
            for (OrderWithItem order_obj : orders) {
                total += order_obj.item.getPrice() * order_obj.order.OrderQuantity;
            }

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
            if (totalNum.getAlpha() == 0.0f) {
                animator.start();
            }
        });

        // Checkout Button Functionality
        findViewById(id.CheckoutButton).setOnClickListener(v -> {
            // Create Intent
            Intent intent = new Intent(this, TransactionActivity.class);
            // Start the transaction activity
            startActivity(intent);
        });

    }
}